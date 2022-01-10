package com.dmlab.cita.server.contracts;

import com.citahub.cita.abi.*;
import com.citahub.cita.abi.datatypes.Address;
import com.citahub.cita.abi.datatypes.Bool;
import com.citahub.cita.abi.datatypes.DynamicArray;
import com.citahub.cita.abi.datatypes.DynamicBytes;
import com.citahub.cita.abi.datatypes.Event;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.abi.datatypes.Utf8String;
import com.citahub.cita.abi.datatypes.generated.Bytes32;
import com.citahub.cita.abi.datatypes.generated.Uint64;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.RemoteCall;
import com.citahub.cita.protocol.core.methods.request.AppFilter;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tuples.generated.Tuple2;
import com.citahub.cita.tuples.generated.Tuple3;
import com.citahub.cita.tx.Contract;
import com.citahub.cita.tx.TransactionManager;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://github.com/citahub/citaj/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with citaj version 20.2.0.
 */
public class Broker extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b506040516200149938038062001499833981810160405281019062000037919062000096565b80600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505062000110565b6000815190506200009081620000f6565b92915050565b600060208284031215620000a957600080fd5b6000620000b9848285016200007f565b91505092915050565b6000620000cf82620000d6565b9050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6200010181620000c2565b81146200010d57600080fd5b50565b61137980620001206000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c80630491dbc51461005c57806317db41d01461008c5780633e10510b146100a8578063ae55c888146100c4578063e942b516146100f4575b600080fd5b61007660048036038101906100719190610aa4565b610110565b6040516100839190610e5f565b60405180910390f35b6100a660048036038101906100a19190610a63565b610332565b005b6100c260048036038101906100bd9190610b39565b6104a1565b005b6100de60048036038101906100d99190610af8565b61076d565b6040516100eb9190610e81565b60405180910390f35b61010e60048036038101906101099190610b39565b61081d565b005b6060600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146101a2576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161019990610f4c565b60405180910390fd5b60018351146101e6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101dd90610f8c565b60405180910390fd5b600083600081518110610222577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b602002602001015190506000600167ffffffffffffffff81111561026f577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6040519080825280602002602001820160405280156102a257816020015b606081526020019060019003908161028d5790505b5090506000826040516102b59190610e31565b90815260200160405180910390206040516020016102d39190610e48565b6040516020818303038152906040528160008151811061031c577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010181905250809250505092915050565b600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146103c2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103b990610f4c565b60405180910390fd5b6002815114610406576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016103fd90610f6c565b60405180910390fd5b600081600081518110610442577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b60200260200101519050600082600181518110610488577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010151905061049c828261081d565b505050565b6000600167ffffffffffffffff8111156104e4577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b60405190808252806020026020018201604052801561051757816020015b60608152602001906001900390816105025790505b5090508160405160200161052b9190610e31565b60405160208183030381529060405281600081518110610574577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b60200260200101819052506000600167ffffffffffffffff8111156105c2577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6040519080825280602002602001820160405280156105f557816020015b60608152602001906001900390816105e05790505b509050826040516020016106099190610e31565b60405160208183030381529060405281600081518110610652577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010181905250600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f4aa3077858484600067ffffffffffffffff8111156106df577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b60405190808252806020026020018201604052801561071257816020015b60608152602001906001900390816106fd5790505b5060006040518663ffffffff1660e01b8152600401610735959493929190610ea3565b600060405180830381600087803b15801561074f57600080fd5b505af1158015610763573d6000803e3d6000fd5b5050505050505050565b606060008260405161077f9190610e31565b908152602001604051809103902080546107989061113e565b80601f01602080910402602001604051908101604052809291908181526020018280546107c49061113e565b80156108115780601f106107e657610100808354040283529160200191610811565b820191906000526020600020905b8154815290600101906020018083116107f457829003601f168201915b50505050509050919050565b8060008360405161082e9190610e31565b9081526020016040518091039020908051906020019061084f929190610854565b505050565b8280546108609061113e565b90600052602060002090601f01602090048101928261088257600085556108c9565b82601f1061089b57805160ff19168380011785556108c9565b828001600101855582156108c9579182015b828111156108c85782518255916020019190600101906108ad565b5b5090506108d691906108da565b5090565b5b808211156108f35760008160009055506001016108db565b5090565b600061090a61090584610fd1565b610fac565b9050808382526020820190508260005b8581101561094a57813585016109308882610a0f565b84526020840193506020830192505060018101905061091a565b5050509392505050565b600061096761096284610ffd565b610fac565b90508281526020810184848401111561097f57600080fd5b61098a8482856110fc565b509392505050565b60006109a56109a08461102e565b610fac565b9050828152602081018484840111156109bd57600080fd5b6109c88482856110fc565b509392505050565b600082601f8301126109e157600080fd5b81356109f18482602086016108f7565b91505092915050565b600081359050610a098161132c565b92915050565b600082601f830112610a2057600080fd5b8135610a30848260208601610954565b91505092915050565b600082601f830112610a4a57600080fd5b8135610a5a848260208601610992565b91505092915050565b600060208284031215610a7557600080fd5b600082013567ffffffffffffffff811115610a8f57600080fd5b610a9b848285016109d0565b91505092915050565b60008060408385031215610ab757600080fd5b600083013567ffffffffffffffff811115610ad157600080fd5b610add858286016109d0565b9250506020610aee858286016109fa565b9150509250929050565b600060208284031215610b0a57600080fd5b600082013567ffffffffffffffff811115610b2457600080fd5b610b3084828501610a39565b91505092915050565b60008060408385031215610b4c57600080fd5b600083013567ffffffffffffffff811115610b6657600080fd5b610b7285828601610a39565b925050602083013567ffffffffffffffff811115610b8f57600080fd5b610b9b85828601610a39565b9150509250929050565b6000610bb18383610c3d565b905092915050565b6000610bc482611084565b610bce81856110b2565b935083602082028501610be08561105f565b8060005b85811015610c1c5784840389528151610bfd8582610ba5565b9450610c08836110a5565b925060208a01995050600181019050610be4565b50829750879550505050505092915050565b610c37816110f0565b82525050565b6000610c488261108f565b610c5281856110c3565b9350610c6281856020860161110b565b610c6b816111ff565b840191505092915050565b6000610c818261109a565b610c8b81856110d4565b9350610c9b81856020860161110b565b610ca4816111ff565b840191505092915050565b6000610cba8261109a565b610cc481856110e5565b9350610cd481856020860161110b565b80840191505092915050565b60008154610ced8161113e565b610cf781866110e5565b94506001821660008114610d125760018114610d2357610d56565b60ff19831686528186019350610d56565b610d2c8561106f565b60005b83811015610d4e57815481890152600182019150602081019050610d2f565b838801955050505b50505092915050565b6000610d6c600d836110d4565b9150610d7782611210565b602082019050919050565b6000610d8f601a836110d4565b9150610d9a82611239565b602082019050919050565b6000610db26033836110d4565b9150610dbd82611262565b604082019050919050565b6000610dd5600d836110d4565b9150610de0826112b1565b602082019050919050565b6000610df86033836110d4565b9150610e03826112da565b604082019050919050565b6000610e1b6000836110d4565b9150610e2682611329565b600082019050919050565b6000610e3d8284610caf565b915081905092915050565b6000610e548284610ce0565b915081905092915050565b60006020820190508181036000830152610e798184610bb9565b905092915050565b60006020820190508181036000830152610e9b8184610c76565b905092915050565b6000610100820190508181036000830152610ebe8188610c76565b90508181036020830152610ed181610d5f565b90508181036040830152610ee58187610bb9565b90508181036060830152610ef881610dc8565b90508181036080830152610f0c8186610bb9565b905081810360a0830152610f1f81610e0e565b905081810360c0830152610f338185610bb9565b9050610f4260e0830184610c2e565b9695505050505050565b60006020820190508181036000830152610f6581610d82565b9050919050565b60006020820190508181036000830152610f8581610da5565b9050919050565b60006020820190508181036000830152610fa581610deb565b9050919050565b6000610fb6610fc7565b9050610fc28282611170565b919050565b6000604051905090565b600067ffffffffffffffff821115610fec57610feb6111d0565b5b602082029050602081019050919050565b600067ffffffffffffffff821115611018576110176111d0565b5b611021826111ff565b9050602081019050919050565b600067ffffffffffffffff821115611049576110486111d0565b5b611052826111ff565b9050602081019050919050565b6000819050602082019050919050565b60008190508160005260206000209050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b600081905092915050565b60008115159050919050565b82818337600083830152505050565b60005b8381101561112957808201518184015260208101905061110e565b83811115611138576000848401525b50505050565b6000600282049050600182168061115657607f821691505b6020821081141561116a576111696111a1565b5b50919050565b611179826111ff565b810181811067ffffffffffffffff82111715611198576111976111d0565b5b80604052505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6000601f19601f8301169050919050565b7f696e746572636861696e47657400000000000000000000000000000000000000600082015250565b7f496e766f6b657220617265206e6f74207468652042726f6b6572000000000000600082015250565b7f696e746572636861696e536574206172677327206c656e677468206973206e6f60008201527f7420636f72726563742c20657870656374203200000000000000000000000000602082015250565b7f696e746572636861696e53657400000000000000000000000000000000000000600082015250565b7f696e746572636861696e476574206172677327206c656e677468206973206e6f60008201527f7420636f72726563742c20657870656374203100000000000000000000000000602082015250565b50565b611335816110f0565b811461134057600080fd5b5056fea264697066735822122023f442c8481ad9b20a4af582c22a1a0e1c61c7b976d8f95e3b8bcd41f0a1e06464736f6c63430008020033";

    private static final String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"chainID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"serviceID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"whiteList\",\"type\":\"address[]\",\"indexed\":false}],\"name\":\"registerRemoteService\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"outServicePair\",\"type\":\"string\",\"indexed\":false},{\"name\":\"idx\",\"type\":\"uint64\",\"indexed\":false}],\"name\":\"getOutMessage\",\"outputs\":[{\"name\":\"func\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"encrypt\",\"type\":\"bool\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"serviceID\",\"type\":\"string\",\"indexed\":false}],\"name\":\"genFullServiceID\",\"outputs\":[{\"name\":\"fullServiceID\",\"type\":\"string\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"chainID\",\"type\":\"string\",\"indexed\":false}],\"name\":\"getAppchainInfo\",\"outputs\":[{\"name\":\"broker\",\"type\":\"string\",\"indexed\":false},{\"name\":\"trustRoot\",\"type\":\"bytes\",\"indexed\":false},{\"name\":\"ruleAddr\",\"type\":\"address\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[],\"name\":\"getLocalServiceList\",\"outputs\":[{\"name\":\"fullServiceIDList\",\"type\":\"string[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[],\"name\":\"getCallbackMeta\",\"outputs\":[{\"name\":\"callbackServicePairs\",\"type\":\"string[]\",\"indexed\":false},{\"name\":\"indices\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"chainID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"broker\",\"type\":\"string\",\"indexed\":false},{\"name\":\"ruleAddr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"trustRoot\",\"type\":\"bytes\",\"indexed\":false}],\"name\":\"registerAppchain\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\",\"indexed\":false}],\"name\":\"register\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getChainID\",\"outputs\":[{\"name\":\"bitxhubID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"appchainID\",\"type\":\"string\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\",\"indexed\":false}],\"name\":\"addressToString\",\"outputs\":[{\"name\":\"asciiString\",\"type\":\"string\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[],\"name\":\"getInnerMeta\",\"outputs\":[{\"name\":\"inServicePairs\",\"type\":\"string[]\",\"indexed\":false},{\"name\":\"indices\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"srcAddr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"dstFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"typ\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"result\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"txStatus\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"signatures\",\"type\":\"bytes[]\",\"indexed\":false}],\"name\":\"invokeReceipt\",\"outputs\":[],\"type\":\"function\",\"payable\":true,\"stateMutability\":\"payable\"},{\"constant\":false,\"inputs\":[],\"name\":\"initialize\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getRemoteServiceList\",\"outputs\":[{\"name\":\"fullServiceID\",\"type\":\"string[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"status\",\"type\":\"int64\",\"indexed\":false}],\"name\":\"audit\",\"outputs\":[{\"name\":\"ok\",\"type\":\"bool\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"chainID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"serviceID\",\"type\":\"string\",\"indexed\":false}],\"name\":\"genRemoteFullServiceID\",\"outputs\":[{\"name\":\"fullServiceID\",\"type\":\"string\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[],\"name\":\"getOuterMeta\",\"outputs\":[{\"name\":\"outServicePairs\",\"type\":\"string[]\",\"indexed\":false},{\"name\":\"indices\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"inServicePair\",\"type\":\"string\",\"indexed\":false},{\"name\":\"idx\",\"type\":\"uint64\",\"indexed\":false}],\"name\":\"getReceiptMessage\",\"outputs\":[{\"name\":\"result\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"typ\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"encrypt\",\"type\":\"bool\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"srcFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"destAddr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"typ\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"callFunc\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"txStatus\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"signatures\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"isEncrypt\",\"type\":\"bool\",\"indexed\":false}],\"name\":\"invokeInterchain\",\"outputs\":[],\"type\":\"function\",\"payable\":true,\"stateMutability\":\"payable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getDstRollbackMeta\",\"outputs\":[{\"name\":\"inServicePairs\",\"type\":\"string[]\",\"indexed\":false},{\"name\":\"indices\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"destFullServiceID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"funcCall\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"funcCb\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argsCb\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"funcRb\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argsRb\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"isEncrypt\",\"type\":\"bool\",\"indexed\":false}],\"name\":\"emitInterchainEvent\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"_bitxhubID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"_appchainID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"_validators\",\"type\":\"address[]\",\"indexed\":false},{\"name\":\"_valThreshold\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"_admins\",\"type\":\"address[]\",\"indexed\":false},{\"name\":\"_adminThreshold\",\"type\":\"uint64\",\"indexed\":false}],\"name\":null,\"outputs\":null,\"type\":\"constructor\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"dstFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"srcFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"func\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"hash\",\"type\":\"bytes32\",\"indexed\":false}],\"name\":\"throwInterchainEvent\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null},{\"constant\":false,\"inputs\":[{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"dstFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"srcFullID\",\"type\":\"string\",\"indexed\":false},{\"name\":\"typ\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"status\",\"type\":\"bool\",\"indexed\":false},{\"name\":\"result\",\"type\":\"bytes[]\",\"indexed\":false},{\"name\":\"hash\",\"type\":\"bytes32\",\"indexed\":false}],\"name\":\"throwReceiptEvent\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null},{\"constant\":false,\"inputs\":[{\"name\":\"ok\",\"type\":\"bool\",\"indexed\":false}],\"name\":\"throwReceiptStatus\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null}]";

    protected Broker(String contractAddress, CITAj citaj, TransactionManager transactionManager) {
        super(BINARY, contractAddress, citaj, transactionManager);
    }

    public List<ThrowInterchainEventEventResponse> getThrowInterchainEventEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("throwInterchainEvent",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Bytes32>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ThrowInterchainEventEventResponse> responses = new ArrayList<ThrowInterchainEventEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ThrowInterchainEventEventResponse typedResponse = new ThrowInterchainEventEventResponse();
            typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.dstFullID = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.srcFullID = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.func = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.args = (List<byte[]>) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.hash = (byte[]) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ThrowInterchainEventEventResponse> throwInterchainEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("throwInterchainEvent",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Bytes32>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, ThrowInterchainEventEventResponse>() {
            @Override
            public ThrowInterchainEventEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ThrowInterchainEventEventResponse typedResponse = new ThrowInterchainEventEventResponse();
                typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.dstFullID = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.srcFullID = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.func = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.args = (List<byte[]>) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.hash = (byte[]) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public List<ThrowReceiptEventEventResponse> getThrowReceiptEventEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("throwReceiptEvent",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint64>() {}, new TypeReference<Bool>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Bytes32>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ThrowReceiptEventEventResponse> responses = new ArrayList<ThrowReceiptEventEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ThrowReceiptEventEventResponse typedResponse = new ThrowReceiptEventEventResponse();
            typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.dstFullID = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.srcFullID = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.typ = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.result = (List<byte[]>) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.hash = (byte[]) eventValues.getNonIndexedValues().get(6).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ThrowReceiptEventEventResponse> throwReceiptEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("throwReceiptEvent",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint64>() {}, new TypeReference<Bool>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Bytes32>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, ThrowReceiptEventEventResponse>() {
            @Override
            public ThrowReceiptEventEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ThrowReceiptEventEventResponse typedResponse = new ThrowReceiptEventEventResponse();
                typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.dstFullID = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.srcFullID = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.typ = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.result = (List<byte[]>) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.hash = (byte[]) eventValues.getNonIndexedValues().get(6).getValue();
                return typedResponse;
            }
        });
    }

    public List<ThrowReceiptStatusEventResponse> getThrowReceiptStatusEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("throwReceiptStatus",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ThrowReceiptStatusEventResponse> responses = new ArrayList<ThrowReceiptStatusEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ThrowReceiptStatusEventResponse typedResponse = new ThrowReceiptStatusEventResponse();
            typedResponse.ok = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ThrowReceiptStatusEventResponse> throwReceiptStatusEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("throwReceiptStatus",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, ThrowReceiptStatusEventResponse>() {
            @Override
            public ThrowReceiptStatusEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ThrowReceiptStatusEventResponse typedResponse = new ThrowReceiptStatusEventResponse();
                typedResponse.ok = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> registerRemoteService(String chainID, String serviceID, List<String> whiteList, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "registerRemoteService",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(chainID),
                        new com.citahub.cita.abi.datatypes.Utf8String(serviceID),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.Address>(
                                Utils.typeMap(whiteList, com.citahub.cita.abi.datatypes.Address.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple3<String, List<byte[]>, Boolean>> getOutMessage(String outServicePair, BigInteger idx) {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getOutMessage",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(outServicePair),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(idx)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple3<String, List<byte[]>, Boolean>>(
                new Callable<Tuple3<String, List<byte[]>, Boolean>>() {
                    @Override
                    public Tuple3<String, List<byte[]>, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<String, List<byte[]>, Boolean>(
                                (String) results.get(0).getValue(),
                                convertToNative((List<DynamicBytes>) results.get(1).getValue()),
                                (Boolean) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<String> genFullServiceID(String serviceID) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("genFullServiceID",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(serviceID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple3<String, byte[], String>> getAppchainInfo(String chainID) {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getAppchainInfo",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(chainID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Address>() {}));
        return new RemoteCall<Tuple3<String, byte[], String>>(
                new Callable<Tuple3<String, byte[], String>>() {
                    @Override
                    public Tuple3<String, byte[], String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<String, byte[], String>(
                                (String) results.get(0).getValue(),
                                (byte[]) results.get(1).getValue(),
                                (String) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<List> getLocalServiceList() {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getLocalServiceList",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return executeRemoteCallSingleValueReturn(function, List.class);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getCallbackMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getCallbackMeta",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()),
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> registerAppchain(String chainID, String broker, String ruleAddr, byte[] trustRoot, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "registerAppchain",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(chainID),
                        new com.citahub.cita.abi.datatypes.Utf8String(broker),
                        new com.citahub.cita.abi.datatypes.Address(ruleAddr),
                        new com.citahub.cita.abi.datatypes.DynamicBytes(trustRoot)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<TransactionReceipt> register(String addr, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "register",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Address(addr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple2<String, String>> getChainID() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getChainID",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple2<String, String>>(
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(),
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<String> addressToString(String account) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("addressToString",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getInnerMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getInnerMeta",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()),
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> invokeReceipt(String srcAddr, String dstFullID, BigInteger index, BigInteger typ, List<byte[]> result, BigInteger txStatus, List<byte[]> signatures, BigInteger weiValue, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "invokeReceipt",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Address(srcAddr),
                        new com.citahub.cita.abi.datatypes.Utf8String(dstFullID),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(index),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(typ),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(result, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(txStatus),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(signatures, com.citahub.cita.abi.datatypes.DynamicBytes.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<TransactionReceipt> initialize(Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "initialize",
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<List> getRemoteServiceList() {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getRemoteServiceList",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return executeRemoteCallSingleValueReturn(function, List.class);
    }

    public RemoteCall<TransactionReceipt> audit(String addr, BigInteger status, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "audit",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Address(addr),
                        new com.citahub.cita.abi.datatypes.generated.Int64(status)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<String> genRemoteFullServiceID(String chainID, String serviceID) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("genRemoteFullServiceID",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(chainID),
                        new com.citahub.cita.abi.datatypes.Utf8String(serviceID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getOuterMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getOuterMeta",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()),
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<Tuple3<List<byte[]>, BigInteger, Boolean>> getReceiptMessage(String inServicePair, BigInteger idx) {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getReceiptMessage",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(inServicePair),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(idx)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<DynamicBytes>>() {}, new TypeReference<Uint64>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple3<List<byte[]>, BigInteger, Boolean>>(
                new Callable<Tuple3<List<byte[]>, BigInteger, Boolean>>() {
                    @Override
                    public Tuple3<List<byte[]>, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<List<byte[]>, BigInteger, Boolean>(
                                convertToNative((List<DynamicBytes>) results.get(0).getValue()),
                                (BigInteger) results.get(1).getValue(),
                                (Boolean) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> invokeInterchain(String srcFullID, String destAddr, BigInteger index, BigInteger typ, String callFunc, List<byte[]> args, BigInteger txStatus, List<byte[]> signatures, Boolean isEncrypt, BigInteger weiValue, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "invokeInterchain",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(srcFullID),
                        new com.citahub.cita.abi.datatypes.Address(destAddr),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(index),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(typ),
                        new com.citahub.cita.abi.datatypes.Utf8String(callFunc),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(args, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.generated.Uint64(txStatus),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(signatures, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.Bool(isEncrypt)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getDstRollbackMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getDstRollbackMeta",
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()),
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> emitInterchainEvent(String destFullServiceID, String funcCall, List<byte[]> args, String funcCb, List<byte[]> argsCb, String funcRb, List<byte[]> argsRb, Boolean isEncrypt, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "emitInterchainEvent",
                Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(destFullServiceID),
                        new com.citahub.cita.abi.datatypes.Utf8String(funcCall),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(args, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.Utf8String(funcCb),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(argsCb, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.Utf8String(funcRb),
                        new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.DynamicBytes>(
                                Utils.typeMap(argsRb, com.citahub.cita.abi.datatypes.DynamicBytes.class)),
                        new com.citahub.cita.abi.datatypes.Bool(isEncrypt)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public static RemoteCall<Broker> deploy(CITAj citaj, TransactionManager transactionManager, Long quota, String nonce, Long validUntilBlock, Integer version, String value, BigInteger chainId, String _bitxhubID, String _appchainID, List<String> _validators, BigInteger _valThreshold, List<String> _admins, BigInteger _adminThreshold) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(_bitxhubID),
                new com.citahub.cita.abi.datatypes.Utf8String(_appchainID),
                new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.Address>(
                        Utils.typeMap(_validators, com.citahub.cita.abi.datatypes.Address.class)),
                new com.citahub.cita.abi.datatypes.generated.Uint64(_valThreshold),
                new com.citahub.cita.abi.datatypes.DynamicArray<com.citahub.cita.abi.datatypes.Address>(
                        Utils.typeMap(_admins, com.citahub.cita.abi.datatypes.Address.class)),
                new com.citahub.cita.abi.datatypes.generated.Uint64(_adminThreshold)));
        return deployRemoteCall(Broker.class, citaj, transactionManager, quota, nonce, validUntilBlock, version, chainId, value, BINARY, encodedConstructor);
    }

    public static Broker load(String contractAddress, CITAj citaj, TransactionManager transactionManager) {
        return new Broker(contractAddress, citaj, transactionManager);
    }

    public static class ThrowInterchainEventEventResponse {
        public BigInteger index;

        public String dstFullID;

        public String srcFullID;

        public String func;

        public List<byte[]> args;

        public byte[] hash;
    }

    public static class ThrowReceiptEventEventResponse {
        public BigInteger index;

        public String dstFullID;

        public String srcFullID;

        public BigInteger typ;

        public Boolean status;

        public List<byte[]> result;

        public byte[] hash;
    }

    public static class ThrowReceiptStatusEventResponse {
        public Boolean ok;
    }
}
