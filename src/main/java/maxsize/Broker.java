package maxsize;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttTopicSubscription;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Broker {

    static MqttEndpoint GW_endpoint; // tmp

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        MqttServerOptions options = new MqttServerOptions()
                .setMaxMessageSize(200_000_000);
        MqttServer mqttServer = MqttServer.create(vertx, options);

        mqttServer.endpointHandler(endpoint -> {
            System.out.println("\nMQTT client [" + endpoint.clientIdentifier() + "] request to connect, "
                    + "clean session = " + endpoint.isCleanSession());

            if (endpoint.auth() != null) {
                System.out.println("[username= " + endpoint.auth().getUsername()
                        + ", password= " + endpoint.auth().getPassword() + "]");
            }

            System.out.println("[keep alive timeout= " + endpoint.keepAliveTimeSeconds() + "]\n");
            endpoint.accept(false);

            if (endpoint.clientIdentifier().equals("Gateway")) { // tmp
                GW_endpoint = endpoint;
            }

            handleSubscription(endpoint);
            handlePublish(endpoint);
        }).listen(12888, "localhost", status -> { // localhost 140.118.109.106
            if (status.succeeded()) {
                System.out.println("MQTT server is listening on port " + status.result().actualPort());
            } else {
                System.out.println("MQTT server starting error");
                status.cause().printStackTrace();
            }
        });
    }

    private static void handleSubscription(MqttEndpoint endpoint) { // This handler is called when a SUBSCRIBE message is received by the remote MQTT client
        endpoint.subscribeHandler(subscribe -> {
            List<MqttQoS> grantedQosLevels = new ArrayList<>();
            for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
                System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                grantedQosLevels.add(s.qualityOfService());
            }

            endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
        });
    }

    private static void handlePublish(MqttEndpoint endpoint) {
        endpoint.publishHandler(message -> {
//            System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
            System.out.println("Just received message [" + message.payload().toString().length() + "] with QoS [" + message.qosLevel() + "]");

            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                GW_endpoint.publish(message.topicName(), Buffer.buffer(message.payload().toString(Charset.defaultCharset()))
                        , MqttQoS.AT_LEAST_ONCE, false, true);

                endpoint.publishAcknowledge(message.messageId());
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                endpoint.publishReceived(message.messageId());
            }
        }).publishReleaseHandler(messageId -> {
            endpoint.publishComplete(messageId);
        });
    }
}
