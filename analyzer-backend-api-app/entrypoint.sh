#!/usr/bin/env sh

/apps/wait-service.sh MongoDB db 27017 && \
/apps/wait-service.sh RabbitMq rabbitmq 15672 && \
/usr/bin/java -Xmx256m -Xss512k -XX:-UseContainerSupport \
              -jar /apps/app.jar