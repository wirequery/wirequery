FROM amazoncorretto:17-alpine-jdk
ARG version
COPY build/libs/manager-server-${version}.jar .
ENTRYPOINT ["java","-jar","/app.jar"]

