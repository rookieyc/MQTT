package v4;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Gateway {

    private static W3J w3j;
    static HashMap<String, String> BK_IPsandPKs = new HashMap<>();

    final static String GW_IP = "127.0.0.1";
    final static String GW_Identifier = "GW-98383a3d-2618-4267-b8a2-0d8da2c1b21a";

    private static MqttClient client;
    private static Thread subscribe, rotate;

    public static void main(String[] args) {
        w3j = new W3J("GW"
                , "0a4053d59d35816f309adc23ea6fc6cb2820c8bc7156b62a931d1ef80f62a620"
                ,"0xa6ea3f188d51042f771af2cc1946d703f3d7a233"); // 假設知道

        Vertx vertx = Vertx.vertx();
        MqttClientOptions options = new MqttClientOptions()
                .setClientId(GW_Identifier)
                .setCleanSession(false) // Set to start with a clean session (or not)
                .setKeepAliveTimeSeconds(30); // Set the keep alive timeout in seconds
        client = MqttClient.create(vertx, options);

        initThread();

        // 訂閱
        subscribe.start(); // 理論上要動態訂閱，先寫在Thread...

        // 依序連接Broker
        try {
            Thread.sleep(10000);
            System.out.println("\nBK_IPs & PKs:");
            for (Map.Entry<String, String> entry : BK_IPsandPKs.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            Thread.sleep(10000);
            rotate.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 訂閱ACK
        client.subscribeCompletionHandler(h -> { // Sets handler which will be called after SUBACK packet receiving
            System.out.println("Subscribe Complete, levels:" + h.grantedQoSLevels()); // e.g. client.subscribe("message", 1);
        });

        // 取得推送
        client.publishHandler(s -> { // Sets handler which will be called each time server publish something to client
            System.out.print("There are new message in topic: " + s.topicName() + ";  ");
            System.out.print("Content(as string) of the message: " + s.payload().toString() + ";  ");
            System.out.println("QoS: " + s.qosLevel());
        });
    }

    private static void initThread() {
        subscribe = new Thread(new Runnable() {
            int times = 10, num;
            Random random = new Random(new Date().getTime());

            @Override
            public void run() {
                while ((times--) > 0) {
                    try {
                        num = random.nextInt(101);
                        System.out.println("num: " + num);

                        if (num < 20) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerA", "DeviceA", "1");
                        } else if (num < 40) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerB", "DeviceB", "1");
                        } else if (num < 60) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerC", "DeviceC", "1");
                        } else if (num < 80) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerD", "DeviceD", "1");
                        } else {
                            w3j.Request("127.7.7.7/"+ GW_Identifier, "manufacturerE", "DeviceE", "2");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        rotate = new Thread(new Runnable() {
            boolean stop_flag = false;

            @Override
            public void run() {
                while (!stop_flag) {
                    try {
                        Thread.sleep(1000);
                        for (Map.Entry<String, String> entry : BK_IPsandPKs.entrySet()) {
                            System.out.println("\nNew Connect...");

                            client.connect(12888, "localhost", conn -> { // 改成 entry.getKey() 依序連接
                                if (conn.succeeded()) {
                                    System.out.println("Gateway connect to Broker successfully");
                                } else if (conn.failed()) {
                                    System.out.println("Gateway connect to Broker failed");
                                }
                            });
                            Thread.sleep(5000);
                            while (client.isConnected()) {
                                Thread.sleep(10000);
                            }
                            System.out.println("Disconnect");
                        }
//                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}