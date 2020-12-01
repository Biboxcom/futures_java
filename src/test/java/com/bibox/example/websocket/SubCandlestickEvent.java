package com.bibox.example.websocket;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.enums.TimeInterval;

public class SubCandlestickEvent {

    public static void main(String[] args) {
        BiboxFuturesClient client = new BiboxFuturesClient();
        String symbol = "BTC_USD";
        client.subscribeCandlestick(symbol, TimeInterval.ONE_MINUTE, (x) -> {
            x.forEach(System.out::println);
            // ...
        });
        // client.unSubscribeCandlestickEvent(symbol,CandlestickInterval.ONE_MINUTE);
    }

}
