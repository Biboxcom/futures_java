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
import com.bibox.util.Listener;

abstract class Subscription<T> {

    protected final BiboxFuturesClient client;
    private final Listener<T> listener;
    private boolean updated;
    private boolean notified;

    Subscription(BiboxFuturesClient client, Listener<T> listener) {
        this.client = client;
        this.listener = listener;
    }

    abstract String getChannel();

    abstract T decode(JSONObject json);

    abstract void onData(T data);

    abstract T getData();

    void start() {
        client.sendSubscribeMessage(this);
    }

    void stop() {
        client.sendUnsubscribeMessage(this);
    }

    void onMessage(JSONObject json) {
        T data = decode(json);
        onData(data);
        updated = true;
        notifyUpdate();
    }

    private void notifyUpdate() {
        if (notified) {
            return;
        }

        // 获取数据的副本给子线程使用
        T data = getData();
        updated = false;

        // 发送数据副本给子线程
        // 如果子线程被其他耗时操作占用, 那么这个数据副本会在队列中等待
        client.getSubscriptionHandler().post(() -> {

            // 子线程已经完成了前面的操作, 并从队列中取出了数据副本
            listener.on(data);

            // 通知 client 数据副本已被处理, 可以处理下一个更新了
            client.getMessageHandler().post(() -> {
                notified = false;

                // 如果在处理过程中, client 收到了新数据, 则立刻通知子线程
                if (updated) {
                    notifyUpdate();
                }
            });
        });

        notified = true;
    }

}
