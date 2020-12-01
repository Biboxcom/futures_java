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

import com.bibox.util.SortedDoubleObjectMap;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Iterator;

@Data
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

}
