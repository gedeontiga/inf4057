FROM openjdk:21
EXPOSE 8761
ADD target/service_registry.jar service_registry.jar
ENTRYPOINT [ "java", "-jar", "/service_registry.jar"]