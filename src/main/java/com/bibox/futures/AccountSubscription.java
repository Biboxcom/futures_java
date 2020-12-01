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
import com.bibox.futures.model.Account;
import com.bibox.util.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AccountSubscription extends PrivateSubscription<List<Account>> {

    private final Map<String, Account> items = new HashMap<>();

    AccountSubscription(BiboxFuturesClient client, Listener<List<Account>> listener) {
        super(client, listener);
    }

    @Override
    String getDataName() {
        return "cbc_assets";
    }

    @Override
    public List<Account> doDecode(JSONObject json) {
        List<Account> a = new ArrayList<>();
        a.add(JSONUtils.parseAccount(json));
        return a;
    }

    @Override
    void onData(List<Account> data) {
        data.forEach(item -> items.put(item.getAsset(), item));
    }

    @Override
    List<Account> getData() {
        List<Account> a = new ArrayList<>(items.values());
        items.clear();
        return a;
    }

}
