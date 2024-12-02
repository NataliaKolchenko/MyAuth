FROM openjdk:17-jdk-slim as build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew build -x test

RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*SNAPSHOT.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/AuthSevice
ENV SPRING_DATASOURCE_USERNAME=todouser
ENV SPRING_DATASOURCE_PASSWORD=Qwerty12345!

ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.demo.MyAuthApplication"]