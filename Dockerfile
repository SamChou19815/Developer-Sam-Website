# Adpated from http://paulbakker.io/java/docker-gradle-multistage/

# Stage 1: Compile
FROM gradle:jdk8 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

# Stage 2: Add Jar
FROM openjdk:8u171-jre-alpine3.7

COPY --from=builder /home/gradle/src/build/libs/website-5.0-all.jar /app/
WORKDIR /app
EXPOSE 8080

CMD ["java", "-jar", "website-5.0-all.jar"]
