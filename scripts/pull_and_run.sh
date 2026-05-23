#!/usr/bin/env bash
# pull_and_run.sh
# 실행 위치: EC2의 /home/ec2-user/pull_and_run.sh
# 호출 주체:  GitHub Actions가 AWS SSM Run Command로 원격 실행
#
# 사전 준비 (1회):
#   1) /etc/koces-receipt.env  (root:root, 0600)
#        DB_PASSWORD=<Supabase DB password>
#        SPRING_PROFILES_ACTIVE=prod
#        # GHCR 이미지가 private이면 아래 2개도 필요
#        GHCR_USER=<github-username-or-bot>
#        GHCR_TOKEN=<PAT with read:packages>
#   2) sudo chmod 600 /etc/koces-receipt.env
#   3) sudo chown root:root /etc/koces-receipt.env
#   4) Docker 설치 + ec2-user를 docker 그룹에 추가
#
# 멱등 실행: 같은 image:latest를 다시 받아 재기동해도 안전

set -euo pipefail

IMAGE="ghcr.io/insightsoop-main/receipt-koces:latest"
CONTAINER_NAME="koces-receipt"
PORT=10033
ENV_FILE="/etc/koces-receipt.env"

log() { echo "[$(date -u +%FT%TZ)] $*"; }

# 1) 환경변수 로드
if [[ ! -f "$ENV_FILE" ]]; then
  log "ERROR: $ENV_FILE not found. See pull_and_run.sh header for setup."
  exit 1
fi
set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

: "${DB_PASSWORD:?DB_PASSWORD missing in $ENV_FILE}"
: "${SPRING_PROFILES_ACTIVE:=prod}"

# 2) GHCR 로그인 (private 이미지인 경우만)
if [[ -n "${GHCR_TOKEN:-}" && -n "${GHCR_USER:-}" ]]; then
  log "Logging in to ghcr.io as $GHCR_USER..."
  echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin
else
  log "Skipping ghcr.io login (assuming public image)"
fi

# 3) 최신 이미지 pull
log "Pulling $IMAGE..."
docker pull "$IMAGE"

# 4) 기존 컨테이너 종료/제거 (있을 때만)
if docker ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  log "Stopping existing container $CONTAINER_NAME..."
  docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
  docker rm "$CONTAINER_NAME"   >/dev/null 2>&1 || true
fi

# 5) 신규 컨테이너 기동
#    application.yml prod 프로파일에서 ${DB_PASSWORD}를 참조하므로 환경변수로 주입
log "Starting new container $CONTAINER_NAME..."
docker run -d \
  --name "$CONTAINER_NAME" \
  --restart unless-stopped \
  -p "${PORT}:${PORT}" \
  -e SPRING_PROFILES_ACTIVE="$SPRING_PROFILES_ACTIVE" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  --log-driver=json-file \
  --log-opt max-size=50m \
  --log-opt max-file=5 \
  "$IMAGE"

# 6) 헬스 체크 (간단)
sleep 3
if docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  log "OK: $CONTAINER_NAME is running"
  docker ps --filter "name=$CONTAINER_NAME" --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
else
  log "ERROR: $CONTAINER_NAME failed to start. Recent logs:"
  docker logs --tail 80 "$CONTAINER_NAME" || true
  exit 1
fi

# 7) 댕글링 이미지 정리 (디스크 절약)
log "Pruning dangling images..."
docker image prune -f >/dev/null

log "Done."
