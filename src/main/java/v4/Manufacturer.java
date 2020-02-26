package v4;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.util.Date;
import java.util.Random;

public class Manufacturer {

    private static MqttClient client;
    private static Thread publish;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions()
                .setClientId("MF-98383a3d-2618-4267-b8a2-0d8da2c1b21a")
                .setCleanSession(false)
                .setKeepAliveTimeSeconds(30);
        client = MqttClient.create(vertx, options);

        initThread();

        client.connect(12888, "localhost", conn -> { // entry.getKey()
            if (conn.succeeded()) {
                System.out.println("Manufacturer connect to Broker successfully");
            } else if (conn.failed()) {
                System.out.println("Manufacturer connect to Broker failed");
            }

            // 推送韌體
            publish.start(); // 理論上要動態推送，先寫在Thread...
        });

        client.publishCompletionHandler(s -> { // Sets handler which will be called each time publish is completed
            System.out.println("publish Completion: " + s.toString());
        });
    }

    private static void initThread() {
        publish = new Thread(new Runnable() {
            boolean stop_flag = false;
            int num;
            Random random = new Random(new Date().getTime());

            @Override
            public void run() {
                while (!stop_flag) {
                    try {
                        num = random.nextInt(101);
                        System.out.println("num: " + num);

                        //  QoS 0 (AT_MOST_ONCE), QoS 1 (AT_LEAST_ONCE), QoS 2 (EXACTLY_ONCE)
                        if (num < 50) {
                            client.publish("manufacturerA/DeviceA",
                                    Buffer.buffer("hello " + num),
                                    MqttQoS.AT_LEAST_ONCE, false, true); // b1: isRetain
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
    }
}
