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

import com.alibaba.fastjson.JSONObject;
import com.bibox.futures.model.enums.TradeAction;
import com.bibox.futures.model.enums.TradeSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Fill extends Trade {

    // 成交 id
    private String id;

    // 委托 id
    private String orderId;

    // 委托价格
    private BigDecimal orderPrice;

    // 开仓或平仓
    private TradeAction action;

    // 是否是 maker
    private Boolean isMaker;

    // 手续费
    private Fee fee;

    public static Fill parseResult(JSONObject obj) {
        Fill a = new Fill();
        a.setId(obj.getString("id"));
        a.setOrderId(obj.getString("orderId"));
        a.setSymbol(obj.getString("pair"));
        Integer side = obj.getInteger("side");
        a.setAction(TradeAction.fromSide(side));
        a.setSide(TradeSide.fromSide(side));
        a.setPrice(obj.getBigDecimal("deal_price"));
        a.setOrderPrice(obj.getBigDecimal("price"));
        a.setQuantity(obj.getBigDecimal("deal_coin"));
        a.setIsMaker(obj.getBoolean("is_maker"));
        a.setTime(obj.getTimestamp("createdAt").getTime());
        Fee fee = new Fee();

        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        a.setFee(fee);
        return a;
    }

    public static Fill parseEvent(JSONObject obj) {
        Fill a = new Fill();
        a.setId(obj.getString("id"));
        a.setOrderId(obj.getString("oi"));
        a.setSymbol(obj.getString("pi"));
        Integer side = obj.getInteger("sd");
        a.setAction(TradeAction.fromSide(side));
        a.setSide(TradeSide.fromSide(side));
        a.setPrice(obj.getBigDecimal("dp"));
        a.setOrderPrice(obj.getBigDecimal("p"));
        a.setQuantity(obj.getBigDecimal("ep"));
        a.setIsMaker(obj.getBoolean("im"));
        a.setTime(obj.getLong("t"));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("f"));
        fee.setInBIX(obj.getBigDecimal("fb"));
        fee.setInCoupon(obj.getBigDecimal("fb0"));
        a.setFee(fee);
        return a;
    }

}
