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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSONObject;

public class UncompressUtils {

    public static String decodeBytes(byte[] array) {
        String message = null;
        byte zipFlag = array[0];
        int offset = 1;
        int length = array.length - offset;
        if (zipFlag == 0) {
            message = new String(array, offset, length, Charset.defaultCharset());
        } else if (zipFlag == 1) {
            message = UncompressUtils.uncompress(array, offset, length);
        }
        return message;
    }

    public static boolean isGzip(JSONObject object) {
        Integer binary = object.getInteger("binary");
        if (binary == null) {
            return false;
        }
        return binary == 1;
    }

    public static String uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return uncompress(bytes, 0, bytes.length);
    }

    public static String uncompress(byte[] bytes, int offset, int length) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes, offset, length);
             GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buffer = new byte[1024];
            int len = gzipInputStream.read(buffer);
            while (len >= 0) {
                outputStream.write(buffer, 0, len);
                len = gzipInputStream.read(buffer);
            }
            return new String(outputStream.toByteArray(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
