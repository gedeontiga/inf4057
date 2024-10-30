FROM openjdk:21
EXPOSE 8888
ADD target/service_proxy.jar service_proxy.jar
ENTRYPOINT [ "java", "-jar", "/service_proxy.jar"]