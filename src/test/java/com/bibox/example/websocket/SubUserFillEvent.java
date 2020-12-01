package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubUserFillEvent {

    public static void main(String[] args) {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        client.subscribeFill(x -> {
            x.forEach(System.out::println);
            // ...
        });
    }

}
