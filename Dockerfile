FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:resolve-plugins

COPY src/main src/main

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/gateway-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]
