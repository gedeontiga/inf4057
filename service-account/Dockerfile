FROM openjdk:21
EXPOSE 8888
ADD target/service_account.jar service_account.jar
ENTRYPOINT [ "java", "-jar", "/service_account.jar"]