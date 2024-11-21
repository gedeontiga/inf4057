FROM openjdk:21
EXPOSE 8881
ADD target/service-user.jar service-user.jar
ENTRYPOINT [ "java", "-jar", "/service-user.jar"]