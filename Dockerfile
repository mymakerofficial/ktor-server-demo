FROM gradle:8-jdk-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle build --no-daemon

FROM golang:1.20-alpine AS compileDaemon

RUN go install github.com/githubnemo/CompileDaemon@latest

FROM gradle:8-jdk-alpine AS development

COPY --from=compileDaemon /go/bin/CompileDaemon /usr/local/bin/CompileDaemon
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

CMD CompileDaemon -log-prefix=false -build="gradle build" -command="gradle run" -pattern="(.+\\.kt)"

FROM openjdk:8-jre-alpine AS runtime

COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

CMD ["java", "-jar", "/app.jar"]
