
# Step 1: ใช้ base image ที่มี JDK และ Gradle
FROM gradle:7.6-jdk17 AS build

# Step 2: กำหนด working directory สำหรับการ build
WORKDIR /app

# Step 3: คัดลอกไฟล์โปรเจคทั้งหมดไปยัง container
COPY . .

# Step 4: Build Gradle project
# RUN ./gradlew clean build -x test
# RUN ./gradlew clean build -x test --no-daemon
RUN ./gradlew build --no-daemon -x test

# Step 5: สร้าง base image สำหรับ runtime
FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl

# Step 6: กำหนด working directory สำหรับการ run application
WORKDIR /app

# Step 7: คัดลอก jar file จากขั้นตอน build ไปยัง runtime image
COPY --from=build /app/build/libs/*.jar /app/order-service.jar

EXPOSE 8083

# Step 8: กำหนดค่า entrypoint เพื่อรัน Spring Boot application
ENTRYPOINT [ "sh", "-c", "echo 'Waiting for discovery-service to start'; \
  until curl -s http://discovery-service:9001/actuator/health | grep 'UP'; do \
    echo 'waiting for discovery-service to be available......'; \
    sleep 5; \
  done; \
  echo 'discovery-service is up and running order-service'; \
  exec java -jar /app/order-service.jar" ]