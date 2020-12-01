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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bibox.futures.model.Trade;
import com.bibox.util.Listener;
import com.bibox.util.ZipUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class TradeSubscription extends Subscription<List<Trade>> {

    private final List<Trade> items = new ArrayList<>();
    private final String symbol;

    TradeSubscription(BiboxFuturesClient client, String symbol, Listener<List<Trade>> listener) {
        super(client, listener);
        this.symbol = symbol;
    }

    static String buildChannelName(String symbol) {
        return String.format("bibox_sub_spot_%s_deals", SymbolConverter.convert(symbol));
    }

    @Override
    String getChannel() {
        return buildChannelName(symbol);
    }

    @Override
    public List<Trade> decode(JSONObject json) {
        String data = ZipUtils.unzip(json.getBytes("data"));
        return JSONUtils.parseTradeEvent(Objects.requireNonNull(JSON.parseArray(data)));
    }

    @Override
    void onData(List<Trade> data) {
        items.addAll(data);
    }

    @Override
    List<Trade> getData() {
        List<Trade> a = new ArrayList<>(items);
        items.clear();
        return a;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("event", "addChannel");
        json.put("channel", getChannel());
        json.put("binary", 0);
        return json.toJSONString();
    }

}
