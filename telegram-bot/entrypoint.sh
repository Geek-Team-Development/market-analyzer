#!/usr/bin/env sh

/apps/wait-service.sh RabbitMQ rabbitmq 5672 && \
/apps/wait-service.sh Redis redis 6379 && \
/usr/bin/java -Xmx256m -Xss512k -XX:-UseContainerSupport \
              -jar /apps/app.jar