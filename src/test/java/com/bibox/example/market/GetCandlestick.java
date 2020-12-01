package com.bibox.example.market;

import com.bibox.futures.BiboxFuturesClient;
import com.bibox.futures.model.Candlestick;
import com.bibox.futures.model.enums.TimeInterval;

import java.util.List;

public class GetCandlestick {
    public static void main(String[] args) throws Throwable {
        BiboxFuturesClient client = new BiboxFuturesClient();
        List<Candlestick> haveLimit = client.getCandlestick("5BTC_USD",
                TimeInterval.WEEKLY, 10);
        List<Candlestick> noLimit = client.getCandlestick("5BTC_USD", TimeInterval.DAILY);
        System.out.println(haveLimit);
        System.out.println(noLimit);
    }
}
