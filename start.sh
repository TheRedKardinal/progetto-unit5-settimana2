#!/usr/bin/env bash
set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BE_DIR="$ROOT_DIR/BE"
FE_DIR="$ROOT_DIR/FE"

cleanup() {
  echo ""
  echo "Arresto BE e FE..."
  kill "$BE_PID" "$FE_PID" 2>/dev/null
  wait "$BE_PID" "$FE_PID" 2>/dev/null
  exit 0
}
trap cleanup INT TERM

echo "Avvio backend (Spring Boot) su http://localhost:8080 ..."
(cd "$BE_DIR" && ./mvnw spring-boot:run) &
BE_PID=$!

echo "Avvio frontend (Vite) su http://localhost:5173 ..."
(cd "$FE_DIR" && npm run dev) &
FE_PID=$!

echo ""
echo "Backend PID: $BE_PID  |  Frontend PID: $FE_PID"
echo "Premi Ctrl+C per fermare entrambi."
echo ""

wait "$BE_PID" "$FE_PID"
