FROM nginx:latest

RUN mkdir -p /var/opt/learning
COPY ./dist/frontend-app/ /var/opt/learning
COPY nginx.config /etc/nginx/conf.d/default.conf
