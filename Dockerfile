# 1) Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests package

# 2) Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app
# copy the boot jar (works even if the jar name changes)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
