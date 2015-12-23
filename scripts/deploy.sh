#!/usr/bin/env bash
set -e

version=${1?No version supplied}
echo "Stop application"
ssh ubuntu@52.62.29.150 "sudo docker rm -f atm"

echo "Start application"
ssh ubuntu@52.62.29.150 "sudo docker run -d -e REDIS_HOST=atm-redis.c156rq.0001.apse2.cache.amazonaws.com --name=atm -p 8080:8080 \"nicholasren/atm-service:$1\""

