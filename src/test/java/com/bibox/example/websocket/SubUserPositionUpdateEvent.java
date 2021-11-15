package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubUserPositionUpdateEvent {

    public static void main(String[] args) {
        String apiKey = "your apiKey";
        String secretKey = "your secretKey";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        client.subscribePositionUpdate(x -> {
            x.forEach(System.out::println);
            // ...
        });
    }

}
