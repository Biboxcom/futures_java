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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Future<V> {

    private final Lock mLock = new ReentrantLock();
    private final Condition mCondition = mLock.newCondition();
    private boolean mDone = false;
    private V mResult = null;

    public void set(V result) {
        if (!mDone) {
            mLock.lock();
            if (!mDone) {
                mDone = true;
                mResult = result;
                mCondition.signalAll();
            }
            mLock.unlock();
        }
    }

    public V get() {
        sync();
        return mResult;
    }

    public void sync() {
        if (!mDone) {
            mLock.lock();
            if (!mDone) {
                try {
                    mCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    mLock.unlock();
                    throw new IllegalStateException();
                }
            }
            mLock.unlock();
        }
    }

}
