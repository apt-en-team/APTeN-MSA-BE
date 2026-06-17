#!/bin/bash
# facility-reservation-service Outbox CDC 커넥터를 Kafka Connect REST API로 등록한다.
# 사전 조건: docker-compose up으로 kafka-connect 컨테이너가 RUNNING 상태여야 한다.
# 사용법: DEBEZIUM_DB_PASSWORD=<비밀번호> ./debezium/register-connector.sh

set -e

CONNECT_URL="http://localhost:8083"
CONNECTOR_NAME="facility-reservation-outbox-connector"
CONNECTOR_FILE="$(dirname "$0")/connectors/facility-reservation-connector.json"
DB_PASSWORD="${DEBEZIUM_DB_PASSWORD:?DEBEZIUM_DB_PASSWORD 환경변수가 필요합니다.}"

echo "=============================="
echo " Outbox CDC 커넥터 등록 시작"
echo "=============================="

# Kafka Connect가 준비될 때까지 대기
echo "[1/4] Kafka Connect 응답 대기 중..."
until curl -sf "${CONNECT_URL}/connectors" > /dev/null; do
  printf "  ."
  sleep 3
done
echo ""
echo "  Kafka Connect 준비 완료."

# 기존 커넥터 삭제 (재등록 시 충돌 방지)
echo "[2/4] 기존 커넥터 삭제 시도..."
if curl -sf -X DELETE "${CONNECT_URL}/connectors/${CONNECTOR_NAME}" > /dev/null 2>&1; then
  echo "  기존 커넥터 삭제 완료."
else
  echo "  기존 커넥터 없음 (정상)."
fi
sleep 1

# DB 비밀번호를 JSON에 치환 후 커넥터 등록
echo "[3/4] 커넥터 등록 중..."
sed "s|\${DEBEZIUM_DB_PASSWORD}|${DB_PASSWORD}|g" "${CONNECTOR_FILE}" | \
  curl -sf -X POST "${CONNECT_URL}/connectors" \
    -H "Content-Type: application/json" \
    -d @- > /dev/null
echo "  커넥터 등록 완료."

# 커넥터 상태 최종 확인
echo "[4/4] 커넥터 상태 확인..."
sleep 3
STATUS=$(curl -sf "${CONNECT_URL}/connectors/${CONNECTOR_NAME}/status")
echo "${STATUS}" | python3 -m json.tool 2>/dev/null || echo "${STATUS}"

echo ""
echo "=============================="
CONNECTOR_STATE=$(echo "${STATUS}" | grep -o '"state":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ "${CONNECTOR_STATE}" = "RUNNING" ]; then
  echo " CDC 준비 완료 — 커넥터 RUNNING"
else
  echo " 주의: 커넥터 상태가 RUNNING이 아닙니다 (${CONNECTOR_STATE})"
  echo " 로그 확인: docker logs apten-kafka-connect"
fi
echo "=============================="
