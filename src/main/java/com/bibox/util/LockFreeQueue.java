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

public class LockFreeQueue<E> {

    protected static class ElementHolder<E> {
        ElementHolder<E> next;
        E element;
    }

    protected final SpinLock spinLock = new SpinLock();
    protected final Lock lock = new ReentrantLock();
    protected final Condition condition = lock.newCondition();
    protected int waitCount = 0;
    protected ElementHolder<E> head = null;
    protected ElementHolder<E> tail = null;
    private ElementHolder<E> idles = null;

    /**
     * 获取 holder 实例, 务必在加锁后使用
     */
    protected ElementHolder<E> obtainHolder(E e) {
        ElementHolder<E> holder = idles;
        if (holder == null) {
            holder = new ElementHolder<>();
        } else {
            idles = holder.next;
        }
        holder.next = null;
        holder.element = e;
        return holder;
    }

    /**
     * 回收 holder 实例, 务必在加锁后使用
     */
    protected E recycleHolder(ElementHolder<E> holder) {
        E e = holder.element;
        holder.element = null;
        holder.next = idles;
        idles = holder;
        return e;
    }

    protected E removeFirst() {
        ElementHolder<E> holder = head;
        if (head == tail) {
            head = tail = null;
        } else {
            head = holder.next;
        }
        return recycleHolder(holder);
    }

    protected void addLast(E e) {
        ElementHolder<E> holder = obtainHolder(e);
        if (head == null) {
            head = tail = holder;
        } else {
            tail.next = holder;
            tail = holder;
        }
    }

    protected void wakeUp() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public E dequeue() {
        try {
            for (; ; ) {
                spinLock.lock();
                if (head != null) {
                    E e = removeFirst();
                    spinLock.unlock();
                    return e;
                } else {
                    spinLock.unlock();
                }

                lock.lock();
                spinLock.lock();
                if (head != null) {
                    E e = removeFirst();
                    spinLock.unlock();
                    lock.unlock();
                    return e;
                }
                waitCount++;
                spinLock.unlock();
                condition.await();
                spinLock.lock();
                waitCount--;
                spinLock.unlock();
                lock.unlock();
            }
        } catch (InterruptedException e) {
            return null;
        }
    }

    public void enqueue(E e) {
        spinLock.lock();
        addLast(e);
        int waitCount = this.waitCount;
        spinLock.unlock();
        if (waitCount > 0) {
            wakeUp();
        }
    }

}
