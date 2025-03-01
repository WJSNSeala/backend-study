# 빌드 스테이지
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Maven 래퍼와 pom.xml 복사 및 의존성 다운로드
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# 소스 코드 복사 및 빌드
COPY src src
RUN ./mvnw package -DskipTests

# 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 컨테이너 실행 시 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]