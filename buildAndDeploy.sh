mvn clean package -DskipTests
docker build . -t marc-brewer.de:8082/world/texter:latest
docker push marc-brewer.de:8082/world/texter:latest