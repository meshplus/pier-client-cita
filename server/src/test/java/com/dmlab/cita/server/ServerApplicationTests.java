package com.dmlab.cita.server;

import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.CITAjService;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.http.HttpService;
import com.dmlab.cita.server.utils.FunctionUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;

class ServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        CITAj client = CITAj.build(new HttpService("http://172.16.13.123:1337"));
        AppBlock.Block block = null;
        TransactionReceipt receipt = null;
        for (int i = 753900; i < 800000; i++) {
            block = client.appGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), true).send().getBlock();
            if (!block.getBody().getTransactions().isEmpty()) {
                AppBlock.TransactionObject tx = block.getBody().getTransactions().get(0);
                receipt = client.appGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt();
                if (!receipt.getLogs().isEmpty()) {
                    break;
                }

            }
        }

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(block.getHeader()));

        System.out.println(mapper.writeValueAsString(receipt));
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
