package v3;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.util.Random;

public class Manufacturer {

    public static void main(String[] args) {
        MqttClientOptions options = new MqttClientOptions().setClientId("PC-98383a3d-2618-4267-b8a2-0d8da2c1b21a")
                .setWillRetain(true)   // y
                .setKeepAliveTimeSeconds(60);

        Vertx vertx = Vertx.vertx();
        MqttClient client = MqttClient.create(vertx, options);

        client.connect(12888, "localhost", conn -> { // entry.getKey()
            if (conn.succeeded()) {
                System.out.println("Gateway connect to Broker success");
            } else if (conn.failed()) {
                System.out.println("Gateway connect to Broker failed");
            }

            Thread publishThread = new Thread(new Runnable() {
                boolean stop_flag = false;
                Random random = new Random();

                @Override
                public void run() {
                    while (!stop_flag) {
                        try {
                            int num = random.nextInt(101);

                            if (num < 50) {
                                client.publish("manufacturerA/DeviceA",
                                        Buffer.buffer("hello " + num),
                                        MqttQoS.AT_LEAST_ONCE, false, true); // y
                            } else {
                                client.publish("manufacturerA/DeviceA",
                                        Buffer.buffer("hello " + num),
                                        MqttQoS.AT_MOST_ONCE, false, true);
                            }
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            publishThread.start();
        });

        client.publishCompletionHandler(s -> {
            System.out.println("publish Completion: " + s.toString()); // Qos 1
        });
    }
}
