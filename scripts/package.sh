#!/usr/bin/env bash
image=${1?No image supplied}
version=${2?No version supplied}

docker tag -f $image nicholasren/atm-service:$version
docker push nicholasren/atm-service:$version