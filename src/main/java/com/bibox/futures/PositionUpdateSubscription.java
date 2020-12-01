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
import com.bibox.futures.model.PositionUpdate;
import com.bibox.util.Listener;

import java.util.ArrayList;
import java.util.List;

class PositionUpdateSubscription extends PrivateSubscription<List<PositionUpdate>> {

    private final List<PositionUpdate> items = new ArrayList<>();

    PositionUpdateSubscription(BiboxFuturesClient client, Listener<List<PositionUpdate>> listener) {
        super(client, listener);
    }

    @Override
    String getDataName() {
        return "cbc_deal_log";
    }

    @Override
    public List<PositionUpdate> doDecode(JSONObject json) {
        List<PositionUpdate> a = new ArrayList<>();
        a.add(JSONUtils.parsePositionUpdateEvent(json));
        return a;
    }

    @Override
    void onData(List<PositionUpdate> data) {
        items.addAll(data);
    }

    @Override
    List<PositionUpdate> getData() {
        List<PositionUpdate> a = new ArrayList<>(items);
        items.clear();
        return a;
    }

}
