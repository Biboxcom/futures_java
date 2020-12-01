package com.bibox.example.market;

import com.bibox.futures.BiboxFuturesClient;

public class GetContractInfo {
    public static void main(String[] args) throws Throwable {
        BiboxFuturesClient client = new BiboxFuturesClient();
        System.out.println(client.getContractInfos());
        // System.out.println(client.getContractInfo("BTC_USD"));
    }
}
