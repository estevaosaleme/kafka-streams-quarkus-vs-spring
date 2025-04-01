# Kafka Streams Quarkus vs Springboot Resource Utilization

This repo contains artifacts to run and compare resource utilization of Quarkus vs Springboot with Kafka Streams. To avoid external influences, it's built on top of Minikube to run in an isolated environment.


## Pre-requisites

The following programs are used to back up the infrastructure needed to run the comparison.

- [Docker](https://www.docker.com/) (version 27.5.1, build 9f9e405)
- [Docker Compose](https://github.com/docker/compose/releases) (version v2.32.4)
- [Minikube](https://minikube.sigs.k8s.io/docs/) (v1.32.0)
- [Helm](https://helm.sh/) (Version:"v3.14.3")
- [Kubectl](https://kubernetes.io/docs/tasks/tools/) (Client Version: v1.29.2)
- [Tilt](https://tilt.dev/) (v0.33.19, built 2024-08-05)

You may use different versions of these programs and still run the infrastructure without any problem. However, I do not recommend using lower versions than that of the aforementioned ones.


## Set up Prometheus for Node Level Metrics

1) Add the following repository to helm:

```shell
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
```

2) Install the provided charts by running the following commands:

```shell
helm install prometheus prometheus-community/prometheus
```

3) Expose the service:

```shell
kubectl expose service prometheus-server --type=NodePort --target-port=9090 --name=prometheus-server-np
```

## Getting Started

Before making progress with the apps running, you need to run the infrastructure and build the container images that will be used.

1) Run Minikube:
   
```shell
minikube start --driver=docker
```

2) In the *kafka-streams-quarkus-vs-spring* folder, run Minikube mount:

```shell
minikube mount $(pwd):/kafka-streams-quarkus-vs-spring
```

3) In the *kafka-streams-quarkus-vs-spring* folder, build the container images into Minikube:

**JVM images**

```shell
minikube image build -f springboot-iot-stateless/Dockerfile -t springboot-iot-stateless .

minikube image build -f springboot-iot-stateful/Dockerfile -t springboot-iot-stateful .

minikube image build -f quarkus-iot-stateless/Dockerfile -t quarkus-iot-stateless .

minikube image build -f quarkus-iot-stateful/Dockerfile -t quarkus-iot-stateful .
```

**Native Images**

Please avoid building **native** images with `minikube image build` because it may take long.

- Build native images:

```shell
docker build -f springboot-iot-stateless/Dockerfile-native -t springboot-iot-stateless-native .

docker build -f springboot-iot-stateful/Dockerfile-native -t springboot-iot-stateful-native .

docker build -f quarkus-iot-stateless/Dockerfile-native -t quarkus-iot-stateless-native .

docker build -f quarkus-iot-stateful/Dockerfile-native -t quarkus-iot-stateful-native .
```

- Save and load native images into Minikube:
  
```shell
docker image save -o springboot-iot-stateless-native.tar springboot-iot-stateless-native:latest
minikube image load springboot-iot-stateless-native.tar

docker image save -o springboot-iot-stateful-native.tar springboot-iot-stateful-native:latest
minikube image load springboot-iot-stateful-native.tar

docker image save -o quarkus-iot-stateless-native.tar quarkus-iot-stateless-native:latest
minikube image load quarkus-iot-stateless-native.tar

docker image save -o quarkus-iot-stateful-native.tar quarkus-iot-stateful-native:latest
minikube image load quarkus-iot-stateful-native.tar
```

4) Then, run Tilt from the *kafka-streams-quarkus-vs-spring* folder:

```shell
tilt up
```

Follow the instructions to open Tilt's UI on the localhost.


## Exposed Services

Here are listed the services that are exposed by the infrastructure so you can analyse the collected metrics.

- Minikube dashboard:

```shell
minikube dashboard
```

- Kafdrop UI:

```shell
minikube service kafdrop
```

- Prometheus for the Apps' metrics:

```shell
minikube service prometheus
```

- Prometheus Node for the Node metrics:

```shell
minikube service prometheus-server-np
```

## Check PID memory

- You can check the amount of RSS the Java process is consuming in each deployment:

```shell
kubectl exec -it deploy/<DEPLOYMENT-NAME> -- ps -eo pid,rss,args
```

List of deployment names:
- *springboot-iot-stateless*
- *springboot-iot-stateful*
- *quarkus-iot-stateless*
- *quarkus-iot-stateful*
- *springboot-iot-stateless-native*
- *springboot-iot-stateful-native*
- *quarkus-iot-stateless-native*
- *quarkus-iot-stateful-native*
