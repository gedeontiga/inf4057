FROM openjdk:17
ADD target/service-transactions.jar server-transactions.jar
EXPOSE 8182
ENTRYPOINT [ "java", "-jar", "/server-transactions.jar" ]