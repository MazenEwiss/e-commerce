# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only Maven configuration first
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Now copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests


# Run stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]