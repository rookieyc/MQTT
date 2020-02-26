package v4;

import io.netty.handler.codec.mqtt.MqttQoS;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.Iterator;

public class W3J {

    private static String OWNER;
    private static String PRIVATE_KEY;
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(0x444444);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(0x44444444);

    private MyContract contract;
    private static String DeployedContract_ADDRESS; // 暫時由BK1負責寫入

    public W3J(){}

    public W3J(String OWNER, String PRIVATE_KEY) {
        this.OWNER = OWNER;
        this.PRIVATE_KEY = PRIVATE_KEY;
        init();
    }

    public W3J(String OWNER, String PRIVATE_KEY, String DeployedContract_ADDRESS) {
        this.OWNER = OWNER;
        this.PRIVATE_KEY = PRIVATE_KEY;
        this.DeployedContract_ADDRESS = DeployedContract_ADDRESS; // GW假設知道合約地址
        init();
    }

    private void init() {
        Web3j web3j = Web3j.build(new HttpService());

        Credentials credentials = getCredentialsFromPrivateKey();
        System.out.println("Credentials: "+ credentials.getAddress());

        if (OWNER.equals("BK1")) { // BK1負責寫入合約，BK2、GW假設知道寫入後的位置
            try {
                // 第一次
                DeployedContract_ADDRESS = deployContract(web3j, credentials); // deployRemoteCall
                System.out.println("Deployed contract address: " + DeployedContract_ADDRESS + "\n");
                // 第二次以上
//                DeployedContract_ADDRESS = "0xa6ea3f188d51042f771af2cc1946d703f3d7a233";

                contract = loadContract(DeployedContract_ADDRESS, web3j, credentials); // new Contract()
                activateFilter_BK(web3j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (OWNER.equals("BK2")) {
            contract = loadContract(DeployedContract_ADDRESS, web3j, credentials);
            activateFilter_BK(web3j);
        } else if (OWNER.equals("GW")) {
            contract = loadContract(DeployedContract_ADDRESS, web3j, credentials);
            activateFilter_GW(web3j);
        } else {
            System.out.println("GG 3:0");
        }
    }

    private Credentials getCredentialsFromPrivateKey() {
        return Credentials.create(PRIVATE_KEY);
    }

    private String deployContract(Web3j web3j, Credentials credentials) throws Exception {
        return MyContract.deploy(web3j, credentials, GAS_PRICE, GAS_LIMIT)
                .send().getContractAddress();
    }

    private MyContract loadContract(String contractAddress, Web3j web3j, Credentials credentials) {
        return MyContract.load(contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    private void activateFilter_GW(Web3j web3j) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, DeployedContract_ADDRESS);

        web3j.ethLogObservable(filter).subscribe(log -> {
//            for (String element: hexToAscii(log.getData().substring(2)).split("#")) {
//                System.out.println(element);
//            }
//            System.out.println(log);
        });

        contract.demandEventObservable(filter).subscribe(log-> {
            if (log.GW_IPandID.equals(Gateway.GW_IP + "/" + Gateway.GW_Identifier)) {
                Gateway.BK_IPsandPKs.put(log.BK_IP, log.BK_PK);
                System.out.println("Filter: get one event BK_IP " + log.BK_IP + ", PK " + log.BK_PK);
            } else {
                System.out.println("Filter: not I write, Discard");
            }
        });
    }

    private void activateFilter_BK(Web3j web3j) {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, DeployedContract_ADDRESS);

        web3j.ethLogObservable(filter).subscribe(log -> {
//            System.out.println(log);
        });

        contract.demandEventObservable(filter).subscribe(log-> {
            if (log.BK_IP.equals(Broker.BK_IP)) {
                System.out.println("Filter: get one event topic is ... ");

                // 訂閱
                Iterator<OBJ> objIterator = Broker.obj_list.iterator();

                boolean added = false;
                while (objIterator.hasNext()) {
                    OBJ obj = objIterator.next();

                    if (log.GW_IPandID.equals(obj.getIPandID())) {
                        obj.put(log.manufacturer + "/" + log.device, MqttQoS.AT_LEAST_ONCE);
                        added = true;
                        System.out.println("OBJ already exist, success to add topic / update qos");
                        break;
                    }
                }

                if(!added) {
                    OBJ obj = new OBJ(log.GW_IPandID);
                    obj.put(log.manufacturer + "/" + log.device, MqttQoS.AT_LEAST_ONCE);
                    Broker.obj_list.add(obj);
                    System.out.println("add OBJ");
                }
            } else {
                System.out.println("Filter: not for me, discard");
            }
        });
    }

    public void Request(String _from, String _manufacturer, String _device, String _qos) throws Exception {
        contract.Request(_from, _manufacturer, _device, _qos).send();
    }

//    public Object Response(String _from, String _to, String _device, String _version) throws Exception {
//        return contract.Response(_from, _to, _device, _version).send();
//    }
}
