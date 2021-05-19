package com.dmlab.cita.server.utils;

import pb.IBTP;

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
}
