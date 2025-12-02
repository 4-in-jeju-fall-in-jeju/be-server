## ----------------------------------------------------------------------------------------------------
## 작성목적 : be-server 애플리케이션 Docker 이미지 빌드 설정
## 작성일 : 2025-12-02
##
## 변경사항 내역 (날짜 | 변경목적 | 변경내용 | 작성자 순으로 기입)
## 2025-12-02 | 최초 구현 | Dockerfile에 코드 헤더 규칙 적용 | 이재인
## ----------------------------------------------------------------------------------------------------

# # 1단계: build
# FROM eclipse-temurin:17-jdk-alpine AS builder
# WORKDIR /app

# COPY mvnw pom.xml ./
# COPY .mvn .mvn
# RUN ./mvnw -B -DskipTests dependency:go-offline

# COPY src ./src
# RUN ./mvnw -B -DskipTests package

# # 2단계: runtime
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# ARG JAR_FILE=target/*.jar
# COPY --from=builder ${JAR_FILE} app.jar

# EXPOSE 8080
# ENTRYPOINT ["java","-jar","/app/app.jar"]


# ----------------------------------------        ----------------------------------------
# ----------------------------------------        ----------------------------------------
# 임시로 더미데이터 빌드용 도커파일
# 실제 사용은 위 코드

# 1단계: build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven
RUN mvn -B -DskipTests package

# 2단계: runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]