FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY springboot-iot-stateful/pom.xml .
COPY springboot-iot-stateful/src ./src

RUN mvn package 

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

RUN curl -k --progress-bar -O "https://repo.maven.apache.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/1.0.1/jmx_prometheus_javaagent-1.0.1.jar"
RUN echo 'rules:\n- pattern: ".*"' > /app/config.yaml

CMD ["java", "-javaagent:jmx_prometheus_javaagent-1.0.1.jar=0.0.0.0:9404:config.yaml", "-jar", "/app/app.jar"]

