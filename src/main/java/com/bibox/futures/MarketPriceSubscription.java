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
import com.bibox.futures.model.MarkPrice;
import com.bibox.util.Listener;

import java.util.ArrayList;
import java.util.List;

class MarketPriceSubscription extends Subscription<List<MarkPrice>> {

    private final List<MarkPrice> items = new ArrayList<>();
    private final String symbol;

    MarketPriceSubscription(BiboxFuturesClient client,
                            String symbol, Listener<List<MarkPrice>> listener) {
        super(client, listener);
        this.symbol = symbol;
    }

    static String buildChannelName(String symbol) {
        return String.format("bibox_sub_spot_%sTAGPRICE_kline_1min",
                SymbolConverter.convert(symbol));
    }

    @Override
    String getChannel() {
        return buildChannelName(symbol);
    }

    @Override
    public List<MarkPrice> decode(JSONObject json) {
        return JSONUtils.parseMarketPrice(json.getJSONArray("data"));
    }

    @Override
    void onData(List<MarkPrice> data) {
        items.addAll(data);
    }

    @Override
    List<MarkPrice> getData() {
        List<MarkPrice> a = new ArrayList<>(items);
        items.clear();
        return a;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("event", "addChannel");
        json.put("channel", getChannel());
        json.put("binary", 0);
        json.put("ver", 8);
        return json.toJSONString();
    }

}
