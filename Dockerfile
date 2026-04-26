# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy project files
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn ./.mvn
COPY src ./src

# Build the application (skip tests and test compilation for faster/robust image build)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw package -Dmaven.test.skip=true -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser

# Copy the built application from build stage
COPY --from=build /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build /app/target/quarkus-app/*.jar /app/
COPY --from=build /app/target/quarkus-app/app/ /app/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /app/quarkus/

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
