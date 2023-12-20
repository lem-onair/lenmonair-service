FROM openjdk:17-jdk

ARG JAR_FILE_PATH=build/libs/*.jar

COPY $JAR_FILE_PATH app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
