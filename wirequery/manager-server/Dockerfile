FROM amazoncorretto:17-alpine-jdk
ARG VERSION
COPY build/libs/manager-server-${VERSION}.jar .
ENTRYPOINT ["java","-jar","/app.jar"]

