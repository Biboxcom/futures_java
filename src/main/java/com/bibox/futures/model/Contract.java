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
public class Contract {

    // 合约名称
    private String symbol;

    // 合约面值
    private BigDecimal unit;

    // 基础风险限额
    private BigDecimal riskLimitBase;

    // 风险限额步长
    private BigDecimal riskLimitStep;

    // 最大委托数量
    private int activeOrderLimit;

    // 最大张数
    private BigDecimal positionSizeLimit;

    // 最大杠杠倍数
    private BigDecimal leverageLimit;

    // 默认 maker 手续费率
    private BigDecimal makerFee;

    // 默认 taker 手续费率
    private BigDecimal takerFee;

    // 最小委托价格
    private BigDecimal priceIncrement;

    // 资金汇率
    private BigDecimal fundingRate;

}
