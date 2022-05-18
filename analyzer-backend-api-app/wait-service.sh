#!/usr/bin/env sh
serviceName=$1
host=$2
port=$3
echo "Starting to wait for '$serviceName' at host '$host'"
while ! nc -z -v -w30 $host $port;
do
  echo Waiting for $serviceName to be ready;
  sleep 5;
done;