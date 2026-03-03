# 枝莺后端 - Spring Boot 3 + JDK 17
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# 复制 Maven 配置和源码
COPY pom.xml .
COPY src ./src

# 构建（跳过测试以加快构建）
RUN mvn clean package -DskipTests -q

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 复制构建产物
COPY --from=builder /app/target/*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/api/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
