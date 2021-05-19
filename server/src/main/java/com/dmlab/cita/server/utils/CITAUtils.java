package com.dmlab.cita.server.utils;

import com.citahub.cita.protobuf.ConvertStrByte;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.response.AppMetaData;
import com.citahub.cita.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class CITAUtils {
    public static byte[] convertHexToBytes(String hex) {
        String clearedStr = Numeric.cleanHexPrefix(hex);
        return ConvertStrByte.hexStringToBytes(clearedStr);
    }

    public static int getVersion(CITAj service) throws IOException {
        AppMetaData appMetaData = null;
        appMetaData = service.appMetaData(DefaultBlockParameterName.PENDING).send();
        return appMetaData.getAppMetaDataResult().getVersion();
    }

    public static BigInteger getChainId(CITAj service) throws IOException {
        AppMetaData appMetaData = null;
        appMetaData = service.appMetaData(DefaultBlockParameterName.PENDING).send();
        return appMetaData.getAppMetaDataResult().getChainId();
    }

    public static String getNonce() {
        Random random = new Random(System.currentTimeMillis());
        return String.valueOf(Math.abs(random.nextLong()));
    }

    public static BigInteger getCurrentHeight(CITAj service) throws IOException {
        return getCurrentHeight(service, 3);
    }

    private static BigInteger getCurrentHeight(CITAj service, int retry) throws IOException {
        long height = -1;
        height = service.appBlockNumber().send().getBlockNumber().longValue();
        return BigInteger.valueOf(height);
    }

    public static BigInteger getValidUtilBlock(CITAj service, int validUntilBlock) throws IOException {
        return getCurrentHeight(service).add(
                BigInteger.valueOf(validUntilBlock));
    }

    public static BigInteger getValidUtilBlock(CITAj service) throws IOException {
        return getCurrentHeight(service).add(BigInteger.valueOf(88));
    }
}

