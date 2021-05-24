package com.dmlab.cita.server;

import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.datatypes.Function;
import com.dmlab.cita.server.utils.FunctionUtils;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;

class ServerApplicationTests {

    @Test
    void contextLoads() throws Exception {
        ArrayList<ByteString> args = new ArrayList<>();
        args.add(ByteString.copyFrom("Alice", Charset.defaultCharset()));
        args.add(ByteString.copyFrom("Alice", Charset.defaultCharset()));
        args.add(ByteString.copyFrom(BigInteger.valueOf(10).toByteArray()));
        Function function = FunctionUtils.packFunc("interchainCharge", args);
        String encode = FunctionEncoder.encode(function);
        System.out.println(encode);
    }

}
