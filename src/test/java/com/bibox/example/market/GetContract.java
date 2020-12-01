package com.bibox.example.market;

import com.bibox.futures.BiboxFuturesClient;

public class GetContract {
    public static void main(String[] args) throws Throwable {
        BiboxFuturesClient client = new BiboxFuturesClient();
        System.out.println(client.getContract("BTC_USD"));
        System.out.println(client.getContracts());
    }
}
