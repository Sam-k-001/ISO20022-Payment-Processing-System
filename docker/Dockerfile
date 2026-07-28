# Dockerfile
FROM eclipse-temurin:21-jre-alpine AS runtime

LABEL maintainer="FinTech Engineering <engineering@fintech.com>"
LABEL application="iso20022-payment-system"
LABEL version="1.0.0"

# Create non-root user for security
RUN addgroup -S fintech && adduser -S paymentapp -G fintech

WORKDIR /app

# Copy built JAR
COPY target/iso20022-payment-system-1.0.0-RELEASE.jar app.jar

# Change ownership
RUN chown paymentapp:fintech app.jar

# Switch to non-root user
USER paymentapp

EXPOSE 8080

# JVM tuning for containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-XX:+HeapDumpOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
