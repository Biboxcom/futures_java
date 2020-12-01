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

import java.nio.ByteBuffer;

public class Hex {

    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static char[] encodeHex(byte[] data, int offset, int length) {
        char[] hexChars = new char[length << 1];
        for (int i = 0, j = 0; i < length; i++, j += 2) {
            int v = data[offset + i] & 0xFF;
            hexChars[j] = HEX_CHARS[v >>> 4];
            hexChars[j + 1] = HEX_CHARS[v & 0x0F];
        }
        return hexChars;
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, 0, data.length);
    }

    public static char[] encodeHex(ByteBuffer data) {
        return encodeHex(data.array(), data.position(), data.remaining());
    }

    public static String encodeHexString(byte[] data, int offset, int length) {
        return new String(encodeHex(data, offset, length));
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    public static String encodeHexString(ByteBuffer data) {
        return new String(encodeHex(data));
    }

}
