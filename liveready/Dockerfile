FROM gradle:alpine as builder

USER root

ADD build.gradle .

RUN gradle --console=verbose compileJava

ADD . .

RUN gradle --console=verbose assemble


FROM java:alpine

COPY --from=builder /home/gradle/build/libs/liveready-0.0.1-SNAPSHOT.jar liveready.jar

CMD [ "java" , "-jar", "liveready.jar"]
