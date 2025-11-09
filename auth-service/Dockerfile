FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Create keys directory (keys will be mounted via volume)
RUN mkdir -p /app/keys

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
