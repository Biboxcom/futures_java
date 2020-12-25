package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.OrderIdSet;
import com.bibox.futures.OrderQuery;
import com.bibox.futures.Pager;
import com.bibox.futures.model.Order;
import com.bibox.futures.model.enums.TradeAction;
import com.bibox.futures.model.enums.TradeSide;

import java.util.List;

public class GetOrder {

    public static void main(String[] args) throws Throwable {
        String apiKey = "5d965851a2d885c2c837a33f8e2fea39cfb17399";
        String secretKey = "2c9dfe3cbe55fb139c68df741165b50eef1cc3a3";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        getOneOrder(client);
        getOrders(client);
        getOpenOrders(client);
        getCloseOrders(client);

    }

    private static void getOpenOrders(BiboxFuturesClient client) throws Throwable {
        OrderQuery query = new OrderQuery();
        query.setSymbol("5BTC_USD");
        query.setPage(1);
        query.setSize(10);
        query.setAction(TradeAction.ENTRY);
        query.setSide(TradeSide.LONG);
        Pager<Order> orders = client.getOpenOrders(query);
        System.out.println(orders);
    }

    private static void getCloseOrders(BiboxFuturesClient client) throws Throwable {
        OrderQuery query = new OrderQuery();
        // you can set page,size,orderStatus ...
        query.setPage(1);
        query.setSize(10);
        // query.setAction(TradeAction.ENTRY);
        // query.setSide(TradeSide.LONG);
        // query.setStatus(OrderStatus.CANCELED);
        Pager<Order> orders = client.getOrders(query);
        System.out.println(orders);
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
