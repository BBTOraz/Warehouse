# --- Стадия сборки (build stage) ---
FROM maven:3.8.5-openjdk-17 AS build

# Создадим рабочую директорию
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# --- Стадия runtime (минимальный образ с JRE) ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar


EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
