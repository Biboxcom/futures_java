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
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString
public class ContractInfo {

    // 合约名称
    private String symbol;

    // 指数价格
    private BigDecimal indexPrice;

    // 标记价格
    private BigDecimal markPrice;

    // 资金费率
    private BigDecimal fundingRate;

    // 时间
    private long time;

    public static void wrapper(ContractInfo contractInfo,BigDecimal fundingRate){
        contractInfo.fundingRate = fundingRate;
    }

    public static ContractInfo parseResult(JSONObject obj) {
        ContractInfo a = new ContractInfo();
        a.setSymbol(obj.getString("symbol"));
        a.setIndexPrice(obj.getBigDecimal("close"));
        a.setMarkPrice(obj.getBigDecimal("priceTag"));
        a.setTime(obj.getTimestamp("createdAt").getTime());
        return a;
    }

}
