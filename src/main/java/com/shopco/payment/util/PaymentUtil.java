package com.shopco.payment.util;

import java.util.UUID;

public class PaymentUtil {
    public static String generatePaymentReference(){
        return  "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0,16);
    }
}
