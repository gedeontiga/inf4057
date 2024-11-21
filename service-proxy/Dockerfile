FROM openjdk:21
EXPOSE 8075
ADD target/service-proxy.jar service-proxy.jar
ENTRYPOINT [ "java", "-jar", "/service-proxy.jar"]