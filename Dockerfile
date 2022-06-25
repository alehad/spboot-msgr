FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} msgr.jar
ENTRYPOINT ["java","-jar","/msgr.jar"]