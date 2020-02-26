package v3;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.*;

public class Gateway {

    static W3J w3j;
    static String GW_IP = "127.0.0.1";
    static String GW_Identifier = "98383a3d-2618-4267-b8a2-0d8da2c1b21a";
    static HashMap<String, String> BK_IPsandPKs = new HashMap<>(); // ?????

    public static void main(String[] args) {
        try {
            w3j = new W3J("Gateway"
                    , "0a4053d59d35816f309adc23ea6fc6cb2820c8bc7156b62a931d1ef80f62a620"
                    ,"0xa6ea3f188d51042f771af2cc1946d703f3d7a233");  // ?????
            //w3j.Request(GW_IP + "/" + GW_Identifier, "ManufacturerB", "DeviceC", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        MqttClientOptions options = new MqttClientOptions().setClientId(GW_Identifier)
                .setCleanSession(true) // reset
                .setKeepAliveTimeSeconds(30);

        Vertx vertx = Vertx.vertx();
        MqttClient client = MqttClient.create(vertx, options);

        // SUBACK
        client.subscribeCompletionHandler(h -> {
            System.out.println("Subscribe complete, levels:" + h.grantedQoSLevels());
        });

        // will be called each time you have a new messages in the topics you subscribe on
        client.publishHandler(s -> {
            System.out.print("There are new message in topic: " + s.topicName() + ";  ");
            System.out.print("Content(as string) of the message: " + s.payload().toString() + ";  ");
            System.out.println("QoS: " + s.qosLevel());
        });

        Thread rotate = new Thread(new Runnable() {
            boolean stop_flag = false;

            @Override
            public void run() {
                while (!stop_flag) {
                    try {
                        for (Map.Entry<String, String> entry : BK_IPsandPKs.entrySet()) {
                            System.out.println("\nNew Connect...");
                            client.connect(12888, "localhost", conn -> { // entry.getKey()
                                if (conn.succeeded()) {
                                    System.out.println("Gateway connect to Broker success");
                                } else if (conn.failed()) {
                                    System.out.println("Gateway connect to Broker failed");
                                }
//                                client.subscribe("message", 1);
                            });

                            Thread.sleep(5000);
                            while (client.isConnected()) {
                                //System.out.println("alive");
                                Thread.sleep(10000);
                            }
                            System.out.println("disconnect");
                        }
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        new Thread(new Runnable() {
            int times = 10, num;
            Random random = new Random(123);

            @Override
            public void run() {
                while ((times--) > 0) {
                    try {
                        Thread.sleep(100);
                        num = random.nextInt(101);
                        System.out.println("n: " + num);

                        if (num < 20) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerA", "DeviceA", "1");
                        } else if (20 <= num && num < 40) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerB", "DeviceB", "1");
                        } else if (40 <= num && num < 60) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerC", "DeviceC", "1");
                        } else if (60 <= num && num < 80) {
                            w3j.Request(GW_IP + "/" + GW_Identifier, "manufacturerD", "DeviceD", "1");
                        } else {
                            w3j.Request("127.7.7.7/"+ GW_Identifier, "manufacturerE", "DeviceE", "2");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(10000);
                    System.out.println("\nBK_IPsandPKs:");
                    for (Map.Entry<String, String> entry : BK_IPsandPKs.entrySet()) {
                        System.out.println(entry.getKey() + " - " + entry.getValue());
                    }
                    rotate.start();
                } catch (Exception e) {}
            }
        }).start();
    }
}