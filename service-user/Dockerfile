FROM openjdk:21
EXPOSE 8888
ADD target/service_user.jar service_user.jar
ENTRYPOINT [ "java", "-jar", "/service_user.jar"]