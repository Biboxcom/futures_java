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

package com.bibox.futures.model.enums;

public enum PositionUpdateType {

    // 开仓
    ENTRY(1),

    // 平仓
    EXIT(2),

    // 减仓
    REDUCE(3),

    // 爆仓
    LIQUIDATE(4),

    // ADL
    ADL(5);

    private final int value;

    PositionUpdateType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PositionUpdateType fromInteger(int x) {
        switch (x) {
            case 1:
                return ENTRY;
            case 2:
                return EXIT;
            case 3:
                return REDUCE;
            case 4:
                return LIQUIDATE;
            case 5:
                return ADL;
            default:
                return null;
        }
    }

}
