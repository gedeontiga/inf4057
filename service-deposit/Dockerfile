FROM openjdk:21
EXPOSE 8183
ADD target/service-deposit-0.0.1-SNAPSHOT.jar service-deposit.jar
ENTRYPOINT [ "java", "-jar", "/service-deposit.jar"]