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

public class Handler {

    private final MessageQueue queue;

    public Handler() {
        this(Looper.myQueue());
    }

    public Handler(MessageQueue queue) {
        if (queue == null) {
            throw new IllegalArgumentException("The message queue must not be null");
        }
        this.queue = queue;
    }

    public final MessageQueue getQueue() {
        return queue;
    }

    public void handleMessage(Message msg) {
    }

    public void dispatchMessage(AbstractMessage msg) {
        if (msg instanceof Message) {
            handleMessage((Message) msg);
        } else if (msg instanceof CallbackMessage) {
            ((CallbackMessage) msg).callback.run();
        } else {
            throw new RuntimeException(
                    "Can't support message type " + msg.getClass().toString());
        }
    }

    public final void post(Runnable r) {
        postDelayed(r, 0);
    }

    public final void postDelayed(Runnable r, long delayMillis) {
        postAtTime(r, System.currentTimeMillis() + delayMillis);
    }

    public final void postAtTime(Runnable r, long timeMillis) {
        enqueueMessage(new CallbackMessage(r), timeMillis);
    }

    public final void sendMessage(Message msg) {
        sendMessageDelayed(msg, 0);
    }

    public final void sendMessageDelayed(Message msg, long delayMillis) {
        sendMessageAtTime(msg, System.currentTimeMillis() + delayMillis);
    }

    public final void sendMessageAtTime(Message msg, long timeMillis) {
        enqueueMessage(msg, timeMillis);
    }

    private void enqueueMessage(AbstractMessage msg, long timeMillis) {
        msg.target = this;
        msg.when = timeMillis;
        queue.enqueue(msg);
    }

    public final void removeMessages(int what) {
        queue.removeAll(this, what);
    }

    public final void removeCallbacks(Runnable callback) {
        queue.removeAll(this, callback);
    }

}
