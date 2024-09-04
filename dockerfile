FROM openjdk:23-ea-21-oraclelinux9
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]