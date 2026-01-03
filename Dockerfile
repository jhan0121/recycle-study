# Build stage
FROM amazoncorretto:21-alpine3.19-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM amazoncorretto:21-alpine3.19

WORKDIR /app

RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser
RUN mkdir -p /app/log && chown -R appuser:appgroup /app

COPY --from=builder --chown=appuser:appgroup /app/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
