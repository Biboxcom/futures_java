package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.enums.MarginMode;
import com.bibox.futures.model.enums.TradeSide;

public class ChangeMargin {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        changeMarginMode(client);
        // changeMargeLeverage(client);
    }

    private static void changeMarginMode(BiboxFuturesClient client) throws Throwable {
        client.changeMarginMode("5ETH_USD", MarginMode.CROSS);
        System.out.println("changeMarginMode succ");
    }

    private static void changeMargeLeverage(BiboxFuturesClient client) throws Throwable {
        client.changeLeverage("5ETH_USD", TradeSide.LONG, 49);
        System.out.println("changeMarginMode succ");
    }

}
