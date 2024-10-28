FROM openjdk:17
EXPOSE 8181
ADD target/message-service-docker.jar message-service-docker.jar
ENTRYPOINT ["java", "-jar", "message-service-docker.jar"]