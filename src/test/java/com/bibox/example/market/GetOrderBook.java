package com.bibox.example.market;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.OrderBook;

public class GetOrderBook {
    public static void main(String[] args) throws Throwable {
        BiboxFuturesClient client = new BiboxFuturesClient();
        OrderBook orderBook = client.getOrderBook("BTC_USD");

        // ask1->askN
        orderBook.getAskBook().iterator().forEachRemaining(priceLevel ->
                System.out.println("the ask: " + priceLevel)
        );
        // bid1->bidN
        orderBook.getBidBook().iterator().forEachRemaining(priceLevel ->
                System.out.println("the bid: " + priceLevel)
        );

        // ...
    }
}
