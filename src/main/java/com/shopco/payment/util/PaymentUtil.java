package com.shopco.payment.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PaymentUtil {
    public static String generatePaymentReference(){
        return  "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0,16);
    }

    public static String generate4DigitCode() {
        int code = ThreadLocalRandom.current().nextInt(1000, 10000); // 1000–9999
        return String.valueOf(code);
    }
}
