FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY quarkus-iot-stateful/pom.xml .
COPY quarkus-iot-stateful/src ./src

RUN mvn package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/quarkus-app /app/

RUN curl -k --progress-bar -O "https://repo.maven.apache.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/1.0.1/jmx_prometheus_javaagent-1.0.1.jar"
RUN echo 'rules:\n- pattern: ".*"' > /app/config.yaml

CMD ["java", "-javaagent:jmx_prometheus_javaagent-1.0.1.jar=0.0.0.0:9404:config.yaml", "-jar", "/app/quarkus-run.jar"]
