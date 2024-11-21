FROM openjdk:21
EXPOSE 9090
ADD target/service-auth.jar service-auth.jar
ENTRYPOINT [ "java", "-jar", "/service-auth.jar"]