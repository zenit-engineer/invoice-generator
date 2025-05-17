# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build

# Set the working directory
WORKDIR /app

# Install Maven (instead of using the wrapper)
RUN apt-get update && apt-get install -y maven

# Copy the POM file first (for layer caching)
COPY pom.xml .

# Download all dependencies (this layer will be cached unless POM changes)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Package the application (skip tests)
RUN mvn package -DskipTests

# Verify the JAR file exists
RUN ls -l target/

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Set timezone (adjust to your location)
ENV TZ=Europe/Tirana
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set JVM options (adjust as needed)
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=prod"

# Expose the port your app runs on
EXPOSE 8080

# Health check (adjust if you don't use actuator)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point to run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]