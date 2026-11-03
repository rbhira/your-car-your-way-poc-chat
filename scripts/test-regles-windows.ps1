$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Push-Location $root
try {
  New-Item -ItemType Directory -Force .temp-java | Out-Null
  javac -encoding UTF-8 -d .temp-java backend/src/main/java/fr/yourcaryourway/poc/chat/MessageRules.java tests-offline/fr/yourcaryourway/poc/chat/MessageRulesCheck.java
  if ($LASTEXITCODE -ne 0) { throw "Compilation Java impossible" }
  java -Dfile.encoding=UTF-8 -cp .temp-java fr.yourcaryourway.poc.chat.MessageRulesCheck
  if ($LASTEXITCODE -ne 0) { throw "Les tests ont échoué" }
} finally {
  Remove-Item -Recurse -Force .temp-java -ErrorAction SilentlyContinue
  Pop-Location
}
