FROM amazoncorretto:17

WORKDIR /app
COPY target/s3-memorydb-loader-1.0.jar app.jar
COPY src/main/resources/application.properties application.properties

ENTRYPOINT ["java", "-jar", "app.jar"]
