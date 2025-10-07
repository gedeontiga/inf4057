FROM openjdk:21
EXPOSE 8181
ADD target/service-bank-0.0.1-SNAPSHOT.jar service-bank.jar
ENTRYPOINT [ "java", "-jar", "/service-bank.jar"]