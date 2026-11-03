FROM node:22-alpine AS build
WORKDIR /app
COPY package.json angular.json tsconfig.json tsconfig.app.json ./
COPY src ./src
RUN npm install --no-audit --no-fund
RUN npm run build

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/chat-poc/browser /usr/share/nginx/html
EXPOSE 80
