FROM openjdk:8-jdk-alpine
WORKDIR /
ARG JAR_FILE=build/libs/terrain-generator-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} .
RUN jar -xf terrain-generator-0.0.1-SNAPSHOT.jar
#RUN java org.springframework.boot.loader.JarLauncher

#COMMAND FOR STARUP
# docker build . -t terrain-generator
# docker run -p 8080:8080 terrain-generator java org.springframework.boot.loader.JarLauncher