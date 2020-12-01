package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.enums.TradeSide;

import java.math.BigDecimal;

public class TransferMargin {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        client.transferMargin("5ETH_USD", TradeSide.LONG, BigDecimal.valueOf(0.0001));
        System.out.println("TransferMargin succ");
    }

}
