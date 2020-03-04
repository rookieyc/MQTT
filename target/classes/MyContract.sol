pragma solidity >=0.4.21;

// solc MyContract.sol --bin --abi --optimize -o /web3j-3.5.0/bin/v4
// cd D:\web3j-3.5.0\bin
// web3j solidity generate v4/MyContract.bin v4/MyContract.abi -p v4 -o /web3j-3.5.0/bin

contract MyContract {

    string public_key;

    function Request(string memory GW_IPandID, string memory manufacturer, string memory device, string memory qos) public {
        emit Demand(msg.sender, GW_IPandID, manufacturer, device, qos, proxy(manufacturer), public_key);
    }
    event Demand(address addr, string GW_IPandID, string manufacturer, string device, string qos, string BK_IP, string BK_PK);

    function proxy(string memory manufacturer) public returns (string memory) {
	    if ( compareStrings(manufacturer, "manufacturerA") ) {
	        public_key = "PK1";
	        return "140.118.111.111";
        } else if ( compareStrings(manufacturer, "manufacturerB") ) {
            public_key = "PK2";
	        return "140.118.222.222";
	    } else if ( compareStrings(manufacturer, "manufacturerC") ) {
	        public_key = "PK3";
            return "140.118.333.333";
	    } else {
	        public_key = "PK4";
	        return "140.118.444.444";
	    }
    }

    function compareStrings(string memory a, string memory b) public pure returns (bool) {
        return (keccak256(abi.encodePacked((a))) == keccak256(abi.encodePacked((b))));
    }

    function Response(string memory from_ip, string memory device, string memory version, string memory qos) public {
        emit Deliver(msg.sender, from_ip, device, version, qos);
    }
    event Deliver(address addr, string from_ip, string device, string version, string qos);
}