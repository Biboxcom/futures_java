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
import com.bibox.futures.model.enums.MarginMode;
import com.bibox.futures.model.enums.PositionUpdateType;
import com.bibox.futures.model.enums.TradeSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class PositionUpdate {

    // 仓位变化id
    private String id;

    // 合约名称
    private String symbol;

    // 多空
    private TradeSide side;

    // 仓位模式
    private MarginMode marginMode;

    // 变化类型
    private PositionUpdateType type;

    // 持仓数量
    private BigDecimal currentQty;

    // 持仓变化量
    private BigDecimal change;

    // 仓位变化时的成交价格
    private BigDecimal price;

    // 开仓均价
    private BigDecimal entryPrice;

    // 收益
    private BigDecimal profit;

    // 变化时间
    private Long time;

    // 用户id
    private String userId;

    // 手续费
    private Fee fee;

    public static PositionUpdate parseResult(JSONObject obj) {
        PositionUpdate a = new PositionUpdate();
        a.setId(obj.getString("id"));
        a.setSymbol(obj.getString("pair"));
        a.setSide(TradeSide.fromInteger(obj.getInteger("side")));
        a.setMarginMode(MarginMode.fromInteger(obj.getInteger("model")));
        a.setType(PositionUpdateType.fromInteger(obj.getInteger("log_type")));
        a.setChange(obj.getBigDecimal("hold_coin_dx"));
        a.setCurrentQty(obj.getBigDecimal("hold_coin"));
        a.setPrice(obj.getBigDecimal("price_log"));
        a.setEntryPrice(obj.getBigDecimal("price_open"));
        a.setProfit(obj.getBigDecimal("profit"));
        Fee fee = new Fee();
        fee.setValue(obj.getBigDecimal("fee"));
        fee.setInBIX(obj.getBigDecimal("fee_bix"));
        fee.setInCoupon(obj.getBigDecimal("fee_bix0"));
        a.setFee(fee);
        a.setTime(obj.getTimestamp("createdAt").getTime());
        a.setUserId(obj.getString("user_id"));
        return a;
    }

    public static PositionUpdate parseEvent(JSONObject obj) {
        PositionUpdate a = new PositionUpdate();
        a.setId(obj.getString("id"));
        a.setUserId(obj.getString("user_id"));
        a.setType(PositionUpdateType.fromInteger(obj.getInteger("type")));
        a.setMarginMode(MarginMode.fromInteger(obj.getInteger("mode")));
        a.setSymbol(obj.getString("pair"));
        a.setPrice(obj.getBigDecimal("price"));
        a.setChange(obj.getBigDecimal("hold_dx"));
        a.setSide(TradeSide.fromInteger(obj.getInteger("order_side")));
        a.setTime(obj.getLong("time"));
        return a;
    }

}
