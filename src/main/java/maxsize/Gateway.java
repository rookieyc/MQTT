package maxsize;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class Gateway {

    private static String name = "Gateway";
    private static String path = "C:\\Users\\hyc\\Desktop\\FW_new.txt";

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions()
                .setClientId(name)
                .setCleanSession(false)
                .setMaxMessageSize(200_000_000)
                .setMaxInflightQueue(100)
                .setKeepAliveTimeSeconds(3000);
        MqttClient client = MqttClient.create(vertx, options);

        client.connect(12888, "140.118.109.106", conn -> { // localhost 140.118.109.106
            if (conn.succeeded()) {
                System.out.println(name + "connect to Broker successfully\n");

                client.subscribe("manufacturerA/DeviceA", 1);
            } else if (conn.failed()) {
                System.out.println(name + "connect to Broker failed");
            }
        });

        client.subscribeCompletionHandler(h -> {
            System.out.println("Receive SUBACK from server with granted QoS : " + h.grantedQoSLevels() + "\n");
        });

        client.publishHandler(s -> {
            System.out.println("There are new message in topic: " + s.topicName());
            System.out.println("Content(as string) of the message: " + s.payload().toString().length());
            System.out.println("QoS: " + s.qosLevel() + "\n");

            try {
                OutputStream os = new FileOutputStream(path);
                os.write(s.payload().getBytes());
                os.flush();
                os.close();
                System.out.println("Write bytes to file successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
