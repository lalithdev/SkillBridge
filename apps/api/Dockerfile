# Build Stage
FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root system group and user
RUN addgroup --system skillbridge && adduser --system --ingroup skillbridge skillbridge

# Create upload directory with proper permissions
RUN mkdir -p /app/uploads/resumes && chown -R skillbridge:skillbridge /app

# Copy the built executable jar from build stage
COPY --from=build --chown=skillbridge:skillbridge /app/target/*.jar app.jar

USER skillbridge

# Render dynamically sets PORT (defaults to 8080 locally)
ENV PORT=8080
EXPOSE 8080

# Production JVM flags for container environments
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
