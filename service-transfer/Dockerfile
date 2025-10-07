FROM openjdk:21
EXPOSE 8185
ADD target/service-transfer-0.0.1-SNAPSHOT.jar service-transfer.jar
ENTRYPOINT [ "java", "-jar", "/service-transfer.jar"]