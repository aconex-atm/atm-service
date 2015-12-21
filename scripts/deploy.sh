#!/usr/bin/env bash
set -e
echo "upload artifact"
scp ./target/universal/atm-service-1.0.zip ubuntu@52.62.29.150:/home/ubuntu;

echo "killing process"
ssh ubuntu@52.62.29.150 "ps aux | grep java | grep Server | tr -s '' | cut -d ' ' -f5 | xargs kill -9";

echo "install package"
ssh ubuntu@52.62.29.150 "rm -rf /home/ubuntu/atm-service-1.0 && unzip /home/ubuntu/atm-service-1.0.zip  -d /home/ubuntu/ && rm atm-service-1.0.zip";

echo "start application"
ssh ubuntu@52.62.29.150 "export PATH=/home/ubuntu/jdk1.8.0_66/bin:$PATH && export HOST=172.31.24.98  && /home/ubuntu/atm-service-1.0/bin/atm-service &" &