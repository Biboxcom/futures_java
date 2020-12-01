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

package com.bibox.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpQuery {

    private final Map<String, String> mItems = new HashMap<>();
    private final List<String> mKeys = new ArrayList<>();

    public boolean isEmpty() {
        return mKeys.isEmpty();
    }

    public List<String> keys() {
        return mKeys;
    }

    public String get(String key) {
        return mItems.get(key);
    }

    public HttpQuery put(String key, String value) {
        if (mItems.containsKey(key)) {
            mKeys.remove(key);
        }
        mItems.put(key, value);
        mKeys.add(key);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : mKeys) {
            String value = mItems.get(key);

            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key);
            if (!StringUtils.isBlank(value)) {
                try {
                    String valueString = URLEncoder.encode(value, "UTF-8");
                    sb.append("=");
                    sb.append(valueString);
                } catch (Throwable e) {
                    // DO NOTHING.
                }
            }
        }
        return sb.toString();
    }

}

