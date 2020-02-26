package v3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class MyContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610f88806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80634971dcf6146100515780634d97addc14610286578063bed34bba1461039f578063d8cc6d78146104dc575b600080fd5b6102846004803603608081101561006757600080fd5b810190602081018135600160201b81111561008157600080fd5b82018360208201111561009357600080fd5b803590602001918460018302840111600160201b831117156100b457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561010657600080fd5b82018360208201111561011857600080fd5b803590602001918460018302840111600160201b8311171561013957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561018b57600080fd5b82018360208201111561019d57600080fd5b803590602001918460018302840111600160201b831117156101be57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561021057600080fd5b82018360208201111561022257600080fd5b803590602001918460018302840111600160201b8311171561024357600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061070f945050505050565b005b61032a6004803603602081101561029c57600080fd5b810190602081018135600160201b8111156102b657600080fd5b8201836020820111156102c857600080fd5b803590602001918460018302840111600160201b831117156102e957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506109e8945050505050565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561036457818101518382015260200161034c565b50505050905090810190601f1680156103915780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6104c8600480360360408110156103b557600080fd5b810190602081018135600160201b8111156103cf57600080fd5b8201836020820111156103e157600080fd5b803590602001918460018302840111600160201b8311171561040257600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561045457600080fd5b82018360208201111561046657600080fd5b803590602001918460018302840111600160201b8311171561048757600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610be3945050505050565b604080519115158252519081900360200190f35b610284600480360360808110156104f257600080fd5b810190602081018135600160201b81111561050c57600080fd5b82018360208201111561051e57600080fd5b803590602001918460018302840111600160201b8311171561053f57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561059157600080fd5b8201836020820111156105a357600080fd5b803590602001918460018302840111600160201b831117156105c457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561061657600080fd5b82018360208201111561062857600080fd5b803590602001918460018302840111600160201b8311171561064957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561069b57600080fd5b8201836020820111156106ad57600080fd5b803590602001918460018302840111600160201b831117156106ce57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610cca945050505050565b7f8b38e5ebb224b3591527f5a91573772679e582627d6b4ec01136a9f344b779d3338585858561073e896109e8565b600060405180886001600160a01b03166001600160a01b0316815260200180602001806020018060200180602001806020018060200187810387528d818151815260200191508051906020019080838360005b838110156107a9578181015183820152602001610791565b50505050905090810190601f1680156107d65780820380516001836020036101000a031916815260200191505b5087810386528c5181528c516020918201918e019080838360005b838110156108095781810151838201526020016107f1565b50505050905090810190601f1680156108365780820380516001836020036101000a031916815260200191505b5087810385528b5181528b516020918201918d019080838360005b83811015610869578181015183820152602001610851565b50505050905090810190601f1680156108965780820380516001836020036101000a031916815260200191505b5087810384528a5181528a516020918201918c019080838360005b838110156108c95781810151838201526020016108b1565b50505050905090810190601f1680156108f65780820380516001836020036101000a031916815260200191505b5087810383528951815289516020918201918b019080838360005b83811015610929578181015183820152602001610911565b50505050905090810190601f1680156109565780820380516001836020036101000a031916815260200191505b508781038252885460026000196101006001841615020190911604808252602090910190899080156109c95780601f1061099e576101008083540402835291602001916109c9565b820191906000526020600020905b8154815290600101906020018083116109ac57829003601f168201915b50509d505050505050505050505050505060405180910390a150505050565b6060610a19826040518060400160405280600d81526020016c6d616e7566616374757265724160981b815250610be3565b15610a755760408051808201909152600380825262504b3160e81b6020909201918252610a4891600091610eb8565b505060408051808201909152600f81526e3134302e3131382e3131312e31313160881b6020820152610bde565b610aa4826040518060400160405280600d81526020016c36b0b73ab330b1ba3ab932b92160991b815250610be3565b15610b00576040805180820190915260038082526228259960e91b6020909201918252610ad391600091610eb8565b505060408051808201909152600f81526e189a181718989c171919191719191960891b6020820152610bde565b610b2f826040518060400160405280600d81526020016c6d616e7566616374757265724360981b815250610be3565b15610b8b5760408051808201909152600380825262504b3360e81b6020909201918252610b5e91600091610eb8565b505060408051808201909152600f81526e3134302e3131382e3333332e33333360881b6020820152610bde565b604080518082019091526003808252621412cd60ea1b6020909201918252610bb591600091610eb8565b505060408051808201909152600f81526e0c4d0c0b8c4c4e0b8d0d0d0b8d0d0d608a1b60208201525b919050565b6000816040516020018082805190602001908083835b60208310610c185780518252601f199092019160209182019101610bf9565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405160208183030381529060405280519060200120836040516020018082805190602001908083835b60208310610c865780518252601f199092019160209182019101610c67565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040528051906020012014905092915050565b7f9436bf75d8be900167264e28525a388194222afb880c2e2467154eab68781dfb338585858560405180866001600160a01b03166001600160a01b0316815260200180602001806020018060200180602001858103855289818151815260200191508051906020019080838360005b83811015610d51578181015183820152602001610d39565b50505050905090810190601f168015610d7e5780820380516001836020036101000a031916815260200191505b5085810384528851815288516020918201918a019080838360005b83811015610db1578181015183820152602001610d99565b50505050905090810190601f168015610dde5780820380516001836020036101000a031916815260200191505b50858103835287518152875160209182019189019080838360005b83811015610e11578181015183820152602001610df9565b50505050905090810190601f168015610e3e5780820380516001836020036101000a031916815260200191505b50858103825286518152865160209182019188019080838360005b83811015610e71578181015183820152602001610e59565b50505050905090810190601f168015610e9e5780820380516001836020036101000a031916815260200191505b50995050505050505050505060405180910390a150505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610ef957805160ff1916838001178555610f26565b82800160010185558215610f26579182015b82811115610f26578251825591602001919060010190610f0b565b50610f32929150610f36565b5090565b610f5091905b80821115610f325760008155600101610f3c565b9056fea265627a7a72315820af47a46021591891959b7137295da7ca047493623faa33ee883cdef499750dbd64736f6c634300050c0032";

    public static final String FUNC_REQUEST = "Request";

    public static final String FUNC_RESPONSE = "Response";

    public static final String FUNC_COMPARESTRINGS = "compareStrings";

    public static final String FUNC_PROXY = "proxy";

    public static final Event DELIVER_EVENT = new Event("Deliver", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event DEMAND_EVENT = new Event("Demand", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected MyContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MyContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<DeliverEventResponse> getDeliverEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DELIVER_EVENT, transactionReceipt);
        ArrayList<DeliverEventResponse> responses = new ArrayList<DeliverEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DeliverEventResponse typedResponse = new DeliverEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.from_ip = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.device = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.version = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.qos = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DeliverEventResponse> deliverEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, DeliverEventResponse>() {
            @Override
            public DeliverEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DELIVER_EVENT, log);
                DeliverEventResponse typedResponse = new DeliverEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.from_ip = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.device = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.version = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.qos = (String) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<DeliverEventResponse> deliverEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DELIVER_EVENT));
        return deliverEventObservable(filter);
    }

    public List<DemandEventResponse> getDemandEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEMAND_EVENT, transactionReceipt);
        ArrayList<DemandEventResponse> responses = new ArrayList<DemandEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DemandEventResponse typedResponse = new DemandEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.GW_IPandID = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.manufacturer = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.device = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.qos = (String) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.to_ip = (String) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.pk = (String) eventValues.getNonIndexedValues().get(6).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<DemandEventResponse> demandEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, DemandEventResponse>() {
            @Override
            public DemandEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEMAND_EVENT, log);
                DemandEventResponse typedResponse = new DemandEventResponse();
                typedResponse.log = log;
                typedResponse.addr = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.GW_IPandID = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.manufacturer = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.device = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.qos = (String) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.to_ip = (String) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.pk = (String) eventValues.getNonIndexedValues().get(6).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<DemandEventResponse> demandEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEMAND_EVENT));
        return demandEventObservable(filter);
    }

    public RemoteCall<TransactionReceipt> Request(String GW_IPandID, String manufacturer, String device, String qos) {
        final Function function = new Function(
                FUNC_REQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(GW_IPandID), 
                new org.web3j.abi.datatypes.Utf8String(manufacturer), 
                new org.web3j.abi.datatypes.Utf8String(device), 
                new org.web3j.abi.datatypes.Utf8String(qos)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> Response(String from_ip, String device, String version, String qos) {
        final Function function = new Function(
                FUNC_RESPONSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(from_ip), 
                new org.web3j.abi.datatypes.Utf8String(device), 
                new org.web3j.abi.datatypes.Utf8String(version), 
                new org.web3j.abi.datatypes.Utf8String(qos)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> compareStrings(String a, String b) {
        final Function function = new Function(FUNC_COMPARESTRINGS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(a), 
                new org.web3j.abi.datatypes.Utf8String(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> proxy(String manufacturer) {
        final Function function = new Function(
                FUNC_PROXY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(manufacturer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<MyContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MyContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MyContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MyContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static MyContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MyContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MyContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MyContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class DeliverEventResponse {
        public Log log;

        public String addr;

        public String from_ip;

        public String device;

        public String version;

        public String qos;
    }

    public static class DemandEventResponse {
        public Log log;

        public String addr;

        public String GW_IPandID;

        public String manufacturer;

        public String device;

        public String qos;

        public String to_ip;

        public String pk;
    }
}
