package maxsize;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Manufacturer {

    private static String name = "Manufacturer";
    private static String path = "C:\\Users\\hyc\\Desktop\\FW_100M.txt";

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions()
                .setClientId(name)
                .setCleanSession(false)
                .setMaxMessageSize(200_000_000) // Set max MQTT message size (in bytes)
                .setMaxInflightQueue(100)       // Set max count of unacknowledged messages
                .setKeepAliveTimeSeconds(3000); // Set the keep alive timeout in seconds
        MqttClient client = MqttClient.create(vertx, options);

        client.connect(12888, "140.118.109.106", conn -> { // localhost 140.118.109.106
            if (conn.succeeded()) {
                try {
                    System.out.println(name + "connect to Broker successfully\n");

                    client.publish("manufacturerA/DeviceA",
                            Buffer.buffer(Files.readAllBytes(Paths.get(path))),
                            MqttQoS.AT_LEAST_ONCE, false, true); // b1: isRetain
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (conn.failed()) {
                System.out.println(name + "connect to Broker failed");
            }
        });

        client.publishCompletionHandler(s -> {
            System.out.println("publish Completion, Id of just received PUBACK or PUBCOMP packet is " + s);
        });
    }
}
