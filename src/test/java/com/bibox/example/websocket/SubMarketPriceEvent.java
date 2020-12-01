package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubMarketPriceEvent {

    public static void main(String[] args) {
        BiboxFuturesClient client = new BiboxFuturesClient();
        String symbol = "BTC_USD";
        client.subscribeMarketPrice(symbol, x -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unSubscribeMarketPriceEvent(symbol);
    }

}
