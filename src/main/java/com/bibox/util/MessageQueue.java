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

import java.util.concurrent.TimeUnit;

public class MessageQueue extends LockFreeQueue<AbstractMessage> {

    @Override
    public AbstractMessage dequeue() {
        try {
            for (; ; ) {
                long now = System.currentTimeMillis();

                spinLock.lock();
                if (head != null && head.element.when <= now) {
                    AbstractMessage message = removeFirst();
                    spinLock.unlock();
                    return message;
                } else {
                    spinLock.unlock();
                }

                lock.lock();
                spinLock.lock();
                if (head != null) {
                    long time = head.element.when - now;
                    if (time <= 0) {
                        AbstractMessage message = removeFirst();
                        spinLock.unlock();
                        lock.unlock();
                        return message;
                    }
                    waitCount++;
                    spinLock.unlock();
                    condition.await(time, TimeUnit.MILLISECONDS);
                } else {
                    waitCount++;
                    spinLock.unlock();
                    condition.await();
                }
                spinLock.lock();
                waitCount--;
                spinLock.unlock();
                lock.unlock();
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public void enqueue(AbstractMessage msg) {
        spinLock.lock();
        ElementHolder<AbstractMessage> holder = obtainHolder(msg);
        ElementHolder<AbstractMessage> p = head;
        if (p == null) {
            head = tail = holder;
        } else if (msg.when < p.element.when) {
            holder.next = p;
            head = holder;
        } else {
            ElementHolder<AbstractMessage> prev;
            for (; ; ) {
                prev = p;
                p = p.next;
                if (p == null) {
                    prev.next = tail = holder;
                    break;
                }
                if (msg.when < p.element.when) {
                    holder.next = p;
                    prev.next = holder;
                    break;
                }
            }
        }
        int waitCount = this.waitCount;
        spinLock.unlock();
        if (waitCount > 0) {
            wakeUp();
        }
    }

    /**
     * 从队列中移除发送给指定 Handler 的指定类型的消息
     */
    public final void removeAll(Handler h, int what) {
        spinLock.lock();
        if (head != null) {
            ElementHolder<AbstractMessage> prev = head;
            ElementHolder<AbstractMessage> p = prev.next;

            while (p != null) {
                AbstractMessage msg = p.element;
                if (msg.target == h &&
                        (msg instanceof Message && ((Message) msg).type == what)) {
                    prev.next = p = p.next;
                } else {
                    prev = p;
                    p = p.next;
                }
            }
            tail = prev;

            p = head;
            AbstractMessage msg = p.element;
            if (msg.target == h &&
                    (msg instanceof Message && ((Message) msg).type == what)) {
                head = p.next;
            }
        }
        spinLock.unlock();
    }

    /**
     * 从队列中移除发送给指定 Handler 的指定 Runnable
     */
    public final void removeAll(Handler h, Runnable r) {
        spinLock.lock();
        if (head != null) {
            ElementHolder<AbstractMessage> prev = head;
            ElementHolder<AbstractMessage> p = prev.next;

            while (p != null) {
                AbstractMessage msg = p.element;
                if (msg.target == h &&
                        (msg instanceof CallbackMessage && ((CallbackMessage) msg).callback == r)) {
                    prev.next = p = p.next;
                } else {
                    prev = p;
                    p = p.next;
                }
            }
            tail = prev;

            p = head;
            AbstractMessage msg = p.element;
            if (msg.target == h &&
                    (msg instanceof CallbackMessage && ((CallbackMessage) msg).callback == r)) {
                head = p.next;
            }
        }
        spinLock.unlock();
    }

}
