package v3;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class OBJ {
    private MqttEndpoint endpoint;
    private String IPandIdentifier;
    private HashMap<String, MqttQoS> topic_qos = new HashMap<>();

    OBJ(String IPandIdentifier) {
        this.IPandIdentifier = IPandIdentifier;
    }

//    OBJ(MqttEndpoint endpoint) {
//        this.endpoint = endpoint;
//    }

    public void setEndpoint(MqttEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void put(String topic, MqttQoS qos) {
        if (topic_qos.containsKey(topic)) {
            topic_qos.replace(topic, qos);
        } else {
            topic_qos.put(topic, qos);
        }
    }

    public String getIPandIdentifier() {
        return IPandIdentifier;
    }

    public MqttEndpoint getEndpoint() {
        return endpoint;
    }

    public HashMap getTopicQos() {
        return topic_qos;
    }

}

public class Broker {
    private static W3J w3j;
    static String BK_IP = "140.118.111.111";
    static List<OBJ> obj_list = new ArrayList<>();

    public static void main(String[] args) {
        w3j = new W3J("Broker"
                , "8ed14fdeb05242ccced868292b1d107671409dce660ee0a6251e817bf2ece7be");

        Vertx vertx = Vertx.vertx();
        MqttServer mqttServer = MqttServer.create(vertx);

        // If an MQTT client connect to the server a new MqttEndpoint instance will be created and passed to the handler
        mqttServer.endpointHandler(endpoint -> {
            System.out.println("\nMQTT client [" + endpoint.clientIdentifier() + "] request to connect, "
                    + "clean session = " + endpoint.isCleanSession());

            if (endpoint.auth() != null) {
                System.out.println("[username= " + endpoint.auth().getUsername()
                        + ", password= " + endpoint.auth().getPassword() + "]");
            }
            System.out.println("[keep alive timeout= " + endpoint.keepAliveTimeSeconds() + "]");

            endpoint.accept(false);  // Sends the CONNACK message to the remote MQTT client

            Iterator<OBJ> objIterator = obj_list.iterator();
            boolean added = false;
            while (objIterator.hasNext()) {
                OBJ obj = objIterator.next();

                if (obj.getIPandIdentifier().split("/")[1].equals(endpoint.clientIdentifier())) {
                    obj.setEndpoint(endpoint);
                    added = true;
                    break;
                }
            }
            if(!added) {
                System.out.println("Not subcribe in BC yet !!!!!"); // equal to endpoint is Manufacturer
            }
//            obj_list.add(new OBJ(endpoint.clientIdentifier()));

            handleClientDisconnect(endpoint);
//            handleSubscription(endpoint);
            handlePublish(endpoint);
            handlePing(endpoint);
        }).listen(12888, "localhost", status -> {
            if (status.succeeded()) {
                System.out.println("MQTT server is listening on port " + status.result().actualPort());
            } else {
                System.out.println("MQTT server starting error");
                status.cause().printStackTrace();
            }
        });
    }

    private static void handleClientDisconnect(MqttEndpoint endpoint) {
        endpoint.disconnectHandler(v -> {
            System.out.println("Received disconnect from client");
            endpoint.close();
        });
    }

    private static void handleSubscription(MqttEndpoint endpoint) {
        endpoint.subscribeHandler(subscribe -> {
            Iterator<OBJ> objIterator = obj_list.iterator();

            while (objIterator.hasNext()) {
                OBJ obj = objIterator.next();
                List<MqttQoS> grantedQosLevels = new ArrayList<>();

//                if (endpoint.clientIdentifier().equals(obj.getEndpoint().clientIdentifier())) {
                    for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
                        System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                        obj.put(s.topicName(), s.qualityOfService());
                        grantedQosLevels.add(s.qualityOfService());

                        try {
//                            w3j.Response("123", "type", "version");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
                    break;
//                }
            }
        });
    }

    private static void handleUnsubscription(MqttEndpoint endpoint) {
        endpoint.unsubscribeHandler(unsubscribe -> {
            for (String t: unsubscribe.topics()) {
                System.out.println("Unsubscription for " + t);
            }
            endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
        });
    }

    private static void handlePublish(MqttEndpoint endpoint) {
        endpoint.publishHandler(message -> {
            System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset())
                    + "] with QoS [" + message.qosLevel() + "]");

            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                String topicName = message.topicName();

                Iterator<OBJ> objIterator = obj_list.iterator();
                HashMap<String, MqttQoS> topic_qos;

                while (objIterator.hasNext()) {
                    OBJ obj = objIterator.next();
                    topic_qos = obj.getTopicQos();

                    if (topic_qos.containsKey(topicName) && topic_qos.get(topicName) == MqttQoS.AT_LEAST_ONCE) {
                        obj.getEndpoint().publish(topicName, Buffer.buffer(message.payload().toString(Charset.defaultCharset()))
                                , MqttQoS.AT_LEAST_ONCE, false, false);
                    }
                }
                endpoint.publishAcknowledge(message.messageId());
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                endpoint.publishReceived(message.messageId());
            }
        }).publishReleaseHandler(messageId -> {
            endpoint.publishComplete(messageId);
        });
    }

    private static void handlePing(MqttEndpoint endpoint) {
        endpoint.pingHandler(v -> {
            System.out.println("Ping received from client");
            endpoint.close();
        });
    }
}