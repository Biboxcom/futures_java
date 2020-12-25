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
import com.bibox.futures.model.enums.OrderStatus;
import com.bibox.futures.model.enums.OrderType;
import com.bibox.futures.model.enums.TradeAction;
import com.bibox.futures.model.enums.TradeSide;
import lombok.*;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
public class LimitOrder extends Order {

    // 委托价格
    private BigDecimal price;

    @Override
    public OrderType getOrderType() {
        return OrderType.LIMIT;
    }

    public static LimitOrder parseResult(JSONObject obj) {
        LimitOrder a = new LimitOrder();
        Integer side = obj.getInteger("side");
        a.setAction(TradeAction.fromSide(side));
        a.setSide(TradeSide.fromSide(side));
        a.setOrderId(obj.getString("id"));
        a.setSymbol(obj.getString("pair"));
        a.setPrice(obj.getBigDecimal("price"));
        a.setQuantity(obj.getBigDecimal("amount_coin"));
        a.setOrderMargin(obj.getBigDecimal("freeze"));
        a.setAvgPrice(obj.getBigDecimal("price_deal"));
        a.setExecutedQty(obj.getBigDecimal("deal_coin"));
        a.setTradeCount(obj.getInteger("deal_num"));
        a.setFailReason(obj.getInteger("reason"));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        a.setFee(fee);
        a.setStatus(OrderStatus.fromInteger(obj.getInteger("status")));
        a.setMakerFee(obj.getBigDecimal("fee_rate_maker"));
        a.setTakerFee(obj.getBigDecimal("fee_rate_taker"));
        a.setClientOrderId(obj.getString("client_oid"));
        a.setCreateTime(obj.getTimestamp("createdAt").getTime());
        a.setUpdateTime(obj.getTimestamp("updatedAt").getTime());
        a.setUserId(obj.getString("user_id"));
        return a;
    }

    public static LimitOrder parseResult2(JSONObject obj) {
        return parseEvent(obj);
    }

    public static LimitOrder parseEvent(JSONObject obj){
        LimitOrder a = new LimitOrder();
        a.setOrderId(obj.getString("oi"));
        a.setClientOrderId(obj.getString("coi"));
        a.setSymbol(obj.getString("pi"));
        a.setOrderMargin(obj.getBigDecimal("fz"));
        a.setCreateTime(obj.getLong("t"));
        a.setUserId(obj.getString("ui"));
        a.setStatus(OrderStatus.fromInteger(obj.getInteger("s")));
        a.setFailReason(obj.getInteger("r"));
        a.setQuantity(obj.getBigDecimal("q"));
        a.setPrice(obj.getBigDecimal("p"));
        a.setExecutedQty(obj.getBigDecimal("eq"));
        a.setAvgPrice(obj.getBigDecimal("dp"));
        Integer side = obj.getInteger("sd");
        a.setAction(TradeAction.fromSide(side));
        a.setSide(TradeSide.fromSide(side));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("f"));
        fee.setInBIX(obj.getBigDecimal("fb"));
        fee.setInCoupon(obj.getBigDecimal("fb0"));
        a.setFee(fee);
        return a;
    }

}
