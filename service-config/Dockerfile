FROM openjdk:21
EXPOSE 8888
ADD target/service_config.jar service_config.jar
ENTRYPOINT [ "java", "-jar", "/service_config.jar"]