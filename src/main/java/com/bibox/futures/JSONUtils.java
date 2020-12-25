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

import java.math.BigDecimal;
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
            list.add(Contract.parseResult(arr.getJSONObject(i)));
        }
        return list;
    }

    public static List<Candlestick> parseCandlesticks(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        return parseCandlesticks(arr);
    }

    public static List<Candlestick> parseCandlesticks(JSONArray arr) {
        List<Candlestick> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(Candlestick.parseResult(arr.getJSONObject(i)));
        }
        return list;
    }

    public static OrderBook parseOrderBook(String text) throws Throwable {
        JSONObject obj = parseObject(text, "result");
        return OrderBook.parseResult(obj);
    }

    public static List<ContractInfo> parseContractInfo(String text) throws Throwable {
        JSONObject obj = parseObject(text, "result");
        ArrayList<ContractInfo> list = new ArrayList<>();
        obj.keySet().forEach(symbol -> {
            JSONObject item = obj.getJSONObject(symbol);
            item.put("symbol", symbol);
            list.add(ContractInfo.parseResult(item));
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
        return Account.parseResult(obj);
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
        return Position.parseResult(obj);
    }

    public static Pager<PositionUpdate> parsePositionEntriesByPage(String text) throws Throwable {
        Pager<PositionUpdate> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("count"));
        pager.setPage(json.getInteger("page"));

        JSONArray arr = parseArray(json.getString("items"));
        ArrayList<PositionUpdate> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(PositionUpdate.parseResult(arr.getJSONObject(i)));
        }
        pager.setItems(list);
        return pager;
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
        obj.put("orderId", orderId);
        return Fill.parseResult(obj);
    }

    public static Pager<Order> parseOrdersByPage(String text) throws Throwable {
        Pager<Order> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("count"));
        pager.setPage(json.getInteger("page"));

        JSONArray arr = parseArray(json.getString("items"));
        ArrayList<Order> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(LimitOrder.parseResult(arr.getJSONObject(i)));
        }
        pager.setItems(list);
        return pager;
    }

    public static Pager<Order> parseOpenOrders(String text) throws Throwable {
        Pager<Order> pager = new Pager<>();

        JSONObject json = parseObject(text, "result");
        pager.setCount(json.getInteger("t"));
        pager.setPage(json.getInteger("p"));

        JSONArray arr = parseArray(json.getString("o"));
        ArrayList<Order> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(LimitOrder.parseResult2(arr.getJSONObject(i)));
        }
        pager.setItems(list);
        return pager;
    }

    public static List<Order> parseOrders(String text) throws Throwable {
        JSONArray arr = parseArray(text, "result");
        ArrayList<Order> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(LimitOrder.parseResult(arr.getJSONObject(i)));
        }
        return list;
    }

    public static Ticker parseTickerEvent(JSONObject obj) {
        return Ticker.parseEvent(obj);
    }

    public static List<Trade> parseTradeEvent(JSONArray arr) {
        List<Trade> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(Trade.parseEvent(arr.getJSONObject(i)));
        }
        return list;
    }

    public static List<MarkPrice> parseMarketPrice(JSONArray arr) {
        List<MarkPrice> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            list.add(MarkPrice.parseResult(arr.getJSONObject(i)));
        }
        return list;
    }

    public static Order parseOrderEvent(JSONObject obj) {
        return LimitOrder.parseEvent(obj);
    }

    public static Fill parseFillEvent(JSONObject obj) {
        return Fill.parseEvent(obj);
    }

    public static PositionUpdate parsePositionUpdateEvent(JSONObject obj) {
        return PositionUpdate.parseEvent(obj);
    }

}
