package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubOrderBookEvent {

    public static void main(String[] args) {
        BiboxFuturesClient client = new BiboxFuturesClient();
        String symbol = "BTC_USD";
        client.subscribeOrderBook(symbol, x -> {
            System.out.println(x.getAskBook().getPriceLevel(0));
            System.out.println(x.getBidBook().getPriceLevel(0));
            // ...
        });
        // client.unSubscribeOrderBookEvent(symbol);
    }

}
