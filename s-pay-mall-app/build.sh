#!/usr/bin/env sh
set -eu
ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT_DIR"
docker build -t s-pay-mall-api:2.0 -f s-pay-mall-app/Dockerfile .
