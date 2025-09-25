# --- Build stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 🔥 의존성 캐시를 위해 gradle 파일들만 먼저 복사
# 👉 여기서 의존성이 추가/변경되면 이 레이어만 다시 실행되고, 기존 의존성은 캐시됨
COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/
RUN chmod +x ./gradlew

# 🔥 의존성만 먼저 다운로드 (소스 변경시에도 이 레이어는 캐시됨)
RUN ./gradlew dependencies --no-daemon --quiet || true

# 소스 코드 복사 및 빌드
COPY src/ src/
RUN ./gradlew bootJar --no-daemon --quiet  # 🔥 clean 제거, quiet 추가

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
# 메모리 상한 (필요시 조정)
ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m -XX:+UseG1GC"
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]