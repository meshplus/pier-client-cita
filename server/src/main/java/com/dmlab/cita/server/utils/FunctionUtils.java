package com.dmlab.cita.server.utils;

import com.citahub.cita.abi.FunctionReturnDecoder;
import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.Bool;
import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.abi.datatypes.Utf8String;
import com.citahub.cita.abi.datatypes.generated.Uint64;
import com.google.protobuf.ByteString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FunctionUtils {
    public static Function packFunc(String func, List<ByteString> objs) throws Exception {
        switch (func) {
            case "interchainCharge": {
                if (objs.size() != 3) {
                    throw new IllegalArgumentException("args length not match");
                }

                return new Function(
                        "interchainCharge",
                        Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(objs.get(0).toStringUtf8()),
                                new com.citahub.cita.abi.datatypes.Utf8String(objs.get(1).toStringUtf8()),
                                new Uint64(new BigInteger(objs.get(2).toStringUtf8()))),
                        Collections.<TypeReference<?>>emptyList());
            }
            case "interchainRollback": {
                if (objs.size() != 2) {
                    throw new IllegalArgumentException("args length not match");
                }
                return new Function(
                        "interchainRollback",
                        Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(objs.get(0).toStringUtf8()),
                                new Uint64(new BigInteger(objs.get(1).toStringUtf8()))),
                        Collections.<TypeReference<?>>emptyList());
            }
            case "interchainSet": {
                if (objs.size() != 2) {
                    throw new IllegalArgumentException("args length not match");
                }
                return new Function(
                        "interchainSet",
                        Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(objs.get(0).toStringUtf8()),
                                new com.citahub.cita.abi.datatypes.Utf8String(objs.get(1).toStringUtf8())),
                        Collections.<TypeReference<?>>emptyList());
            }
            case "interchainGet": {
                if (objs.size() != 1) {
                    throw new IllegalArgumentException("args length not match");
                }
                return new Function(
                        "interchainGet",
                        Arrays.<Type>asList(new com.citahub.cita.abi.datatypes.Utf8String(objs.get(0).toStringUtf8())),
                        Collections.<TypeReference<?>>emptyList());
            }
            default:
                throw new Exception("Unsupported function");
        }
    }

    public static byte[][] unPackFunc(String func, String rawInput) throws Exception {
        switch (func) {
            case "interchainCharge": {
                List<TypeReference<?>> outputParameters = new ArrayList<>();
                outputParameters.add(new TypeReference<Bool>() {
                });
                List<TypeReference<Type>> outputParameterC = new ArrayList<>();
                for (TypeReference<?> outputParameter : outputParameters) {
                    outputParameterC.add((TypeReference<Type>) outputParameter);

                }

                List<Type> typeList = FunctionReturnDecoder.decode(rawInput, outputParameterC);
                Bool returnBool = (Bool) typeList.get(0);
                return new byte[][]{new byte[]{(byte) (returnBool.getValue() ? 1 : 0)}};

            }
            case "interchainGet": {
                List<TypeReference<?>> outputParameters = new ArrayList<>();
                outputParameters.add(new TypeReference<Bool>() {
                });
                outputParameters.add(new TypeReference<Utf8String>() {
                });
                List<TypeReference<Type>> outputParameterC = new ArrayList<>();
                for (TypeReference<?> outputParameter : outputParameters) {
                    outputParameterC.add((TypeReference<Type>) outputParameter);

                }

                List<Type> typeList = FunctionReturnDecoder.decode(rawInput, outputParameterC);
                Bool returnBool = (Bool) typeList.get(0);
                Utf8String returnString = (Utf8String) typeList.get(1);
                return new byte[][]{new byte[]{(byte) (returnBool.getValue() ? 1 : 0)}, returnString.getValue().getBytes()};
            }
            default:
                throw new Exception("Unsupported function");
        }
    }
}
