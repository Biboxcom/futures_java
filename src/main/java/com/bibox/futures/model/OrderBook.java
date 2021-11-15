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

package com.bibox.futures.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bibox.util.SortedDoubleObjectMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Iterator;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString
public class OrderBook {

    // 合约名称
    private String symbol;

    // 更新时间
    private long updateTime;

    // 买方深度
    private BidBook bidBook = new BidBook();

    // 卖方深度
    private AskBook askBook = new AskBook();

    public OrderBook() {
    }

    public OrderBook(OrderBook other) {
        this.symbol = other.symbol;
        this.updateTime = other.updateTime;
        this.bidBook = new BidBook(other.bidBook);
        this.askBook = new AskBook(other.askBook);
    }

    public static abstract class SubBook {

        protected SortedDoubleObjectMap<PriceLevel> mLevels;

        protected SubBook() {
        }

        SubBook(SubBook other) {
            this.mLevels = new SortedDoubleObjectMap<>(other.mLevels);
        }

        public int getDepth() {
            return mLevels.size();
        }

        public PriceLevel getPriceLevel(int level) {
            return mLevels.valueAt(level);
        }

        public PriceLevel get(BigDecimal price) {
            return mLevels.get(price.doubleValue());
        }

        public void remove(BigDecimal price) {
            mLevels.remove(price.doubleValue());
        }

        public void add(BigDecimal price, BigDecimal amount) {
            mLevels.put(price.doubleValue(), new PriceLevel(price, amount));
        }

        public Iterator<PriceLevel> iterator() {
            return mLevels.iterator();
        }

        public Iterator<PriceLevel> reverseIterator() {
            return mLevels.reverseIterator();
        }

    }

    public static class BidBook extends SubBook {

        BidBook(BidBook other) {
            super(other);
        }

        BidBook() {
            mLevels = new SortedDoubleObjectMap<>(true);
        }

    }

    public static class AskBook extends SubBook {

        AskBook(AskBook other) {
            super(other);
        }

        AskBook() {
            mLevels = new SortedDoubleObjectMap<>();
        }

    }

    public static OrderBook parseResult(JSONObject obj) {
        OrderBook a = new OrderBook();
        a.setSymbol(obj.getString("pair"));
        a.setUpdateTime(obj.getLong("update_time"));
        JSONArray bidArr = obj.getJSONArray("bids");
        JSONArray askArr = obj.getJSONArray("asks");
        // set bids
        for (int i = 0; i < bidArr.size(); i++) {
            JSONObject item = bidArr.getJSONObject(i);
            a.getBidBook().add(item.getBigDecimal("price"), item.getBigDecimal("volume"));
        }
        // set asks
        for (int i = 0; i < askArr.size(); i++) {
            JSONObject item = askArr.getJSONObject(i);
            a.getAskBook().add(item.getBigDecimal("price"), item.getBigDecimal("volume"));
        }
        return a;
    }

    public static OrderBook parseEventResult(JSONObject obj) {
        OrderBook a = new OrderBook();
        a.setSymbol(obj.getString("pair"));
        a.setUpdateTime(obj.getLong("ut"));
        JSONArray bidArr = obj.getJSONArray("bids");
        JSONArray askArr = obj.getJSONArray("asks");

        // set bids
        for (int i = 0; i < bidArr.size(); i++) {
            JSONArray item = bidArr.getJSONArray(i);
            a.getBidBook().add(item.getBigDecimal(1), item.getBigDecimal(0));
        }
        // set asks
        for (int i = 0; i < askArr.size(); i++) {
            JSONArray item = askArr.getJSONArray(i);
            a.getAskBook().add(item.getBigDecimal(1), item.getBigDecimal(0));
        }
        return a;
    }


    public static void updateFromEvent(OrderBook orderBook, JSONObject obj) {
        orderBook.setUpdateTime(obj.getLong("ut"));
        JSONArray askArr = obj.getJSONObject("add").getJSONArray("asks");
        if (askArr != null) {
            for (int i = 0; i < askArr.size(); i++) {
                JSONArray item = askArr.getJSONArray(i);
                orderBook.getAskBook().
                        add(item.getBigDecimal(1), item.getBigDecimal(0));
            }
        }

        JSONArray bidArr = obj.getJSONObject("add").getJSONArray("bids");
        if (bidArr != null) {
            for (int i = 0; i < bidArr.size(); i++) {
                JSONArray item = bidArr.getJSONArray(i);
                orderBook.getBidBook().
                        add(item.getBigDecimal(1), item.getBigDecimal(0));
            }
        }

        askArr = obj.getJSONObject("del").getJSONArray("asks");
        if (askArr != null) {
            for (int i = 0; i < askArr.size(); i++) {
                JSONArray item = askArr.getJSONArray(i);
                orderBook.getAskBook().remove(item.getBigDecimal(1));
            }
        }

        bidArr = obj.getJSONObject("del").getJSONArray("bids");
        if (bidArr != null) {
            for (int i = 0; i < bidArr.size(); i++) {
                JSONArray item = bidArr.getJSONArray(i);
                orderBook.getBidBook().remove(item.getBigDecimal(1));
            }
        }

    }

}
