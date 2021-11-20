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
import com.bibox.futures.model.enums.TradeSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString
public class Trade {

    // 合约名称
    private String symbol;

    // 成交时间
    private long time;

    // 成交价格
    private BigDecimal price;

    // 成交数量
    private BigDecimal quantity;

    // 多空
    private TradeSide side;

    public static Trade parseEvent(JSONObject obj) {
        Trade a = new Trade();
        a.setSymbol(obj.getString("pair"));
        Integer side = obj.getInteger("side");
        a.setSide(TradeSide.fromSide(side));
        a.setPrice(obj.getBigDecimal("price"));
        a.setQuantity(obj.getBigDecimal("amount"));
        a.setTime(obj.getLong("time"));
        return a;
    }

    public static Trade parseEvent(JSONArray obj) {
        Trade a = new Trade();
        a.setSymbol(obj.getString(0));
        Integer side = obj.getInteger(3);
        a.setSide(TradeSide.fromSide(side));
        a.setPrice(obj.getBigDecimal(1));
        a.setQuantity(obj.getBigDecimal(2));
        a.setTime(obj.getLong(4));
        return a;
    }

    public static Trade parseEvent(JSONArray obj, String pair) {
        Trade a = new Trade();
        a.setSymbol(pair);
        Integer side = obj.getInteger(2);
        a.setSide(TradeSide.fromSide(side));
        a.setPrice(obj.getBigDecimal(0));
        a.setQuantity(obj.getBigDecimal(1));
        a.setTime(obj.getLong(3));
        return a;
    }
}
