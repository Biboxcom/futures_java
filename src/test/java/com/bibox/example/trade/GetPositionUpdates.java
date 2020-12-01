package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.Pager;
import com.bibox.futures.model.PositionUpdate;

public class GetPositionUpdates {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        Pager<PositionUpdate> positionEntries = client.getPositionUpdates("ETH_USD", 1, 10);
        System.out.println(positionEntries);
    }

}
