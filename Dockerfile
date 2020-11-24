FROM debian:stable

RUN apt update && \
    apt dist-upgrade -y && \
    apt install default-jdk -y

COPY target/microservice.jar /microservice.jar
EXPOSE 8080
CMD java -jar microservice.jar