FROM amazoncorretto:11-alpine-jdk
RUN apk add -U tzdata && cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime
ENV TZ=Europe/Moscow
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]