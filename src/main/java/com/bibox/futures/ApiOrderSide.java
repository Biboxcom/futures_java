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

package com.bibox.futures;

import com.bibox.futures.model.Order;
import com.bibox.futures.model.enums.TradeAction;
import com.bibox.futures.model.enums.TradeSide;

enum ApiOrderSide {

    LONG_ENTRY(1),
    SHORT_ENTRY(2),
    LONG_EXIT(3),
    SHORT_EXIT(4);

    private final int value;

    ApiOrderSide(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    protected static ApiOrderSide lookupOrderSide(TradeSide side, TradeAction action) {
        if (side == TradeSide.LONG && action == TradeAction.ENTRY) {
            return LONG_ENTRY;
        } else if (side == TradeSide.SHORT && action == TradeAction.ENTRY) {
            return SHORT_ENTRY;
        } else if (side == TradeSide.LONG && action == TradeAction.EXIT) {
            return LONG_EXIT;
        } else if (side == TradeSide.SHORT && action == TradeAction.EXIT) {
            return SHORT_EXIT;
        } else {
            return null;
        }
    }

    protected static ApiOrderSide lookupOrderSide(Order order) {
        return lookupOrderSide(order.getSide(), order.getAction());
    }

}
