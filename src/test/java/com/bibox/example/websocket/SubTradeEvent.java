package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubTradeEvent {

    public static void main(String[] args) {
        BiboxFuturesClient client = new BiboxFuturesClient();
        String symbol = "BTC_USD";
        client.subscribeTrade(symbol, (x) -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unSubscribeTradeEvent(symbol);
    }

}
