<h1 align="center">Welcome to Bibox Futures Client 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-v1.0.0-blue.svg?cacheSeconds=2592000" />
  <a href="#" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg" />
  </a>
</p>

> Bibox平台币本位合约Java版本SDK

### 🏠 [Homepage](https://futures.bibox.me/zh/futures)

## Dependency

```sh
需要 jdk1.8+
```

## Usage

```sh
// 如果出现网络问题,你可能需要代理或者重设client的相关地址参数

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
```

## Author

👤 **Biboxcom**

* Website: https://github.com/Biboxcom
* Github: [@Biboxcom](https://github.com/Biboxcom)

## Show your support

Give a ⭐️ if this project helped you!


