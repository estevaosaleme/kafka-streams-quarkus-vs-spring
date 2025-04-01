print("""
-----------------------------------------------------------------
✨ Tilt has started for Quarkus vs Springboot Kafka Streams memory footprint comparison.
-----------------------------------------------------------------

⭐ Don't forget to read the README.md file to start all services properly.
⭐ Use the scripts on the left panel to load data into Kafka.   

""")

# common pods 
k8s_yaml('./tilt-artifacts/k8s/kafka-kraft.yaml')
k8s_resource('kafka-kraft', labels=["001-common-pods"])

k8s_yaml('./tilt-artifacts/k8s/kafdrop-ui.yaml')
k8s_resource('kafdrop', labels=["001-common-pods"])

k8s_yaml('./tilt-artifacts/k8s/prometheus.yaml')
k8s_resource('prometheus', labels=["001-common-pods"])


# topic management
DELETE_TOPICS="kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/delete-topics.sh"
local_resource("delete-celsius", cmd=DELETE_TOPICS, auto_init = False, labels="002-topic-management")

CREATE_TOPICS="kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/create-topics.sh"
local_resource("create-topics", cmd=CREATE_TOPICS, auto_init = True, labels="002-topic-management")


# load temperature data
PUBLISH_KAFKA_MESSAGES_100M_10S = "kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/temperature-load.sh 100 10"
local_resource("100.Msgs_10.PerSec", cmd=PUBLISH_KAFKA_MESSAGES_100M_10S, auto_init = False, labels="003-load")

PUBLISH_KAFKA_MESSAGES_10000M_100S = "kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/temperature-load.sh 10000 100"
local_resource("10K.Msgs_100.PerSec", cmd=PUBLISH_KAFKA_MESSAGES_10000M_100S, auto_init = False, labels="003-load")

PUBLISH_KAFKA_MESSAGES_10000M_100S = "kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/temperature-load.sh 100000 100"
local_resource("100K.Msgs_100.PerSec", cmd=PUBLISH_KAFKA_MESSAGES_10000M_100S, auto_init = False, labels="003-load")

PUBLISH_KAFKA_MESSAGES_10000M_100S = "kubectl exec -it deploy/kafka-kraft -- bash /app/tilt-artifacts/scripts/temperature-load.sh 10000000 100"
local_resource("1M.Msgs_100.PerSec", cmd=PUBLISH_KAFKA_MESSAGES_10000M_100S, auto_init = False, labels="003-load")


# apps
k8s_yaml('./tilt-artifacts/k8s/springboot-iot-stateless.yaml')
k8s_resource('springboot-iot-stateless', auto_init = False, labels=["004-apps-pods"])

k8s_yaml('./tilt-artifacts/k8s/springboot-iot-stateful.yaml')
k8s_resource('springboot-iot-stateful', auto_init = False, labels=["004-apps-pods"])

k8s_yaml('./tilt-artifacts/k8s/quarkus-iot-stateless.yaml')
k8s_resource('quarkus-iot-stateless', auto_init = False, labels=["004-apps-pods"])

k8s_yaml('./tilt-artifacts/k8s/quarkus-iot-stateful.yaml')
k8s_resource('quarkus-iot-stateful', auto_init = False, labels=["004-apps-pods"],)

k8s_yaml('./tilt-artifacts/k8s/springboot-iot-stateless-native.yaml')
k8s_resource('springboot-iot-stateless-native', auto_init = False, labels=["004-apps-pods-native"])

k8s_yaml('./tilt-artifacts/k8s/springboot-iot-stateful-native.yaml')
k8s_resource('springboot-iot-stateful-native', auto_init = False, labels=["004-apps-pods-native"])

k8s_yaml('./tilt-artifacts/k8s/quarkus-iot-stateless-native.yaml')
k8s_resource('quarkus-iot-stateless-native', auto_init = False, labels=["004-apps-pods-native"])

k8s_yaml('./tilt-artifacts/k8s/quarkus-iot-stateful-native.yaml')
k8s_resource('quarkus-iot-stateful-native', auto_init = False, labels=["004-apps-pods-native"],)


# TO-DO:
# - create a stateful app with endpoint rest that change a kafka streams rule on-the-fly (post method) 
# and a get method to check whether this feature is enabled.
#   - the application deserializes data in json and serializes them in Avro when publishing
#   - this application must have log enabled
#   - this application must serialize messages with avro serialization
