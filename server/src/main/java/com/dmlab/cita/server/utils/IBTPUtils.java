package com.dmlab.cita.server.utils;

import com.dmlab.cita.server.contracts.Broker;
import com.google.protobuf.ByteString;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;
import pb.Ibtp.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class IBTPUtils {
    public static IBTP.Category category(IBTP ibtp) {
        switch (ibtp.getType()) {
            case INTERCHAIN:
                return IBTP.Category.REQUEST;
            case RECEIPT_SUCCESS:
            case RECEIPT_FAILURE:
            case RECEIPT_ROLLBACK:
                return IBTP.Category.RESPONSE;
            default:
                return IBTP.Category.UNKNOWN;
        }
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
