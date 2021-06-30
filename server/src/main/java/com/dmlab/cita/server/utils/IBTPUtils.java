package com.dmlab.cita.server.utils;

import com.citahub.cita.utils.HexUtil;
import com.dmlab.cita.server.contracts.Broker;
import com.google.protobuf.ByteString;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Keys;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import pb.IBTP;
import pb.content;
import pb.payload;

import java.util.ArrayList;
import java.util.List;

public class IBTPUtils {
    public static IBTP.Category category(IBTP ibtp) {
        switch (ibtp.getType()) {
            case INTERCHAIN:
            case ASSET_EXCHANGE_INIT:
            case ASSET_EXCHANGE_REDEEM:
            case ASSET_EXCHANGE_REFUND:
                return IBTP.Category.REQUEST;
            case RECEIPT_SUCCESS:
            case RECEIPT_FAILURE:
            case ASSET_EXCHANGE_RECEIPT:
                return IBTP.Category.RESPONSE;
            default:
                return IBTP.Category.UNKNOWN;
        }
    }

    public static IBTP convertFromEvent(Broker.ThrowEventEventResponse response, String from) throws Exception {
        String[] funcs = response.funcs.split(",");
        if (funcs.length != 3) {
            throw new Exception("expect 3 functions, current " + response.funcs);
        }

        if (isValidAddress(response.fid)) {
            response.fid = Numeric.prependHexPrefix(response.fid);
        }

        if (isValidAddress(response.tid)) {
            response.tid = Numeric.prependHexPrefix(response.tid);
        }

        content cont = content.newBuilder()
                .setSrcContractId(response.fid)
                .setDstContractId(response.tid)
                .setFunc(funcs[0])
                .setCallback(funcs[1])
                .setRollback(funcs[2])
                .addAllArgs(handleArgs(response.args))
                .addAllArgsCb(handleArgs(response.argscb))
                .addAllArgsRb(handleArgs(response.argsrb))
                .build();


        payload pl = payload.newBuilder()
                .setContent(cont.toByteString())
                .setEncrypted(false)
                .build();

        return IBTP.newBuilder()
                .setFrom(from)
                .setTo(Keys.toChecksumAddress(response.to))
                .setIndex(response.index.longValue())
                .setType(IBTP.Type.INTERCHAIN)
                .setTimestamp(System.nanoTime())
                .setPayload(pl.toByteString())
                .setProof(ByteString.copyFromUtf8("1"))
                .build();
    }


    private static List<ByteString> handleArgs(String argStr) {
        List<ByteString> byteStrings = new ArrayList<>();
        String[] args = argStr.split(",");

        for (String arg : args) {
            ByteString bytes = ByteString.copyFromUtf8(arg);
            byteStrings.add(bytes);
        }

        return byteStrings;
    }

    public static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == 40;
    }
}
