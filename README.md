bibox-futures-client 
===========================
#####简介
Bibox平台币本位合约SDK
#####环境依赖
JDK 1.8+

#####使用方法

        // 公开的api 获取kline
        BiboxFuturesClient client = new BiboxFuturesClient();
        List<Candlestick> res = client.getCandlestick("5BTC_USD", CandlestickInterval.WEEKLY,10);
        System.out.println(res);
        
        // 用户的api 转入合约账户
        String apiKey = "use your apiKey";
        String secretKey = "use your secretKey";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        client.transferOut("ETH", BigDecimal.valueOf(0.0001));
        client.transferIn("ETH", BigDecimal.valueOf(0.0001));
        
        // 用户的api 下单
        String apiKey = "use your apiKey";
        String secretKey = "use your secretKey";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setSymbol("5ETH_USD");
        marketOrder.setQty(BigDecimal.valueOf(1));
        marketOrder.setAction(PositionAction.EXIT);
        marketOrder.setSide(TradeSide.LONG);
        String orderId = client.placeOrder(marketOrder);
        System.out.println("the market order_id:" + orderId);
        
        // 公开的订阅 订阅kline
        BiboxFuturesClient client = new BiboxFuturesClient();
        String symbol = "BTC_USD";
        client.subscribeCandlestick(symbol, TimeInterval.ONE_MINUTE, (x) -> {
            x.forEach(System.out::println);
            // ...
        });
        
        // 用户的订阅 用户资产数据
        String apiKey = "use your apiKey";
        String secretKey = "use your secretKey";
        BiboxFuturesClient client = new BiboxFuturesClient(apiKey, secretKey);
        client.subscribeAccount(x -> {
            x.forEach(System.out::println);
            // ...
        });
        
        // 更多的可以参考测试用例





#####V1.0.0 版本内容更新
无