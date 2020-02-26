package v4;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class OBJ {
    MqttEndpoint endpoint; // GW終端 (GW透過MQTT連上時才會設)
    private String IPandID; // IP and Identifier
    private HashMap<String, MqttQoS> topic_qos = new HashMap<>(); // topic = "manufacturer / device"

    OBJ(String IPandID) {
        this.IPandID = IPandID;
    }

//    OBJ(MqttEndpoint endpoint) {
//        this.endpoint = endpoint;
//    }

    public void setEndpoint(MqttEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void put(String topic, MqttQoS qos) {
        if (topic_qos.containsKey(topic)) {
            topic_qos.replace(topic, qos); // 更新Qos
        } else {
            topic_qos.put(topic, qos); // 新增Topic
        }
    }

    public String getIPandID() {
        return IPandID;
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
    static List<OBJ> obj_list = new ArrayList<>();

    static String BK_IP = "140.118.111.111";

    public static void main(String[] args) {
        w3j = new W3J("BK1"
                , "8ed14fdeb05242ccced868292b1d107671409dce660ee0a6251e817bf2ece7be");

        Vertx vertx = Vertx.vertx();
        MqttServer mqttServer = MqttServer.create(vertx);

        mqttServer.endpointHandler(endpoint -> { // If an MQTT client connect to the server a new MqttEndpoint instance will be created and passed to the handler
            System.out.println("\nMQTT client [" + endpoint.clientIdentifier() + "] request to connect, "
                    + "clean session = " + endpoint.isCleanSession());

            if (endpoint.auth() != null) {
                System.out.println("[username= " + endpoint.auth().getUsername()
                        + ", password= " + endpoint.auth().getPassword() + "]");
            }

            System.out.println("[keep alive timeout= " + endpoint.keepAliveTimeSeconds() + "]");
            endpoint.accept(false); // Sends the CONNACK message to the remote MQTT client

            Iterator<OBJ> objIterator = obj_list.iterator(); // endpoint現在才會設定
            boolean added = false;
            while (objIterator.hasNext()) {
                OBJ obj = objIterator.next();

                if (obj.getIPandID().split("/")[1].equals(endpoint.clientIdentifier())) {
                    obj.setEndpoint(endpoint);
                    added = true;
                    break;
                }
            }
            if(!added) {
                System.out.println("Not subcribe yet !!!!!"); // Manufacturer or 還沒註冊的Gateway
            }

            handleClientDisconnect(endpoint); // GW 取消連線
//            handleSubscription(endpoint); // 我們是透過BC訂閱，所以用不到
            handleUnsubscription(endpoint); // 取消訂閱
            handlePublish(endpoint); // 取得MF韌體，並推送韌體給GW
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

    private static void handleSubscription(MqttEndpoint endpoint) { // This handler is called when a SUBSCRIBE message is received by the remote MQTT client
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

    private static void handleUnsubscription(MqttEndpoint endpoint) { // This handler is called when a UNSUBSCRIBE message is received by the remote MQTT client
        endpoint.unsubscribeHandler(unsubscribe -> {
            for (String t: unsubscribe.topics()) {
                System.out.println("Unsubscription for " + t);
            }
            // 這裡要補 取消訂閱 的處理
            endpoint.unsubscribeAcknowledge(unsubscribe.messageId()); // Sends the UNSUBACK message to the remote MQTT client
        });
    }

    private static void handlePublish(MqttEndpoint endpoint) {
        endpoint.publishHandler(message -> { // Sets handler which will be called each time server publish something to client
            System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset())
                    + "] with QoS [" + message.qosLevel() + "]");

            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                String topicName = message.topicName();

                Iterator<OBJ> objIterator = obj_list.iterator();
                HashMap<String, MqttQoS> topic_qos;

                while (objIterator.hasNext()) {
                    OBJ obj = objIterator.next();
                    topic_qos = obj.getTopicQos();

                    /*
                    /QoS 0 > there is no need from the endpoint to reply the client.
                    /QoS 1 > publishAcknowledge (send PUBACK message)
                    /QoS 2 > publishReceived (send PUBREC message) -> GW (send PUBREL message)
                            -> publishReleaseHandler (receive PUBREL message) -> publishComplete (send PUBCOMP message)
                    */

                    if (topic_qos.containsKey(topicName) && topic_qos.get(topicName) == MqttQoS.AT_LEAST_ONCE) {
                        obj.getEndpoint().publish(topicName, Buffer.buffer(message.payload().toString(Charset.defaultCharset()))
                                , MqttQoS.AT_LEAST_ONCE, false, true);
                    }
                }
                endpoint.publishAcknowledge(message.messageId()); // Sends the PUBACK message to the remote MQTT client
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                endpoint.publishReceived(message.messageId()); // Sends the PUBREC message to the remote MQTT client
            }
        }).publishReleaseHandler(messageId -> { // This handler is called when a PUBREL message is received by the remote MQTT client
            endpoint.publishComplete(messageId); // Sends the PUBCOMP message to the remote MQTT client
        });
    }

    private static void handlePing(MqttEndpoint endpoint) {
        endpoint.pingHandler(v -> {
            System.out.println("Ping received from client");
            endpoint.close();
        });
    }
}