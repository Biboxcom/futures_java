package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;

public class SubTickerEvent {

    public static void main(String[] args) {
        BiboxFuturesClient client = new BiboxFuturesClient();
        // 处理业务逻辑
        String symbol = "BTC_USD";
        client.subscribeTicker(symbol, x -> {
            System.out.println(x);
            // ...
        });
        // client.unSubscribeTickerEvent(symbol);
    }

}
