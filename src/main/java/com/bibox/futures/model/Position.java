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
import com.bibox.futures.model.enums.TradeSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString
public class Position {

    // 合约名称
    private String symbol;

    // 仓位模式
    private MarginMode marginMode;

    // 多空方向
    private TradeSide side;

    // 杠杆
    private int leverage;

    // 开仓价格
    private BigDecimal entryPrice;

    // 仓位保证金
    private BigDecimal positionMargin;

    // 爆仓价格
    private BigDecimal liquidationPrice;

    // 告警价格
    private BigDecimal marginCallPrice;

    // 持仓数量
    private BigDecimal currentQty;

    // 可平数量
    private BigDecimal reducibleQty;

    // 用户 id
    private String userId;

    public static void wrapper(Position position, BigDecimal unit) {
        position.currentQty = position.getCurrentQty().divide(unit, BigDecimal.ROUND_DOWN);
    }

    public static Position parseResult(JSONObject obj) {
        Position a = new Position();
        a.setSymbol(obj.getString("pi"));
        a.setPositionMargin(obj.getBigDecimal("mg"));
        a.setMarginCallPrice(obj.getBigDecimal("pa"));
        a.setLiquidationPrice(obj.getBigDecimal("pf"));
        a.setMarginMode(MarginMode.fromInteger(obj.getInteger("md")));
        a.setSide(TradeSide.fromInteger(obj.getInteger("sd")));
        a.setLeverage(obj.getInteger("l"));
        a.setEntryPrice(obj.getBigDecimal("po"));
        a.setCurrentQty(obj.getBigDecimal("hc"));
        a.setReducibleQty(obj.getBigDecimal("lc"));
        a.setUserId(obj.getString("ui"));
        return a;
    }

}
