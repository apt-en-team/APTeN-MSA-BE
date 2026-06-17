#!/bin/bash
# Outbox CDC 커넥터를 Kafka Connect REST API로 등록한다.
# 사전 조건: docker-compose up으로 kafka-connect 컨테이너가 RUNNING 상태여야 한다.
#
# 사용법:
#   ./debezium/register-connector.sh                    # 전체 서비스 등록
#   ./debezium/register-connector.sh facility           # 특정 서비스만 등록
#   ./debezium/register-connector.sh auth complex       # 여러 서비스 선택 등록
#
# 지원 서비스: facility, auth, complex, board, household, parking

set -e

CONNECT_URL="http://localhost:8083"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/../.env"

# .env에서 민감 정보를 읽는다 (이미 환경변수에 있으면 .env보다 우선)
load_env_var() {
  local key="$1"
  local current_val
  current_val=$(eval echo "\${${key}}")
  if [ -z "${current_val}" ] && [ -f "${ENV_FILE}" ]; then
    local val
    val=$(grep "^${key}=" "${ENV_FILE}" | cut -d'=' -f2-)
    eval "export ${key}='${val}'"
  fi
}

load_env_var DEBEZIUM_DB_HOST
load_env_var DEBEZIUM_DB_PORT
load_env_var DEBEZIUM_DB_USER
load_env_var DEBEZIUM_DB_PASSWORD

: "${DEBEZIUM_DB_HOST:?DEBEZIUM_DB_HOST가 .env에 없거나 비어있습니다.}"
: "${DEBEZIUM_DB_PORT:?DEBEZIUM_DB_PORT가 .env에 없거나 비어있습니다.}"
: "${DEBEZIUM_DB_USER:?DEBEZIUM_DB_USER가 .env에 없거나 비어있습니다.}"
: "${DEBEZIUM_DB_PASSWORD:?DEBEZIUM_DB_PASSWORD가 .env에 없거나 비어있습니다.}"

# 서비스명 → 커넥터 파일 / 커넥터 이름 매핑
declare -A CONNECTOR_FILES=(
  [facility]="${SCRIPT_DIR}/connectors/facility-reservation-connector.json"
  [auth]="${SCRIPT_DIR}/connectors/auth-outbox-connector.json"
  [complex]="${SCRIPT_DIR}/connectors/apartment-complex-outbox-connector.json"
  [board]="${SCRIPT_DIR}/connectors/board-outbox-connector.json"
  [household]="${SCRIPT_DIR}/connectors/household-outbox-connector.json"
  [parking]="${SCRIPT_DIR}/connectors/parking-vehicle-outbox-connector.json"
)

declare -A CONNECTOR_NAMES=(
  [facility]="facility-reservation-outbox-connector"
  [auth]="auth-outbox-connector"
  [complex]="apartment-complex-outbox-connector"
  [board]="board-outbox-connector"
  [household]="household-outbox-connector"
  [parking]="parking-vehicle-outbox-connector"
)

# 인자가 없으면 전체 서비스 등록
if [ $# -eq 0 ]; then
  SERVICES=("facility" "auth" "complex" "board" "household" "parking")
else
  SERVICES=("$@")
fi

# 유효하지 않은 서비스명 검사
for svc in "${SERVICES[@]}"; do
  if [ -z "${CONNECTOR_FILES[$svc]+_}" ]; then
    echo "오류: 알 수 없는 서비스 이름 '${svc}'"
    echo "지원 서비스: facility, auth, complex, board, household, parking"
    exit 1
  fi
done

echo "=============================="
echo " Outbox CDC 커넥터 등록 시작"
echo " 대상: ${SERVICES[*]}"
echo "=============================="

# Kafka Connect가 준비될 때까지 대기
echo "[준비] Kafka Connect 응답 대기 중..."
until curl -sf "${CONNECT_URL}/connectors" > /dev/null; do
  printf "  ."
  sleep 3
done
echo ""
echo "  Kafka Connect 준비 완료."
echo ""

# 서비스별 커넥터 등록
register_connector() {
  local svc="$1"
  local connector_name="${CONNECTOR_NAMES[$svc]}"
  local connector_file="${CONNECTOR_FILES[$svc]}"

  echo "-------------------------------"
  echo " 서비스: ${svc}  →  ${connector_name}"
  echo "-------------------------------"

  # 기존 커넥터 삭제 (재등록 시 충돌 방지)
  if curl -sf -X DELETE "${CONNECT_URL}/connectors/${connector_name}" > /dev/null 2>&1; then
    echo "  기존 커넥터 삭제 완료."
    sleep 1
  else
    echo "  기존 커넥터 없음 (정상)."
  fi

  # 환경변수 치환 후 커넥터 등록
  sed \
    -e "s|\${DEBEZIUM_DB_HOST}|${DEBEZIUM_DB_HOST}|g" \
    -e "s|\${DEBEZIUM_DB_PORT}|${DEBEZIUM_DB_PORT}|g" \
    -e "s|\${DEBEZIUM_DB_USER}|${DEBEZIUM_DB_USER}|g" \
    -e "s|\${DEBEZIUM_DB_PASSWORD}|${DEBEZIUM_DB_PASSWORD}|g" \
    "${connector_file}" | \
  curl -sf -X POST "${CONNECT_URL}/connectors" \
    -H "Content-Type: application/json" \
    -d @- > /dev/null
  echo "  커넥터 등록 완료."

  # 상태 확인
  sleep 3
  STATUS=$(curl -sf "${CONNECT_URL}/connectors/${connector_name}/status")
  CONNECTOR_STATE=$(echo "${STATUS}" | grep -o '"state":"[^"]*"' | head -1 | cut -d'"' -f4)
  if [ "${CONNECTOR_STATE}" = "RUNNING" ]; then
    echo "  상태: RUNNING ✓"
  else
    echo "  주의: 커넥터 상태 = ${CONNECTOR_STATE}"
    echo "  로그 확인: docker logs apten-kafka-connect"
    echo "${STATUS}" | python3 -m json.tool 2>/dev/null || echo "${STATUS}"
  fi
  echo ""
}

for svc in "${SERVICES[@]}"; do
  register_connector "${svc}"
done

echo "=============================="
echo " 등록 완료: ${SERVICES[*]}"
echo "=============================="
