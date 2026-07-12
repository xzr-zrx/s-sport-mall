FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -B -ntp -pl s-pay-mall-app -am clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=builder /workspace/s-pay-mall-app/target/s-pay-mall-app.jar /app/app.jar
USER app
EXPOSE 10000
ENTRYPOINT ["sh","-c","exec java ${JAVA_OPTS:--XX:MaxRAMPercentage=75.0} -Dserver.port=${PORT:-10000} -jar /app/app.jar"]
