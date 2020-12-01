package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.ClientOrderIdSet;
import com.bibox.futures.OrderIdSet;

public class CancelOrder {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        cancelWithSymbol(client);
        // cancelWithOrderId(client);
        // cancelWithOrderIds(client);
    }

    private static void cancelWithOrderId(BiboxFuturesClient client) throws Throwable {
        client.cancelOrder("123134123124433");
        System.out.println("cancel succ");
    }

    private static void cancelWithOrderIds(BiboxFuturesClient client) throws Throwable {
        OrderIdSet orderIdSet = new OrderIdSet();
        orderIdSet.add("28587322968618");
        orderIdSet.add("35184377957292");
        client.cancelAllOrders(orderIdSet);
        System.out.println("cancel succ");
    }

    private static void cancelWithSymbol(BiboxFuturesClient client) throws Throwable {
        client.cancelAllOrders("5ETH_USD");
        System.out.println("cancel succ");
    }

}
