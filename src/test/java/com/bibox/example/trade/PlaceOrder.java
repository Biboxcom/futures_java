package com.bibox.example.trade;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.LimitOrder;
import com.bibox.futures.model.MarketOrder;
import com.bibox.futures.model.enums.TradeAction;
import com.bibox.futures.model.enums.TradeSide;

import java.math.BigDecimal;

public class PlaceOrder {

    public static void main(String[] args) throws Throwable {
        String apiKey = "adc88f86bfa5598ae76b2e512c123b9cac5f4ac8";
        String secretKey = "12afe3046e782c85066d5da2b533036a981efbff";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        placeLimitOrder(client);
        placeMarketOrder(client);
    }

    private static void placeMarketOrder(BiboxFuturesClient client) throws Throwable {
        // market order
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setSymbol("5ETH_USD");
        marketOrder.setQuantity(BigDecimal.valueOf(1));
        marketOrder.setAction(TradeAction.ENTRY);
        marketOrder.setSide(TradeSide.LONG);
        String orderId = client.placeOrder(marketOrder);
        System.out.println("the market order_id: " + orderId);
    }

    private static void placeLimitOrder(BiboxFuturesClient client) throws Throwable {
        // limit order
        LimitOrder limitOrder = new LimitOrder();
        limitOrder.setSymbol("5ETH_USD");
        limitOrder.setQuantity(BigDecimal.valueOf(1));
        limitOrder.setAction(TradeAction.ENTRY);
        limitOrder.setSide(TradeSide.SHORT);
        limitOrder.setPrice(BigDecimal.valueOf(470));
        String orderId = client.placeOrder(limitOrder);
        System.out.println("the limit order_id: " + orderId);
    }

}
