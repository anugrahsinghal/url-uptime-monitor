FROM openjdk:8

EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} xup.jar
ENTRYPOINT ["java", "-jar","/xup.jar"]
