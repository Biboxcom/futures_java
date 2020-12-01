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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SortedDoubleObjectMap<V> {

    private final SortedDoubleSet mKeys;
    private final List<V> mValues;

    public SortedDoubleObjectMap() {
        this(false);
    }

    public SortedDoubleObjectMap(boolean descending) {
        mKeys = new SortedDoubleSet(descending);
        mValues = new ArrayList<>();
    }

    public SortedDoubleObjectMap(SortedDoubleObjectMap<V> o) {
        mKeys = new SortedDoubleSet(o.mKeys);
        mValues = new ArrayList<>(o.mValues);
    }

    public final boolean isEmpty() {
        return mKeys.isEmpty();
    }

    public final int size() {
        return mKeys.size();
    }

    public final void clear() {
        mKeys.clear();
        mValues.clear();
    }

    public final boolean containsKey(double key) {
        return mKeys.indexOf(key) >= 0;
    }

    public final V get(double key) {
        int index = mKeys.indexOf(key);
        if (index >= 0) {
            return mValues.get(index);
        } else {
            return null;
        }
    }

    public final double keyAt(int index) {
        return mKeys.get(index);
    }

    public final V valueAt(int index) {
        return mValues.get(index);
    }

    public final SortedDoubleSet keys() {
        return mKeys;
    }

    public final List<V> values() {
        return mValues;
    }

    public final V put(double key, V value) {
        int index = mKeys.add(key);
        if (index >= 0) {
            mValues.add(index, value);
            return null;
        } else {
            return mValues.set(-(index + 1), value);
        }
    }

    public final V putIfAbsent(double key, V value) {
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }
        return v;
    }

    public final V remove(double key) {
        int index = mKeys.indexOf(key);
        if (index >= 0) {
            return removeAt(index);
        } else {
            return null;
        }
    }

    public final V removeAt(int index) {
        mKeys.removeAt(index);
        return mValues.remove(index);
    }

    public final Iterator<V> iterator() {
        return new Iterator<V>() {
            private int mIndex = -1;

            @Override
            public boolean hasNext() {
                return ++mIndex < size();
            }

            @Override
            public V next() {
                return valueAt(mIndex);
            }
        };
    }

    public final Iterator<V> reverseIterator() {
        return new Iterator<V>() {
            private int mIndex = size();

            @Override
            public boolean hasNext() {
                return --mIndex >= 0;
            }

            @Override
            public V next() {
                return valueAt(mIndex);
            }
        };
    }
}
