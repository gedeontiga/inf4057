FROM openjdk:21
EXPOSE 8184
ADD target/service-withdrawal-0.0.1-SNAPSHOT.jar service-withdrawal.jar
ENTRYPOINT [ "java", "-jar", "/service-withdrawal.jar"]