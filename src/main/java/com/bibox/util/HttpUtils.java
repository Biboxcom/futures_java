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

import okhttp3.*;
import okio.ByteString;

import java.io.IOException;

public class HttpUtils {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static final MediaType MT_FORM = MediaType.get("application/x-www-form-urlencoded");
    public static final MediaType MT_JSON = MediaType.get("application/json; charset=utf-8");

    private static String buildUri(String url, HttpQuery query) {
        if (query != null && !query.isEmpty()) {
            return url + "?" + query.toString();
        }
        return url;
    }

    private static void addHeader(Request.Builder builder, HttpHeader header) {
        if (header != null && !header.isEmpty()) {
            for (String key : header.keySet()) {
                builder.addHeader(key, header.get(key));
            }
        }
    }

    private static String executeRequest(Request request) throws IOException {
        Response response = CLIENT.newCall(request).execute();
        return response.body().string();
    }

    public static String doGet(String url, HttpQuery query) throws Throwable {
        return doGet(url, null, query);
    }

    public static String doGet(String url, HttpHeader header, HttpQuery query) throws Throwable {
        Request.Builder builder = new Request.Builder();
        builder.url(buildUri(url, query));
        addHeader(builder, header);
        return executeRequest(builder.build());
    }

    public static String doPost(String url, HttpHeader header, MediaType type, String bodyString) throws Throwable {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(RequestBody.create(type, bodyString));
        addHeader(builder, header);
        return executeRequest(builder.build());
    }

    public static String doDelete(String url, HttpHeader header, HttpQuery query) throws Throwable {
        Request.Builder builder = new Request.Builder();
        builder.url(buildUri(url, query));
        builder.delete();
        addHeader(builder, header);
        return executeRequest(builder.build());
    }

    /**
     * Web Socket
     */
    public static class WebSocket {
        private okhttp3.WebSocket mSocket;

        public void send(String message) {
            mSocket.send(message);
        }

        public void close() {
            mSocket.close(1000, null);
        }
    }

    public static abstract class WebSocketListener {
        public void onOpen(WebSocket socket) {
        }

        public void onMessage(WebSocket socket, String text) {
        }

        public void onMessage(WebSocket socket, byte[] bytes) {
        }

        public void onFailure(WebSocket socket, Throwable error) {
        }

        public void onClosed(WebSocket socket) {
        }
    }

    public static WebSocket createWebSocket(String url, final WebSocketListener listener) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        final WebSocket socket = new WebSocket();
        socket.mSocket = CLIENT.newWebSocket(builder.build(), new okhttp3.WebSocketListener() {
            @Override
            public void onOpen(okhttp3.WebSocket webSocket, Response response) {
                listener.onOpen(socket);
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, String text) {
                listener.onMessage(socket, text);
            }

            @Override
            public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
                listener.onMessage(socket, bytes.toByteArray());
            }

            @Override
            public void onFailure(okhttp3.WebSocket webSocket, Throwable error, Response response) {
                listener.onFailure(socket, error);
            }

            @Override
            public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
                listener.onClosed(socket);
            }
        });
        return socket;
    }

}
