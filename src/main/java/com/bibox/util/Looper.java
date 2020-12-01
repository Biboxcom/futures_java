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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Looper {

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    private final MessageQueue queue;

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        Looper looper = myLooper();
        if (looper != null) {
            return looper.queue;
        } else {
            return null;
        }
    }

    /**
     * Constructor
     */
    private Looper(MessageQueue queue) {
        this.queue = (queue != null ? queue : new MessageQueue());
    }

    /**
     * 初始化本线程为 Looper, 并由之后调用的 loop() 方法启动
     */
    public static void prepare() {
        prepare(null);
    }

    public static void prepare(MessageQueue queue) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(queue));
    }

    /**
     * 在本线程启动消息循环
     */
    public static void loop() {
        final Looper looper = myLooper();
        if (looper == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }

        try {
            for (; ; ) {
                AbstractMessage msg = looper.queue.dequeue();
                if (msg == null) {
                    // No message indicates that the message queue is quitting.
                    return;
                }

                msg.target.dispatchMessage(msg);
            }
        } catch (Throwable e) {
            log.error("Stop looper on thread '{}' due to", Thread.currentThread().getName(),
                    e.toString());
            throw e;
        }
    }

}
