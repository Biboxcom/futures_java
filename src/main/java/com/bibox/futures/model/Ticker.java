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

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Ticker {

    // 合约名称
    private String symbol;

    // 成交时间
    private Long time;

    // 成交数量
    private BigDecimal volume;

    // 最新成交价
    private BigDecimal price;

    // cny 计价
    private BigDecimal priceInCNY;

    // usd 计价
    private BigDecimal priceInUSD;

    // 24小时最高价
    private BigDecimal high;

    // 24小时最低价
    private BigDecimal low;

    // 最新买一价
    private BigDecimal bestBidPrice;

    // 最新买一量
    private BigDecimal bestBidQty;

    // 最新卖一价
    private BigDecimal bestAskPrice;

    // 最新卖一量
    private BigDecimal bestAskQty;

    // 24小时涨幅
    private String change;

}
