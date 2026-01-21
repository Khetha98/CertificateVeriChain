FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom first (for caching)
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

RUN chmod +x mvnw

# Download dependencies only
RUN ./mvnw -B -DskipTests dependency:resolve

# Now copy source
COPY src src

# Build
RUN ./mvnw -B -DskipTests package

# ---- Runtime image ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*jar app.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","app.jar"]
