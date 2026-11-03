import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnDestroy, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Client, IMessage } from '@stomp/stompjs';

interface ChatMessage {
  id: number;
  conversationId: number;
  sender: 'client' | 'conseiller';
  content: string;
  createdAt: string;
}

const api = 'http://localhost:8080';
const conversationId = 1;

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnDestroy {
  private readonly http = inject(HttpClient);
  private socket?: Client;
  role: 'client' | 'conseiller' = 'client';
  token = '';
  draft = '';
  readonly connected = signal(false);
  readonly status = signal('Hors ligne');
  readonly messages = signal<ChatMessage[]>([]);

  connect(): void {
    if (!this.token.trim()) {
      this.status.set('Saisis le jeton correspondant à ton rôle.');
      return;
    }
    this.disconnect();
    this.status.set('Connexion en cours…');
    const token = this.token.trim();
    const requestedRole = this.role;
    let authenticationRejected = false;
    const socket = new Client({
      brokerURL: api.replace(/^http/, 'ws') + '/ws',
      connectHeaders: { Authorization: `Bearer ${token}`, 'X-Demo-Role': requestedRole },
      reconnectDelay: 0,
      onConnect: () => {
        this.connected.set(true);
        this.status.set(`Connecté comme ${requestedRole}`);
        socket.subscribe(`/topic/conversations/${conversationId}`, frame => this.onMessage(frame));
        this.loadHistory(token, requestedRole);
      },
      onStompError: () => {
        authenticationRejected = true;
        this.connected.set(false);
        this.status.set('Accès refusé : le jeton ne correspond pas au profil sélectionné, ou il est invalide.');
      },
      onWebSocketClose: () => {
        this.connected.set(false);
        if (!authenticationRejected) this.status.set('Connexion fermée.');
      }
    });
    this.socket = socket;
    socket.activate();
  }

  private loadHistory(token: string, requestedRole: 'client' | 'conseiller'): void {
    this.http.get<ChatMessage[]>(`${api}/api/conversations/${conversationId}/messages`, {
      headers: new HttpHeaders({ Authorization: `Bearer ${token}`, 'X-Demo-Role': requestedRole })
    }).subscribe({
      next: history => {
        // Fusion par id pour éviter les doublons si un message arrive pendant le chargement.
        const combined = [...history, ...this.messages()];
        this.messages.set(Array.from(new Map(combined.map(item => [item.id, item])).values())
          .sort((a, b) => a.id - b.id));
      },
      error: () => this.status.set('Historique inaccessible. Vérifie la configuration.')
    });
  }

  private onMessage(frame: IMessage): void {
    try {
      const message = JSON.parse(frame.body) as ChatMessage;
      if (!Number.isInteger(message.id) || typeof message.content !== 'string') return;
      this.messages.update(items => items.some(item => item.id === message.id)
        ? items : [...items, message].sort((a, b) => a.id - b.id));
    } catch {
      this.status.set('Message illisible reçu du serveur.');
    }
  }

  send(): void {
    const content = this.draft.trim();
    if (!this.socket?.connected || !content) return;
    if ([...content].length > 1000) {
      this.status.set('Ton message ne doit pas dépasser 1 000 caractères.');
      return;
    }
    this.socket.publish({
      destination: `/app/conversations/${conversationId}/send`,
      body: JSON.stringify({ content })
    });
    this.draft = '';
  }

  disconnect(): void {
    if (this.socket) {
      void this.socket.deactivate();
      this.socket = undefined;
    }
    this.connected.set(false);
    this.messages.set([]);
    this.status.set('Hors ligne');
  }

  ngOnDestroy(): void { this.disconnect(); }
}
