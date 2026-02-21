# --- Build stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Dependencies (캐시 레이어 - gradle 파일 안 바뀌면 여기까지 캐시)
COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/
RUN chmod +x ./gradlew

# Gradle dependencies with cache mount (BuildKit 캐시로 다운로드 재사용)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew dependencies --no-daemon --quiet || true

# Source code (여기서부터는 코드 바뀌면 재실행)
COPY src/ src/

# Build with cache mount (Gradle 빌드 캐시 재사용)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon --quiet

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx512m -XX:+UseG1GC"
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
