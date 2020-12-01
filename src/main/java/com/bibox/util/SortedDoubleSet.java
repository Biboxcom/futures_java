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

import java.util.Arrays;
import java.util.NoSuchElementException;

public class SortedDoubleSet {

    private static final int INITIAL_CAPACITY = 8;

    private final boolean mDescending;
    private double[] mElements;
    private int mSize;

    public SortedDoubleSet() {
        this(false);
    }

    public SortedDoubleSet(boolean descending) {
        mDescending = descending;
        mElements = new double[INITIAL_CAPACITY];
        mSize = 0;
    }

    public SortedDoubleSet(SortedDoubleSet o) {
        mDescending = o.mDescending;
        mElements = Arrays.copyOf(o.mElements, o.mElements.length);
        mSize = o.mSize;
    }

    public final boolean isEmpty() {
        return mSize == 0;
    }

    public final int size() {
        return mSize;
    }

    public final void clear() {
        mSize = 0;
    }

    public final double get(int index) {
        return mElements[index];
    }

    public final int add(double e) {
        int index = indexOf(e);
        if (index >= 0) {
            return -(index + 1);
        }
        index = -(index + 1);

        double[] newElements;
        if (mSize < mElements.length) {
            newElements = mElements;
        } else {
            newElements = new double[mElements.length << 1];
            if (index > 0) {
                System.arraycopy(mElements, 0, newElements, 0, index);
            }
        }

        // 拷贝后半部分
        if (index < mSize) {
            System.arraycopy(mElements, index, newElements, index + 1, mSize - index);
        }

        newElements[index] = e;
        mElements = newElements;
        mSize++;

        return index;
    }

    public final int remove(double e) {
        int index = indexOf(e);
        if (index < 0) {
            return -1;
        }

        removeAt(index);
        return index;
    }

    public final double removeAt(int index) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        double value = mElements[index];

        double[] newElements;
        if (mSize > (mElements.length >> 2) || mElements.length <= INITIAL_CAPACITY) {
            newElements = mElements;
        } else {
            newElements = new double[mElements.length >> 1];
            if (index > 0) {
                System.arraycopy(mElements, 0, newElements, 0, index);
            }
        }

        mSize--;
        if (index < mSize) {
            System.arraycopy(mElements, index + 1, newElements, index, mSize - index);
        }
        mElements = newElements;

        return value;
    }

    public final int indexOf(double e) {
        if (mSize < 1) {
            return -1;
        }

        int low = 0, high = mSize, mid;
        double midElem;

        while (low < high) {
            mid = (low + high) >> 1;
            midElem = mElements[mid];

            if (e == midElem) {
                return mid;
            }

            if (mDescending ? e > midElem : e < midElem) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }

        return -(low + 1);
    }
}
