package com.dmlab.cita.server.contracts;

import com.citahub.cita.abi.EventEncoder;
import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.*;
import com.citahub.cita.abi.datatypes.generated.Uint256;
import com.citahub.cita.abi.datatypes.generated.Uint64;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.RemoteCall;
import com.citahub.cita.protocol.core.methods.request.AppFilter;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tuples.generated.Tuple2;
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
    private static final String BINARY = "608060405234801561001057600080fd5b50612390806100206000396000f3fe60806040526004361061009c5760003560e01c80637cf636ce116100645780637cf636ce146106775780638129fc1c1461077f57806383c44c2714610796578063a0342a3f1461080f578063b38ff85f14610888578063c20cab50146108fe5761009c565b80633aabe619146100a15780633b6bbe4a1461017a5780634420e4861461022e57806350b0b9021461027f57806367b9fa3b146105c3575b600080fd5b610178600480360360a08110156100b757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803567ffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035151590602001909291908035906020019064010000000081111561013457600080fd5b82018360208201111561014657600080fd5b8035906020019184600183028401116401000000008311171561016857600080fd5b90919293919293905050506109b2565b005b34801561018657600080fd5b5061018f610a67565b604051808060200180602001838103835285818151815260200191508051906020019060200280838360005b838110156101d65780820151818401526020810190506101bb565b50505050905001838103825284818151815260200191508051906020019060200280838360005b838110156102185780820151818401526020810190506101fd565b5050505090500194505050505060405180910390f35b34801561023a57600080fd5b5061027d6004803603602081101561025157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610c2f565b005b34801561028b57600080fd5b506105c1600480360360c08110156102a257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001906401000000008111156102df57600080fd5b8201836020820111156102f157600080fd5b8035906020019184600183028401116401000000008311171561031357600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192908035906020019064010000000081111561037657600080fd5b82018360208201111561038857600080fd5b803590602001918460018302840111640100000000831117156103aa57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192908035906020019064010000000081111561040d57600080fd5b82018360208201111561041f57600080fd5b8035906020019184600183028401116401000000008311171561044157600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290803590602001906401000000008111156104a457600080fd5b8201836020820111156104b657600080fd5b803590602001918460018302840111640100000000831117156104d857600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192908035906020019064010000000081111561053b57600080fd5b82018360208201111561054d57600080fd5b8035906020019184600183028401116401000000008311171561056f57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050610c9b565b005b3480156105cf57600080fd5b506105d861129b565b604051808060200180602001838103835285818151815260200191508051906020019060200280838360005b8381101561061f578082015181840152602081019050610604565b50505050905001838103825284818151815260200191508051906020019060200280838360005b83811015610661578082015181840152602081019050610646565b5050505090500194505050505060405180910390f35b34801561068357600080fd5b5061077d6004803603608081101561069a57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803567ffffffffffffffff169060200190929190803515159060200190929190803590602001906401000000008111156106f757600080fd5b82018360208201111561070957600080fd5b8035906020019184600183028401116401000000008311171561072b57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050509192919290505050611445565b005b34801561078b57600080fd5b50610794611457565b005b3480156107a257600080fd5b506107f9600480360360408110156107b957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803567ffffffffffffffff169060200190929190505050611781565b6040518082815260200191505060405180910390f35b34801561081b57600080fd5b506108726004803603604081101561083257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803567ffffffffffffffff1690602001909291905050506117f0565b6040518082815260200191505060405180910390f35b34801561089457600080fd5b506108e4600480360360408110156108ab57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803560070b906020019092919050505061185f565b604051808215151515815260200191505060405180910390f35b34801561090a57600080fd5b5061091361199c565b604051808060200180602001838103835285818151815260200191508051906020019060200280838360005b8381101561095a57808201518184015260208101905061093f565b50505050905001838103825284818151815260200191508051906020019060200280838360005b8381101561099c578082015181840152602081019050610981565b5050505090500194505050505060405180910390f35b60016000808673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460070b60070b14610a0d57600080fd5b610a2886868560405180602001604052806000815250611b64565b604051602060848237805160208160040183378151808260240184376000808285348b5af13d806000863e8160008114610a63578186a08186f35b8186fd5b6060806060600580549050604051908082528060200260200182016040528015610aa05781602001602082028038833980820191505090505b50905060008090505b6005805490508167ffffffffffffffff161015610b9a57600a600060058367ffffffffffffffff1681548110610adb57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff16828267ffffffffffffffff1681518110610b6b57fe5b602002602001019067ffffffffffffffff16908167ffffffffffffffff16815250508080600101915050610aa9565b5060058181805480602002602001604051908101604052809291908181526020018280548015610c1f57602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019060010190808311610bd5575b5050505050915092509250509091565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908360070b67ffffffffffffffff16021790555050565b60016000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460070b60070b14610d5f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601d8152602001807f496e766f6b657220617265206e6f7420696e207768697465206c69737400000081525060200191505060405180910390fd5b600660008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600081819054906101000a900467ffffffffffffffff168092919060010191906101000a81548167ffffffffffffffff021916908367ffffffffffffffff160217905550506001600660008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff161415610eb05760038690806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b43600760008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000600660008a73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff1667ffffffffffffffff168152602001908152602001600020819055507fdde0d454bdf1d147a0842ac1864ecc133506af30efc60d34dabc910267c4e40a600660008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1687338888888888604051808967ffffffffffffffff1667ffffffffffffffff1681526020018873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001806020018060200180602001806020018060200186810386528b818151815260200191508051906020019080838360005b838110156110b657808201518184015260208101905061109b565b50505050905090810190601f1680156110e35780820380516001836020036101000a031916815260200191505b5086810385528a818151815260200191508051906020019080838360005b8381101561111c578082015181840152602081019050611101565b50505050905090810190601f1680156111495780820380516001836020036101000a031916815260200191505b50868103845289818151815260200191508051906020019080838360005b83811015611182578082015181840152602081019050611167565b50505050905090810190601f1680156111af5780820380516001836020036101000a031916815260200191505b50868103835288818151815260200191508051906020019080838360005b838110156111e85780820151818401526020810190506111cd565b50505050905090810190601f1680156112155780820380516001836020036101000a031916815260200191505b50868103825287818151815260200191508051906020019080838360005b8381101561124e578082015181840152602081019050611233565b50505050905090810190601f16801561127b5780820380516001836020036101000a031916815260200191505b509d505050505050505050505050505060405180910390a1505050505050565b60608060606004805490506040519080825280602002602001820160405280156112d45781602001602082028038833980820191505090505b50905060008090505b6004805490508110156113b05760086000600483815481106112fb57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1682828151811061138157fe5b602002602001019067ffffffffffffffff16908167ffffffffffffffff168152505080806001019150506112dd565b506004818180548060200260200160405190810160405280929190818152602001828054801561143557602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190600101908083116113eb575b5050505050915092509250509091565b61145184848484611b64565b50505050565b60008090505b600480549050811015611515576000600860006004848154811061147d57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908367ffffffffffffffff160217905550808060010191505061145d565b5060008090505b6003805490508110156115d4576000600660006003848154811061153c57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908367ffffffffffffffff160217905550808060010191505061151c565b5060008090505b600580549050811015611693576000600a6000600584815481106115fb57fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908367ffffffffffffffff16021790555080806001019150506115db565b5060008090505b600180549050811015611754576000806000600184815481106116b957fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908360070b67ffffffffffffffff160217905550808060010191505061169a565b5060036000611763919061229e565b60046000611771919061229e565b6005600061177f919061229e565b565b6000600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008367ffffffffffffffff1667ffffffffffffffff16815260200190815260200160002054905092915050565b6000600760008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008367ffffffffffffffff1667ffffffffffffffff16815260200190815260200160002054905092915050565b60007fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8260070b14158015611898575060008260070b14155b80156118a8575060018260070b14155b156118b65760009050611996565b816000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908360070b67ffffffffffffffff16021790555060018260070b14156119915760018390806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b600190505b92915050565b60608060606003805490506040519080825280602002602001820160405280156119d55781602001602082028038833980820191505090505b50905060008090505b6003805490508167ffffffffffffffff161015611acf576006600060038367ffffffffffffffff1681548110611a1057fe5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff16828267ffffffffffffffff1681518110611aa057fe5b602002602001019067ffffffffffffffff16908167ffffffffffffffff168152505080806001019150506119de565b5060038181805480602002602001604051908101604052809291908181526020018280548015611b5457602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019060010190808311611b0a575b5050505050915092509250509091565b8115611d01578267ffffffffffffffff166001600860008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff160167ffffffffffffffff1614611be057600080fd5b611be984611e9b565b60405160200180600001905060405160208183030381529060405280519060200120816040516020018082805190602001908083835b60208310611c425780518252602082019150602081019050602083039250611c1f565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040528051906020012014611cfc5780600b60008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008567ffffffffffffffff1667ffffffffffffffff1681526020019081526020016000209080519060200190611cfa9291906122bf565b505b611e95565b8267ffffffffffffffff166001600a60008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff160167ffffffffffffffff1614611d7757600080fd5b611d8184846120ab565b60405160200180600001905060405160208183030381529060405280519060200120816040516020018082805190602001908083835b60208310611dda5780518252602082019150602081019050602083039250611db7565b6001836020036101000a0380198251168184511680821785525050505050509050019150506040516020818303038152906040528051906020012014611e945780600c60008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008567ffffffffffffffff1667ffffffffffffffff1681526020019081526020016000209080519060200190611e929291906122bf565b505b5b50505050565b600860008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600081819054906101000a900467ffffffffffffffff168092919060010191906101000a81548167ffffffffffffffff021916908367ffffffffffffffff160217905550506001600860008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff161415611fec5760048190806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b43600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000600860008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff1667ffffffffffffffff1681526020019081526020016000208190555050565b6000600a60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff1614156121785760058290806001815401808255809150509060018203906000526020600020016000909192909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550505b80600a60008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548167ffffffffffffffff021916908367ffffffffffffffff16021790555043600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000600a60008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900467ffffffffffffffff1667ffffffffffffffff1667ffffffffffffffff168152602001908152602001600020819055505050565b50805460008255906000526020600020908101906122bc919061233f565b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061230057805160ff191683800117855561232e565b8280016001018555821561232e579182015b8281111561232d578251825591602001919060010190612312565b5b50905061233b919061233f565b5090565b61236191905b8082111561235d576000816000905550600101612345565b5090565b9056fea165627a7a723058205ce7ec136c633f9f804faa7c1cd239a427741b48432c3413bf832d914b2038680029";

    private static final String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"srcChainID\",\"type\":\"address\",\"indexed\":false},{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"destAddr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"req\",\"type\":\"bool\",\"indexed\":false},{\"name\":\"bizCallData\",\"type\":\"bytes\",\"indexed\":false}],\"name\":\"invokeInterchain\",\"outputs\":[],\"type\":\"function\",\"payable\":true,\"stateMutability\":\"payable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getCallbackMeta\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\",\"indexed\":false},{\"name\":\"\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\",\"indexed\":false}],\"name\":\"register\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[{\"name\":\"destChainID\",\"type\":\"address\",\"indexed\":false},{\"name\":\"destAddr\",\"type\":\"string\",\"indexed\":false},{\"name\":\"funcs\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argscb\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argsrb\",\"type\":\"string\",\"indexed\":false}],\"name\":\"emitInterchainEvent\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getInnerMeta\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\",\"indexed\":false},{\"name\":\"\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"srcChainID\",\"type\":\"address\",\"indexed\":false},{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"req\",\"type\":\"bool\",\"indexed\":false},{\"name\":\"err\",\"type\":\"string\",\"indexed\":false}],\"name\":\"invokeIndexUpdateWithError\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":false,\"inputs\":[],\"name\":\"initialize\",\"outputs\":[],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[{\"name\":\"from\",\"type\":\"address\",\"indexed\":false},{\"name\":\"idx\",\"type\":\"uint64\",\"indexed\":false}],\"name\":\"getInMessage\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":true,\"inputs\":[{\"name\":\"to\",\"type\":\"address\",\"indexed\":false},{\"name\":\"idx\",\"type\":\"uint64\",\"indexed\":false}],\"name\":\"getOutMessage\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\",\"indexed\":false},{\"name\":\"status\",\"type\":\"int64\",\"indexed\":false}],\"name\":\"audit\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"nonpayable\"},{\"constant\":true,\"inputs\":[],\"name\":\"getOuterMeta\",\"outputs\":[{\"name\":\"\",\"type\":\"address[]\",\"indexed\":false},{\"name\":\"\",\"type\":\"uint64[]\",\"indexed\":false}],\"type\":\"function\",\"payable\":false,\"stateMutability\":\"view\"},{\"constant\":false,\"inputs\":[{\"name\":\"index\",\"type\":\"uint64\",\"indexed\":false},{\"name\":\"to\",\"type\":\"address\",\"indexed\":false},{\"name\":\"fid\",\"type\":\"address\",\"indexed\":false},{\"name\":\"tid\",\"type\":\"string\",\"indexed\":false},{\"name\":\"funcs\",\"type\":\"string\",\"indexed\":false},{\"name\":\"args\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argscb\",\"type\":\"string\",\"indexed\":false},{\"name\":\"argsrb\",\"type\":\"string\",\"indexed\":false}],\"name\":\"throwEvent\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null},{\"constant\":false,\"inputs\":[{\"name\":\"status\",\"type\":\"bool\",\"indexed\":false},{\"name\":\"data\",\"type\":\"string\",\"indexed\":false}],\"name\":\"LogInterchainData\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null},{\"constant\":false,\"inputs\":[{\"name\":\"status\",\"type\":\"bool\",\"indexed\":false}],\"name\":\"LogInterchainStatus\",\"outputs\":null,\"type\":\"event\",\"payable\":false,\"stateMutability\":null}]";

    protected Broker(String contractAddress, CITAj citaj, TransactionManager transactionManager) {
        super(BINARY, contractAddress, citaj, transactionManager);
    }

    public List<ThrowEventEventResponse> getThrowEventEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("throwEvent", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ThrowEventEventResponse> responses = new ArrayList<ThrowEventEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ThrowEventEventResponse typedResponse = new ThrowEventEventResponse();
            typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.fid = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.tid = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.funcs = (String) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.args = (String) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.argscb = (String) eventValues.getNonIndexedValues().get(6).getValue();
            typedResponse.argsrb = (String) eventValues.getNonIndexedValues().get(7).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ThrowEventEventResponse> throwEventEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("throwEvent", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, ThrowEventEventResponse>() {
            @Override
            public ThrowEventEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ThrowEventEventResponse typedResponse = new ThrowEventEventResponse();
                typedResponse.index = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.fid = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.tid = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.funcs = (String) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.args = (String) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.argscb = (String) eventValues.getNonIndexedValues().get(6).getValue();
                typedResponse.argsrb = (String) eventValues.getNonIndexedValues().get(7).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogInterchainDataEventResponse> getLogInterchainDataEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogInterchainData", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogInterchainDataEventResponse> responses = new ArrayList<LogInterchainDataEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogInterchainDataEventResponse typedResponse = new LogInterchainDataEventResponse();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.data = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogInterchainDataEventResponse> logInterchainDataEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogInterchainData", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, LogInterchainDataEventResponse>() {
            @Override
            public LogInterchainDataEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogInterchainDataEventResponse typedResponse = new LogInterchainDataEventResponse();
                typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.data = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<LogInterchainStatusEventResponse> getLogInterchainStatusEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("LogInterchainStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<LogInterchainStatusEventResponse> responses = new ArrayList<LogInterchainStatusEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            LogInterchainStatusEventResponse typedResponse = new LogInterchainStatusEventResponse();
            typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogInterchainStatusEventResponse> logInterchainStatusEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("LogInterchainStatus", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        AppFilter filter = new AppFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return citaj.appLogFlowable(filter).map(new Function<Log, LogInterchainStatusEventResponse>() {
            @Override
            public LogInterchainStatusEventResponse apply(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogInterchainStatusEventResponse typedResponse = new LogInterchainStatusEventResponse();
                typedResponse.status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> invokeInterchain(String srcChainID, BigInteger index, String destAddr, Boolean req, byte[] bizCallData, BigInteger weiValue, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "invokeInterchain", 
                Arrays.<Type>asList(new Address(srcChainID),
                new Uint64(index),
                new Address(destAddr),
                new Bool(req),
                new com.citahub.cita.abi.datatypes.DynamicBytes(bizCallData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function,  quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getCallbackMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getCallbackMeta", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Address>) results.get(0).getValue()), 
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> register(String addr, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "register", 
                Arrays.<Type>asList(new Address(addr)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<TransactionReceipt> emitInterchainEvent(String destChainID, String destAddr, String funcs, String args, String argscb, String argsrb, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "emitInterchainEvent", 
                Arrays.<Type>asList(new Address(destChainID),
                new Utf8String(destAddr),
                new Utf8String(funcs),
                new Utf8String(args),
                new Utf8String(argscb),
                new Utf8String(argsrb)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getInnerMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getInnerMeta", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Address>) results.get(0).getValue()), 
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteCall<TransactionReceipt> invokeIndexUpdateWithError(String srcChainID, BigInteger index, Boolean req, String err, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "invokeIndexUpdateWithError", 
                Arrays.<Type>asList(new Address(srcChainID),
                new Uint64(index),
                new Bool(req),
                new Utf8String(err)),
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

    public RemoteCall<BigInteger> getInMessage(String from, BigInteger idx) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getInMessage", 
                Arrays.<Type>asList(new Address(from),
                new Uint64(idx)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getOutMessage(String to, BigInteger idx) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getOutMessage", 
                Arrays.<Type>asList(new Address(to),
                new Uint64(idx)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> audit(String addr, BigInteger status, Long quota, String nonce, Long validUntilBlock, Integer version, BigInteger chainId, String value) {
        com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function(
                "audit", 
                Arrays.<Type>asList(new Address(addr),
                new com.citahub.cita.abi.datatypes.generated.Int64(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, quota, nonce, validUntilBlock, version, chainId, value);
    }

    public RemoteCall<Tuple2<List<String>, List<BigInteger>>> getOuterMeta() {
        final com.citahub.cita.abi.datatypes.Function function = new com.citahub.cita.abi.datatypes.Function("getOuterMeta", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Uint64>>() {}));
        return new RemoteCall<Tuple2<List<String>, List<BigInteger>>>(
                new Callable<Tuple2<List<String>, List<BigInteger>>>() {
                    @Override
                    public Tuple2<List<String>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<List<String>, List<BigInteger>>(
                                convertToNative((List<Address>) results.get(0).getValue()), 
                                convertToNative((List<Uint64>) results.get(1).getValue()));
                    }
                });
    }

    public static RemoteCall<Broker> deploy(CITAj citaj, TransactionManager transactionManager, Long quota, String nonce, Long validUntilBlock, Integer version, String value, BigInteger chainId) {
        return deployRemoteCall(Broker.class, citaj, transactionManager, quota, nonce, validUntilBlock, version, chainId, value, BINARY, "");
    }

    public static Broker load(String contractAddress, CITAj citaj, TransactionManager transactionManager) {
        return new Broker(contractAddress, citaj, transactionManager);
    }

    public static class ThrowEventEventResponse {
        public BigInteger index;

        public String to;

        public String fid;

        public String tid;

        public String funcs;

        public String args;

        public String argscb;

        public String argsrb;
    }

    public static class LogInterchainDataEventResponse {
        public Boolean status;

        public String data;
    }

    public static class LogInterchainStatusEventResponse {
        public Boolean status;
    }
}
