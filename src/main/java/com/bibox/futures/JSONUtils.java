/*
 * Copyright (C) 2020, Bibox.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.bibox.futures;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bibox.futures.model.*;
import com.bibox.futures.model.enums.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class JSONUtils {

    public static long parsePing(JSONObject json) {
        if (json.containsKey("ping")) {
            return json.getLong("ping");
        }
        return -1;
    }

    public static JSONObject parseError(String text) throws Throwable {
        if (!text.startsWith("{")) {
            throw new RuntimeException();
        }

        JSONObject json = JSON.parseObject(text);
        if (json.containsKey("state")) {
            int state = json.getInteger("state");
            String message = json.getString("msg");
            if (state != 0) {
                throw new RuntimeException(
                        String.format("Error (state:%s, message:%s)", state, message));
            }
        }
        return json;
    }

    public static boolean parseError(JSONObject json) {
        return "error".equals(json.getString("status"));
    }

    public static JSONObject parseObject(String text) throws Throwable {
        return parseError(text);
    }

    public static JSONObject parseObject(String text, String child) throws Throwable {
        return parseError(text).getJSONObject(child);
    }

    public static JSONArray parseArray(String text) throws Throwable {
        if (!text.startsWith("[")) {
            parseError(text);
            throw new RuntimeException();
        }
        return JSON.parseArray(text);
    }

    public static JSONArray parseArray(String text, String child) throws Throwable {
        text = parseObject(text).getString(child);
        if (!text.startsWith("[")) {
            parseError(text);
            throw new RuntimeException();
        }
        return JSON.parseArray(text);
    }

    public static List<Contract> parseContracts(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        List<Contract> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseContract(arr.getJSONObject(i)));
        }
        return list;
    }

    private static Contract parseContract(JSONObject obj) {
        Contract contract = new Contract();
        contract.setSymbol(obj.getString("pair"));
        contract.setUnit(obj.getBigDecimal("value"));
        contract.setRiskLimitBase(obj.getBigDecimal("risk_level_base"));
        contract.setRiskLimitStep(obj.getBigDecimal("risk_level_dx"));
        contract.setActiveOrderLimit(obj.getInteger("pending_max"));
        contract.setPositionSizeLimit(obj.getBigDecimal("hold_max")
                .divide(obj.getBigDecimal("value"), RoundingMode.HALF_DOWN));
        contract.setLeverageLimit(obj.getBigDecimal("leverage_max"));
        contract.setMakerFee(obj.getBigDecimal("maker_fee"));
        contract.setTakerFee(obj.getBigDecimal("taker_fee"));
        return contract;
    }

    public static List<Candlestick> parseCandlesticks(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        List<Candlestick> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseCandlestick(arr.getJSONObject(i)));
        }
        return list;
    }

    public static List<Candlestick> parseCandlesticks(JSONArray arr) {
        List<Candlestick> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseCandlestick(arr.getJSONObject(i)));
        }
        return list;
    }

    private static Candlestick parseCandlestick(JSONObject obj) {
        Candlestick candlestick = new Candlestick();
        candlestick.setTime(obj.getLong("time"));
        candlestick.setOpen(obj.getBigDecimal("open"));
        candlestick.setClose(obj.getBigDecimal("close"));
        candlestick.setHigh(obj.getBigDecimal("high"));
        candlestick.setLow(obj.getBigDecimal("low"));
        candlestick.setVolume(obj.getBigDecimal("vol"));
        return candlestick;
    }

    private static MarkPrice parseMarketPrice(JSONObject obj) {
        MarkPrice markPrice = new MarkPrice();
        markPrice.setTime(obj.getLong("time"));
        markPrice.setOpen(obj.getBigDecimal("open"));
        markPrice.setClose(obj.getBigDecimal("close"));
        markPrice.setHigh(obj.getBigDecimal("high"));
        markPrice.setLow(obj.getBigDecimal("low"));
        return markPrice;
    }

    public static OrderBook parseOrderBook(String text) throws Throwable {
        JSONObject obj = parseObject(text, "result");

        OrderBook orderBook = new OrderBook();
        orderBook.setSymbol(obj.getString("pair"));
        orderBook.setUpdateTime(obj.getLong("update_time"));
        JSONArray bidArr = obj.getJSONArray("bids");
        JSONArray askArr = obj.getJSONArray("asks");
        // set bids
        for (int i = 0; i < bidArr.size(); i++) {
            JSONObject item = bidArr.getJSONObject(i);
            orderBook.getBidBook().add(item.getBigDecimal("price"), item.getBigDecimal("volume"));
        }
        // set asks
        for (int i = 0; i < askArr.size(); i++) {
            JSONObject item = askArr.getJSONObject(i);
            orderBook.getAskBook().add(item.getBigDecimal("price"), item.getBigDecimal("volume"));
        }
        return orderBook;
    }

    private static PriceLevel parseOrderBookEntry(JSONObject obj) {
        PriceLevel priceLevel = new PriceLevel();
        priceLevel.setPrice(obj.getBigDecimal("price"));
        priceLevel.setAmount(obj.getBigDecimal("volume"));
        return priceLevel;
    }

    public static List<ContractInfo> parseContractInfo(String text) throws Throwable {
        JSONObject obj = parseObject(text, "result");
        ArrayList<ContractInfo> list = new ArrayList<>();
        obj.keySet().forEach(symbol -> {
            ContractInfo contractInfo = new ContractInfo();
            contractInfo.setSymbol(symbol);
            contractInfo.setIndexPrice(obj.getJSONObject(symbol).getBigDecimal("close"));
            contractInfo.setMarkPrice(obj.getJSONObject(symbol).getBigDecimal("priceTag"));
            contractInfo.setTime(obj.getJSONObject(symbol).getTimestamp("createdAt").getTime());
            list.add(contractInfo);
        });
        return list;
    }

    public static HashMap<String, BigDecimal> parseFundingRates(String text) throws Throwable {
        HashMap<String, BigDecimal> m = new HashMap<>();
        JSONObject obj = parseObject(text, "result");
        obj.keySet().forEach(symbol -> {
            m.put(symbol, obj.getJSONObject(symbol).getBigDecimal("fund_rate"));
        });
        return m;
    }

    public static HashMap<String, BigDecimal> parseSymbolPrecisions(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        HashMap<String, BigDecimal> m = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject item = arr.getJSONObject(i);
            m.put(item.getString("pair"), BigDecimal.valueOf(0.1).
                    pow(item.getInteger("price_unit")));
        }
        return m;
    }

    public static Long parseTimestamp(String text) throws Throwable {
        return parseObject(text).getLong("time");
    }

    public static String parseOrderId(String text) throws Throwable {
        return parseObject(text).getString("order_id");
    }

    public static List<Account> parseAccounts(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        ArrayList<Account> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseAccount(arr.getJSONObject(i)));
        }
        return list;
    }

    public static Account parseAccount(JSONObject obj) {
        Account account = new Account();
        account.setAsset(obj.getString("c"));
        account.setAvailable(obj.getBigDecimal("b"));
        account.setOrderMargin(obj.getBigDecimal("f"));
        account.setPositionMargin(obj.getBigDecimal("m"));
        return account;
    }

    public static List<Position> parsePositions(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        ArrayList<Position> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parsePosition(arr.getJSONObject(i)));
        }
        return list;
    }

    public static HashMap<String, BigDecimal> parseUnits(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        HashMap<String, BigDecimal> m = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject item = arr.getJSONObject(i);
            m.put(item.getString("pair"), item.getBigDecimal("value"));
        }
        return m;
    }

    public static Position parsePosition(JSONObject obj) {
        Position position = new Position();
        position.setSymbol(obj.getString("pi"));
        position.setPositionMargin(obj.getBigDecimal("mg"));
        position.setMarginCallPrice(obj.getBigDecimal("pa"));
        position.setLiquidationPrice(obj.getBigDecimal("pf"));
        position.setMarginMode(MarginMode.fromInteger(obj.getInteger("md")));
        position.setSide(TradeSide.fromInteger(obj.getInteger("sd")));
        position.setLeverage(obj.getInteger("l"));
        position.setEntryPrice(obj.getBigDecimal("po"));
        position.setCurrentQty(obj.getBigDecimal("hc"));
        position.setReducibleQty(obj.getBigDecimal("lc"));
        position.setUserId("ui");
        return position;
    }

    public static Pager<PositionUpdate> parsePositionEntriesByPage(String text) throws Throwable {
        Pager<PositionUpdate> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("count"));
        pager.setPage(json.getInteger("page"));

        JSONArray arr = parseArray(json.getString("items"));
        ArrayList<PositionUpdate> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parsePositionUpdate(arr.getJSONObject(i)));
        }
        pager.setItems(list);
        return pager;
    }

    private static PositionUpdate parsePositionUpdate(JSONObject obj) {
        PositionUpdate positionUpdate = new PositionUpdate();
        positionUpdate.setId(obj.getString("id"));
        positionUpdate.setSymbol(obj.getString("pair"));
        positionUpdate.setSide(TradeSide.fromInteger(obj.getInteger("side")));
        positionUpdate.setMarginMode(MarginMode.fromInteger(obj.getInteger("model")));
        positionUpdate.setType(PositionUpdateType.fromInteger(obj.getInteger("log_type")));
        positionUpdate.setChange(obj.getBigDecimal("hold_coin_dx"));
        positionUpdate.setCurrentQty(obj.getBigDecimal("hold_coin"));
        positionUpdate.setPrice(obj.getBigDecimal("price_log"));
        positionUpdate.setEntryPrice(obj.getBigDecimal("price_open"));
        positionUpdate.setProfit(obj.getBigDecimal("profit"));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        positionUpdate.setFee(fee);
        positionUpdate.setTime(obj.getTimestamp("createdAt").getTime());
        positionUpdate.setUserId(obj.getString("user_id"));
        return positionUpdate;
    }

    public static Pager<Fill> parseTradesByPage(String text, String orderId) throws Throwable {
        Pager<Fill> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("count"));
        pager.setPage(json.getInteger("page"));

        JSONArray arr = parseArray(json.getString("items"));
        ArrayList<Fill> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseFill(arr.getJSONObject(i), orderId));
        }
        pager.setItems(list);
        return pager;
    }

    private static Fill parseFill(JSONObject obj, String orderId) {
        Fill fill = new Fill();
        fill.setId(obj.getString("id"));
        fill.setOrderId(orderId);
        fill.setSymbol(obj.getString("pair"));
        Integer side = obj.getInteger("side");
        fill.setAction(TradeAction.fromSide(side));
        fill.setSide(TradeSide.fromSide(side));
        fill.setPrice(obj.getBigDecimal("deal_price"));
        fill.setOrderPrice(obj.getBigDecimal("price"));
        fill.setQuantity(obj.getBigDecimal("deal_coin"));
        fill.setIsMaker(obj.getBoolean("is_maker"));
        fill.setTime(obj.getTimestamp("createdAt").getTime());
        Fee fee = new Fee();

        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        fill.setFee(fee);

        return fill;
    }

    public static Pager<Order> parseOrdersByPage(String text) throws Throwable {
        Pager<Order> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("count"));
        pager.setPage(json.getInteger("page"));

        JSONArray arr = parseArray(json.getString("items"));
        ArrayList<Order> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseOrder(arr.getJSONObject(i)));
        }
        pager.setItems(list);
        return pager;
    }

    public static List<Order> parseOrders(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        ArrayList<Order> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseOrder(arr.getJSONObject(i)));
        }
        return list;
    }

    private static Order parseOrder(JSONObject obj) throws Throwable {
        LimitOrder order =new LimitOrder();
        Integer side = obj.getInteger("side");
        order.setAction(TradeAction.fromSide(side));
        order.setSide(TradeSide.fromSide(side));
        order.setOrderId(obj.getString("id"));
        order.setSymbol(obj.getString("pair"));
        order.setPrice(obj.getBigDecimal("price"));
        order.setQuantity(obj.getBigDecimal("amount_coin"));
        order.setOrderMargin(obj.getBigDecimal("freeze"));
        order.setAvgPrice(obj.getBigDecimal("price_deal"));
        order.setExecutedQty(obj.getBigDecimal("deal_coin"));
        order.setTradeCount(obj.getInteger("deal_num"));
        order.setFailReason(obj.getInteger("reason"));

        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        order.setFee(fee);
        order.setStatus(OrderStatus.fromInteger(obj.getInteger("status")));
        order.setMakerFee(obj.getBigDecimal("fee_rate_maker"));
        order.setTakerFee(obj.getBigDecimal("fee_rate_taker"));
        order.setClientOrderId(obj.getString("client_oid"));
        order.setCreateTime(obj.getTimestamp("createdAt").getTime());
        order.setUpdateTime(obj.getTimestamp("updatedAt").getTime());
        order.setUserId(obj.getString("user_id"));

        return order;
    }

    private static Trade parseTrade(JSONObject obj) {
        Trade trade = new Trade();
        trade.setSymbol(obj.getString("pair"));
        Integer side = obj.getInteger("side");
        trade.setSide(TradeSide.fromSide(side));
        trade.setPrice(obj.getBigDecimal("price"));
        trade.setQuantity(obj.getBigDecimal("amount"));
        trade.setTime(obj.getLong("time"));
        return trade;
    }

    public static Ticker parseTicker(JSONObject obj) {
        Ticker ticker = new Ticker();
        ticker.setSymbol(obj.getString("pair"));
        ticker.setChange(obj.getString("percent"));
        ticker.setTime(obj.getLong("timestamp"));
        ticker.setVolume(obj.getBigDecimal("vol"));
        ticker.setPrice(obj.getBigDecimal("last"));
        ticker.setPriceInCNY(obj.getBigDecimal("base_last_cny"));
        ticker.setPriceInUSD(obj.getBigDecimal("last_usd"));
        ticker.setHigh(obj.getBigDecimal("high"));
        ticker.setLow(obj.getBigDecimal("low"));
        ticker.setBestAskPrice(obj.getBigDecimal("sell"));
        ticker.setBestAskQty(obj.getBigDecimal("sell_amount"));
        ticker.setBestBidPrice(obj.getBigDecimal("buy"));
        ticker.setBestBidQty(obj.getBigDecimal("buy_amount"));
        return ticker;
    }

    public static List<Trade> parseTradeEvent(JSONArray arr) {
        List<Trade> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseTrade(arr.getJSONObject(i)));
        }
        return list;
    }

    public static List<MarkPrice> parseMarketPrice(JSONArray arr) {
        List<MarkPrice> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(parseMarketPrice(arr.getJSONObject(i)));
        }
        return list;
    }

    public static Order parseOrderEvent(JSONObject obj) {
        LimitOrder order = new LimitOrder();
        order.setOrderId(obj.getString("oi"));
        order.setClientOrderId(obj.getString("coi"));
        order.setSymbol(obj.getString("pi"));
        order.setOrderMargin(obj.getBigDecimal("fz"));
        order.setCreateTime(obj.getLong("t"));
        order.setUserId(obj.getString("ui"));
        order.setStatus(OrderStatus.fromInteger(obj.getInteger("s")));
        order.setFailReason(obj.getInteger("r"));
        order.setQuantity(obj.getBigDecimal("q"));
        order.setPrice(obj.getBigDecimal("p"));
        order.setExecutedQty(obj.getBigDecimal("eq"));
        order.setAvgPrice(obj.getBigDecimal("dp"));
        Integer side = obj.getInteger("sd");
        order.setAction(TradeAction.fromSide(side));
        order.setSide(TradeSide.fromSide(side));

        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("f"));
        fee.setInBIX(obj.getBigDecimal("fb"));
        fee.setInCoupon(obj.getBigDecimal("fb0"));
        order.setFee(fee);
        return order;
    }

    public static Fill parseFill(JSONObject obj) {
        Fill fill = new Fill();
        fill.setId(obj.getString("id"));
        fill.setOrderId(obj.getString("oi"));
        fill.setSymbol(obj.getString("pi"));
        Integer side = obj.getInteger("sd");
        fill.setAction(TradeAction.fromSide(side));
        fill.setSide(TradeSide.fromSide(side));
        fill.setPrice(obj.getBigDecimal("dp"));
        fill.setOrderPrice(obj.getBigDecimal("p"));
        fill.setQuantity(obj.getBigDecimal("ep"));
        fill.setIsMaker(obj.getBoolean("im"));
        fill.setTime(obj.getLong("t"));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("f"));
        fee.setInBIX(obj.getBigDecimal("fb"));
        fee.setInCoupon(obj.getBigDecimal("fb0"));
        fill.setFee(fee);
        return fill;
    }

    public static PositionUpdate parsePositionUpdateEvent(JSONObject obj) {
        PositionUpdate positionUpdate = new PositionUpdate();
        positionUpdate.setId(obj.getString("id"));
        positionUpdate.setUserId(obj.getString("user_id"));
        positionUpdate.setType(PositionUpdateType.fromInteger(obj.getInteger("type")));
        positionUpdate.setMarginMode(MarginMode.fromInteger(obj.getInteger("mode")));
        positionUpdate.setSymbol(obj.getString("pair"));
        positionUpdate.setPrice(obj.getBigDecimal("price"));
        positionUpdate.setChange(obj.getBigDecimal("hold_dx"));
        positionUpdate.setSide(TradeSide.fromInteger(obj.getInteger("order_side")));
        positionUpdate.setTime(obj.getLong("time"));
        return positionUpdate;
    }

}
