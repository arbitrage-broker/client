#!/bin/bash

# Replace these variables with your actual values
HOST="177.136.225.167"

# Connect via SSH, run sudo su, and execute additional Docker commands
sudo ssh -p 6579 -T "root@$HOST" <<EOF
  cd eshop/client
  git pull
  mvn clean package verify -DskipTests

  docker rm -f client-container
  docker rmi client-image
  docker compose up -d
EOF
