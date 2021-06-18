pragma solidity >=0.5.6;

contract DataSwapper {
    mapping(string => string) dataM; // map for accounts
    // change the address of Broker accordingly
    address BrokerAddr = 0x97135d4d2578dd2347FF5382db77553bE50bff3f;
    Broker broker = Broker(BrokerAddr);

    event LogDataExists(string key, string value);


    // AccessControl
    modifier onlyBroker {
        require(msg.sender == BrokerAddr, "Invoker are not the Broker");
        _;
    }

    // contract for data exchange
    function getData(string memory key) public returns(string memory) {
        string memory v = dataM[key];
        bytes memory str = bytes(v); // Uses memory
        if (str.length == 0) {
            return v;
        }
        emit LogDataExists(key, v);
        return v;
    }

    function setData(string memory key,string memory value) public{
        dataM[key]=value;
    }

    function get(address destChainID, string memory destAddr, string memory key) public {
        broker.emitInterchainEvent(destChainID, destAddr, "interchainGet,interchainSet, ", key, key, "");
    }

    function set(address destChainID, string memory destAddr, string memory key, string memory value) public {
        // stitch parameters
        string memory args = concat(toSlice(key), toSlice(","));
        args = concat(toSlice(args), toSlice(value));
        broker.emitInterchainEvent(destChainID, destAddr, "interchainSet, , ", args,"", "");
    }

    function interchainSet(string memory key, string memory value) public onlyBroker {
        setData(key, value);
    }

    function interchainGet(string memory key) public onlyBroker returns(bool, string memory) {
        return (true, dataM[key]);
    }

    struct slice {
        uint _len;
        uint _ptr;
    }

    function toSlice(string memory self) internal returns (slice memory) {
        uint ptr;
        assembly {
            ptr := add(self, 0x20)
        }
        return slice(bytes(self).length, ptr);
    }

    function concat(slice memory self, slice memory other) internal pure returns (string memory) {
        string memory ret = new string(self._len + other._len);
        uint retptr;
        assembly { retptr := add(ret, 32) }
        memcpy(retptr, self._ptr, self._len);
        memcpy(retptr + self._len, other._ptr, other._len);
        return ret;
    }

    function memcpy(uint dest, uint src, uint len) private pure {
        // Copy word-length chunks while possible
        for(; len >= 32; len -= 32) {
            assembly {
                mstore(dest, mload(src))
            }
            dest += 32;
            src += 32;
        }

        // Copy remaining bytes
        uint mask = 256 ** (32 - len) - 1;
        assembly {
            let srcpart := and(mload(src), not(mask))
            let destpart := and(mload(dest), mask)
            mstore(dest, or(destpart, srcpart))
        }
    }
}

contract Broker {
    function emitInterchainEvent(
        address destChainID,
        string memory destAddr,
        string memory funcs,
        string memory args,
        string memory argsCb,
        string memory argsRb) public;
}