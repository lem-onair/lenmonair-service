FROM openjdk:17-jdk

VOLUME /tmp

ARG JAR_FILE="build/libs/*.jar"

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=secret,common,deploy", "-jar","/app.jar"]
