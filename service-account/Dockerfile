FROM openjdk:21
EXPOSE 8085
ADD target/service-account.jar service-account.jar
ENTRYPOINT [ "java", "-jar", "/service-account.jar"]