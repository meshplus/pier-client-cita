package com.dmlab.cita.server;

import com.citahub.cita.abi.datatypes.Address;
import com.citahub.cita.abi.datatypes.DynamicArray;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.abi.datatypes.generated.Bytes4;
import com.citahub.cita.protobuf.ConvertStrByte;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.request.Transaction;
import com.citahub.cita.protocol.core.methods.response.AppMetaData;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.protocol.system.CITASystemContract;
import com.citahub.cita.utils.Numeric;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class ServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        CITAj service = CITAj.build(new HttpService("http://cita-rpc-ql-rivbase-0.cs.cmburl.cn"));
        AppMetaData appMetaData;
        appMetaData = service.appMetaData(DefaultBlockParameter.valueOf("latest")).send();
        String chainIdHex = appMetaData.getAppMetaDataResult().getChainIdV1();
        BigInteger chainId = new BigInteger(chainIdHex.substring(2), 16);
        int version = appMetaData.getAppMetaDataResult().getVersion();
        long currentHeight = service.appBlockNumber().send().getBlockNumber().longValue();
        System.out.println(currentHeight);    long validUntilBlock = currentHeight + 80;
        Random random = new Random(System.currentTimeMillis());
        String nonce = String.valueOf(Math.abs(random.nextLong()));
        long quota = 1000000;
        // function hash from A.signature
        List<String> funcs = new ArrayList<>();
        // 将合约signature文件中func的hash添加到funcs中
        /*
         修改：根据跨链合约生成的signature修改此值
         */
        funcs.add("3b1f68ff");
        List<String> contractAddrs = new ArrayList<>();
        int index = funcs.size();
        while (index > 0)
        {
            contractAddrs.add("0xa9234240c15b8ee015478f652740c70c979c9f9b");
            index --;
        }

        //添加合约可调用权限，permissionAddr 不用修改
        String permissionAddr = "0xc5752095480a8cbb73d3358d5697098e2d8f45cd";
        List<Address> addrsToAdd = contractAddrs.stream().map(item -> new Address(item)).collect(Collectors.toList());
        List<Bytes4> funcHashList = funcs.stream().map(item ->
                new Bytes4(ConvertStrByte.hexStringToBytes(Numeric.cleanHexPrefix(item)))).collect(Collectors.toList());
        List<Type> inputParameters = Arrays.asList(new Address(permissionAddr),
                new DynamicArray<Address>(addrsToAdd),
                new DynamicArray<Bytes4>(funcHashList));
        String callData = CITASystemContract.encodeFunction("addResources", inputParameters);
        Transaction tx = Transaction.createFunctionCallTransaction("0xffFffFffFFffFFFFFfFfFFfFFFFfffFFff020004",nonce,quota,validUntilBlock,version,chainId,"0",callData);
        String rawTx = tx.sign("0x16366b77a9a7131353c45535b0f2e065b4c2240f02a6a987bcc887541614b9d1",Transaction.CryptoTx.SM2,false);

        //instantiate a CITAj and send the transaction
        AppSendTransaction sendTransaction = service.appSendRawTransaction(rawTx).send();
        String txHash = sendTransaction.getSendTransactionResult().getHash();
        System.out.println("tx hash is : " + txHash);
    }

    @Test
    void address() throws JsonProcessingException {
        TT t = new TT();
        t.tt = 1;
        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String value = mapper.writeValueAsString(t);
        System.out.println(value);
    }


    //    @Data
    public static class TT {
        public int tt;
    }

}
