#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p .temp-java
javac -encoding UTF-8 -d .temp-java \
  backend/src/main/java/fr/yourcaryourway/poc/chat/MessageRules.java \
  tests-offline/fr/yourcaryourway/poc/chat/MessageRulesCheck.java
java -cp .temp-java fr.yourcaryourway.poc.chat.MessageRulesCheck
rm -rf .temp-java
