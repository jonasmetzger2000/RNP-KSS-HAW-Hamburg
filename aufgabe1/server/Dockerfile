FROM gradle:jdk17-focal AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar

FROM openjdk:17
EXPOSE 7878
RUN mkdir /app
ENV TCP_HOST=0.0.0.0
ENV TCP_PORT=7878
ENV MAX_CLIENTS=3
ENV PROT_MAX_BYTE_SIZE=255
ENV TERMINATION_SOCKET_TIMEOUT=30
COPY --from=build /home/gradle/src/build/libs/*.jar /app/server.jar
ENTRYPOINT ["java", "-jar","/app/server.jar"]