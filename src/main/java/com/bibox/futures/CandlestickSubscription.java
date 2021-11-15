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

import com.alibaba.fastjson.JSONObject;
import com.bibox.futures.model.Candlestick;
import com.bibox.futures.model.enums.TimeInterval;
import com.bibox.util.Listener;

import java.util.ArrayList;
import java.util.List;

class CandlestickSubscription extends Subscription<List<Candlestick>> {

    private final List<Candlestick> items = new ArrayList<>();
    private final String symbol;
    private final TimeInterval timeInterval;

    CandlestickSubscription(BiboxFuturesClient client,
                            String symbol, TimeInterval timeInterval,
                            Listener<List<Candlestick>> listener) {
        super(client, listener);
        this.symbol = symbol;
        this.timeInterval = timeInterval;
    }

    static String buildChannelName(String symbol, TimeInterval timeInterval) {
        return String.format("%s_kline_%s",
                SymbolConverter.convert(symbol),
                timeInterval.getValue());
    }

    @Override
    String getChannel() {
        return buildChannelName(symbol, timeInterval);
    }

    @Override
    public List<Candlestick> decode(JSONObject json) {
        return JSONUtils.parseCandlesticksNew(json.getJSONArray("d"));
    }

    @Override
    void onData(List<Candlestick> data) {
        items.addAll(data);
    }

    @Override
    List<Candlestick> getData() {
        List<Candlestick> a = new ArrayList<>(items);
        items.clear();
        return a;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("sub", getChannel());
        return json.toJSONString();
    }

}
