#!/bin/bash

# Usage:
#   ./start-all.sh        # 로컬 (local-mysql :3306)
#   ./start-all.sh test   # 테스트 (test-mysql :3307)

ENV=${1:-local}

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
LOG_DIR="$PROJECT_ROOT/.logs"
mkdir -p "$LOG_DIR"

GREEN='\033[0;32m'
RED='\033[0;31m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

BACKEND_PID=""
ADMIN_PID=""
WEBAPP_PID=""

kill_tree() {
    local pid=$1
    if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
        pkill -TERM -P "$pid" 2>/dev/null
        kill -TERM "$pid" 2>/dev/null
        wait "$pid" 2>/dev/null
    fi
}

cleanup() {
    echo ""
    echo -e "${YELLOW}Shutting down all services...${NC}"
    kill_tree "$BACKEND_PID"
    kill_tree "$ADMIN_PID"
    kill_tree "$WEBAPP_PID"
    sleep 1
    pkill -TERM -P $$ 2>/dev/null
    echo -e "${GREEN}All services stopped.${NC}"
    exit 0
}

trap cleanup SIGINT SIGTERM

wait_for_pattern() {
    local pid=$1
    local log_file=$2
    local name=$3
    local success_pattern=$4
    local fail_pattern=$5

    (
        for i in $(seq 1 60); do
            if ! kill -0 "$pid" 2>/dev/null; then
                echo -e "${RED}[$name] ✗ Process exited. Check .logs/${name}.log${NC}"
                return
            fi
            if grep -q "$success_pattern" "$log_file" 2>/dev/null; then
                local url
                url=$(grep -o "http://localhost:[0-9]*" "$log_file" 2>/dev/null | head -1)
                if [ -z "$url" ] && [ -n "$6" ]; then
                    url="$6"
                fi
                if [ -n "$url" ]; then
                    echo -e "${GREEN}[$name] ✓ Started → $url${NC}"
                else
                    echo -e "${GREEN}[$name] ✓ Started${NC}"
                fi
                return
            fi
            if grep -q "$fail_pattern" "$log_file" 2>/dev/null; then
                echo -e "${RED}[$name] ✗ Failed! Check .logs/${name}.log${NC}"
                return
            fi
            sleep 1
        done
        echo -e "${RED}[$name] ✗ Timeout. Check .logs/${name}.log${NC}"
    ) &
}

start_backend() {
    echo -e "${CYAN}[backend]${NC} Starting..."
    > "$LOG_DIR/backend.log"
    (cd "$SCRIPT_DIR" && exec ./gradlew bootRun --args="--spring.profiles.active=$ENV --server.port=8081" > "$LOG_DIR/backend.log" 2>&1) &
    BACKEND_PID=$!
    wait_for_pattern "$BACKEND_PID" "$LOG_DIR/backend.log" "backend" \
        "Started .* in .* seconds" "APPLICATION FAILED TO START\|BUILD FAILED" \
        "http://localhost:8081"
}

start_admin() {
    echo -e "${CYAN}[admin]${NC}   Starting..."
    > "$LOG_DIR/admin.log"
    if [ "$ENV" = "test" ]; then
        (cd "$PROJECT_ROOT/admin" && exec npm run dev:test > "$LOG_DIR/admin.log" 2>&1) &
    else
        (cd "$PROJECT_ROOT/admin" && exec npm run dev > "$LOG_DIR/admin.log" 2>&1) &
    fi
    ADMIN_PID=$!
    wait_for_pattern "$ADMIN_PID" "$LOG_DIR/admin.log" "admin" \
        "Ready in\|Local:" "error\|ERROR"
}

start_webapp() {
    echo -e "${CYAN}[web-app]${NC} Starting..."
    > "$LOG_DIR/web-app.log"
    (cd "$PROJECT_ROOT/web-app" && exec npm run dev > "$LOG_DIR/web-app.log" 2>&1) &
    WEBAPP_PID=$!
    wait_for_pattern "$WEBAPP_PID" "$LOG_DIR/web-app.log" "web-app" \
        "Local:" "error\|ERROR"
}

PORTS_IN_USE=""
for port in 8081 3001 5173; do
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$pid" ]; then
        echo -e "${RED}Port $port is already in use (PID: $pid)${NC}"
        PORTS_IN_USE="$PORTS_IN_USE $port"
    fi
done
if [ -n "$PORTS_IN_USE" ]; then
    echo -e "${RED}Please free the above ports before starting.${NC}"
    exit 1
fi

echo -e "${CYAN}===========================================${NC}"
if [ "$ENV" = "test" ]; then
echo -e "${YELLOW}  FeedMySheep - TEST MODE (DB :3307)${NC}"
else
echo -e "${CYAN}  FeedMySheep - Starting All Services${NC}"
fi
echo -e "${CYAN}===========================================${NC}"
echo -e "${CYAN}  backend  → http://localhost:8081${NC}"
echo -e "${CYAN}  admin    → http://localhost:3001${NC}"
echo -e "${CYAN}  web-app  → http://localhost:5173${NC}"
echo -e "${CYAN}-------------------------------------------${NC}"
echo -e "${CYAN}  Logs: .logs/{backend,admin,web-app}.log${NC}"
echo -e "${CYAN}  r = restart backend  │  q = quit all${NC}"
echo -e "${CYAN}  1 = tail backend  2 = tail admin${NC}"
echo -e "${CYAN}  3 = tail web-app${NC}"
echo -e "${CYAN}===========================================${NC}"
echo ""

start_backend
start_admin
start_webapp

echo ""

while true; do
    read -rsn1 key
    case "$key" in
        r|R)
            echo -e "${YELLOW}[backend] Restarting...${NC}"
            kill_tree "$BACKEND_PID"
            start_backend
            ;;
        1)
            echo -e "${CYAN}--- backend log (press any key to stop) ---${NC}"
            tail -f "$LOG_DIR/backend.log" &
            TAIL_PID=$!; read -rsn1; kill "$TAIL_PID" 2>/dev/null
            echo -e "${CYAN}--- end ---${NC}"
            ;;
        2)
            echo -e "${CYAN}--- admin log (press any key to stop) ---${NC}"
            tail -f "$LOG_DIR/admin.log" &
            TAIL_PID=$!; read -rsn1; kill "$TAIL_PID" 2>/dev/null
            echo -e "${CYAN}--- end ---${NC}"
            ;;
        3)
            echo -e "${CYAN}--- web-app log (press any key to stop) ---${NC}"
            tail -f "$LOG_DIR/web-app.log" &
            TAIL_PID=$!; read -rsn1; kill "$TAIL_PID" 2>/dev/null
            echo -e "${CYAN}--- end ---${NC}"
            ;;
        q|Q)
            cleanup
            ;;
    esac
done
