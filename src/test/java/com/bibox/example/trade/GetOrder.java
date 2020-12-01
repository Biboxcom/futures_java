package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.OrderIdSet;
import com.bibox.futures.OrderQuery;
import com.bibox.futures.Pager;
import com.bibox.futures.model.Order;
import com.bibox.futures.model.enums.TradeAction;

import java.util.List;

public class GetOrder {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        getOneOrder(client);
        getOrders(client);
        getOrdersByPage(client);

    }

    private static void getOrdersByPage(BiboxFuturesClient client) throws Throwable {
        OrderQuery query = new OrderQuery();
        query.setPage(1);
        query.setSize(2);
        query.setAction(TradeAction.ENTRY);
        // you can set page ,size,orderStatus ...
        Pager<Order> ordersByPage = client.getOrders(query);
        System.out.println(ordersByPage);
        System.out.println(ordersByPage.getItems().size());
    }

    private static void getOneOrder(BiboxFuturesClient client) throws Throwable {
        System.out.println(client.getOrder("1112223333"));
    }

    private static void getOrders(BiboxFuturesClient client) throws Throwable {
        OrderIdSet idSet = new OrderIdSet();
        idSet.add("35184377957292");
        idSet.add("28587322968618");
        List<Order> orders = client.getOrders(idSet);
        orders.forEach(System.out::println);
    }

}
