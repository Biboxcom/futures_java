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
import com.bibox.futures.model.*;
import com.bibox.futures.model.enums.*;
import com.bibox.util.HttpUtils;
import com.bibox.util.Listener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BiboxFuturesClient extends BiboxFuturesClientBase {

    public BiboxFuturesClient() {
    }

    public BiboxFuturesClient(String apiKey, String secretKey) {
        super(apiKey, secretKey);
    }

    /**
     * 转入资金
     *
     * @param asset  账户
     * @param amount 金额
     */
    public void transferIn(String asset, BigDecimal amount) throws Throwable {
        transfer(asset, amount, TransferDirection.IN);
    }

    /**
     * 转出资金
     *
     * @param asset  账户
     * @param amount 金额
     */
    public void transferOut(String asset, BigDecimal amount) throws Throwable {
        transfer(asset, amount, TransferDirection.OUT);
    }

    /**
     * 提交委托
     */
    public String placeOrder(Order order) throws Throwable {
        if (order instanceof LimitOrder) {
            return placeOrder((LimitOrder) order);
        } else if (order instanceof MarketOrder) {
            return placeOrder((MarketOrder) order);
        } else {
            throw new RuntimeException("couldn't support this order");
        }
    }

    /**
     * 撤销委托
     */
    public void cancelOrder(String orderId) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("order_id", orderId);

        JSONUtils.parseError(doPost(URL_CANCEL_ORDER, json.toJSONString()));
    }

    /**
     * 撤销指定 id 的全部委托
     */
    public void cancelAllOrders(OrderIdSet orderIdSet) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("order_ids", orderIdSet);

        JSONUtils.parseError(doPost(URL_CANCEL_ORDERS, json.toJSONString()));
    }

    /**
     * 撤销指定交易对上的全部委托
     */
    public void cancelAllOrders(String symbol) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));

        JSONUtils.parseError(doPost(URL_CANCEL_ALL_ORDERS, json.toJSONString()));
    }

    /**
     * 划转保证金
     *
     * @param symbol 合约名称
     * @param side   仓位方向, 取值自 TradeSide 类
     * @param amount 保证金数量, 正值为转入, 负值为转出
     */
    public void transferMargin(String symbol, TradeSide side, BigDecimal amount) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));
        json.put("side", side.getValue());
        json.put("margin", amount);

        JSONUtils.parseError(doPost(URL_TRANSFER_MARGIN, json.toJSONString()));
    }

    /**
     * 变更保证金模式
     *
     * @param symbol 合约名称
     * @param mode   仓位模式
     */
    public void changeMarginMode(String symbol, MarginMode mode) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));
        json.put("mode", mode.getValue());
        List<Position> positions = getAllPositions(symbol);
        for (Position position : positions) {
            if (position.getMarginMode() == mode) {
                return;
            }
            if (position.getSide() == TradeSide.LONG) {
                json.put("leverage_long", position.getLeverage());
            } else {
                json.put("leverage_short", position.getLeverage());
            }
        }

        JSONUtils.parseError(doPost(URL_CHANGE_MARGIN_MODE, json.toJSONString()));
    }

    /**
     * 变更杠杆
     *
     * @param symbol   合约名称
     * @param side     仓位方向, 取值自 TradeSide 类
     * @param leverage 杠杆
     */
    public void changeLeverage(String symbol, TradeSide side, int leverage) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));
        TradeSide otherSide = side == TradeSide.LONG ? TradeSide.SHORT : TradeSide.LONG;
        Position position = getPosition(symbol, otherSide);

        json.put("mode", position.getMarginMode().getValue());
        if (otherSide == TradeSide.LONG) {
            json.put("leverage_long", position.getLeverage());
            json.put("leverage_short", leverage);
        } else {
            json.put("leverage_long", leverage);
            json.put("leverage_short", position.getLeverage());
        }

        JSONUtils.parseError(doPost(URL_CHANGE_MARGIN_MODE, json.toJSONString()));
    }

    /**
     * 查询账户资产
     */
    public List<Account> getAccounts() throws Throwable {
        JSONObject json = new JSONObject();

        return JSONUtils.parseAccounts(doPost(URL_ACCOUNT, json.toJSONString()));
    }

    /**
     * 查询账户资产
     *
     * @param asset BTC/ETH/...
     */
    public Account getAccount(String asset) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("coin", asset);

        return JSONUtils.parseAccounts(doPost(URL_ACCOUNT, json.toJSONString())).get(0);
    }

    /**
     * 获取指定持仓
     *
     * @param symbol 合约名称
     * @param side   持仓方向
     */
    public Position getPosition(String symbol, TradeSide side) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));
        json.put("side", side.getValue());

        return positionsWrapper(
                JSONUtils.parsePositions(doPost(URL_POSITION, json.toJSONString()))).get(0);
    }

    /**
     * 获取指定方向的全部持仓
     *
     * @param side 持仓方向
     */
    public List<Position> getAllPositions(TradeSide side) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("side", side.getValue());

        return positionsWrapper(
                JSONUtils.parsePositions(doPost(URL_POSITION, json.toJSONString())));
    }

    /**
     * 获取在指定合约上的全部持仓
     *
     * @param symbol 合约名称
     */
    public List<Position> getAllPositions(String symbol) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));

        return positionsWrapper(
                JSONUtils.parsePositions(doPost(URL_POSITION, json.toJSONString())));
    }

    /**
     * 获取仓位变化记录
     *
     * @param symbol 合约名称
     * @param page   页数
     * @param size   每夜数量
     */
    public Pager<PositionUpdate> getPositionUpdates(
            String symbol, int page, int size) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("pair", convertSymbol(symbol));
        json.put("page", page);
        json.put("size", size);

        return JSONUtils.parsePositionEntriesByPage(
                doPost(URL_POSITION_CHANGE_HISTORY, json.toJSONString()));
    }

    /**
     * 获取用户成交记录
     *
     * @param orderId 委托id
     * @param page    页数
     * @param size    每页数量
     */
    public Pager<Fill> getFills(String orderId, int page, int size) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("orderId", convertSymbol(orderId));
        json.put("page", page);
        json.put("size", size);

        return JSONUtils.parseTradesByPage(doPost(URL_TRADES, json.toJSONString()), orderId);
    }

    /**
     * 获取符合指定 OrderQuery 条件的当前委托
     */
    public Pager<Order> getOpenOrders(OrderQuery query) throws Throwable {
        // this method will ignore the query's status
        JSONObject json = new JSONObject();
        Integer page = Optional.ofNullable(query.getPage()).orElse(1);
        Integer size = Optional.ofNullable(query.getSize()).orElse(10);
        json.put("page", page);
        json.put("size", size);
        Optional.ofNullable(query.getSymbol())
                .ifPresent(item -> json.put("pair", convertSymbol(item)));
        Optional.ofNullable(ApiOrderSide.lookupOrderSide(
                query.getSide(), query.getAction()))
                .ifPresent(item -> json.put("order_side", item.getValue()));

        return JSONUtils.parseOpenOrders(doPost(URL_QUERY_OPEN_ORDER, json.toJSONString()));
    }

    /**
     * 获取指定 orderId 的委托
     */
    public Order getOrder(String orderId) throws Throwable {
        JSONObject json = new JSONObject();
        OrderIdSet orderIdSet = new OrderIdSet();
        orderIdSet.add(orderId);
        ClientOrderIdSet clientOrderIdSet = new ClientOrderIdSet();
        clientOrderIdSet.add(orderId);
        json.put("orderIds", orderIdSet);
        json.put("clientOids", clientOrderIdSet);

        return JSONUtils.parseOrders(doPost(URL_QUERY_ORDER, json.toJSONString())).get(0);
    }

    /**
     * 获取指定 orderId 的全部委托
     */
    public List<Order> getOrders(OrderIdSet orderIds) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("orderIds", orderIds);

        return JSONUtils.parseOrders(doPost(URL_QUERY_ORDER, json.toJSONString()));
    }

    /**
     * 获取指定 clientOrderId 的全部委托
     */
    public List<Order> getOrders(ClientOrderIdSet clientOrderIds) throws Throwable {
        JSONObject json = new JSONObject();
        json.put("clientOids", clientOrderIds);

        return JSONUtils.parseOrders(doPost(URL_QUERY_ORDER, json.toJSONString()));
    }

    /**
     * 获取符合指定 OrderQuery 条件的历史委托
     */
    public Pager<Order> getOrders(OrderQuery query) throws Throwable {
        JSONObject json = new JSONObject();
        Integer page = Optional.ofNullable(query.getPage()).orElse(1);
        Integer size = Optional.ofNullable(query.getSize()).orElse(10);
        json.put("page", page);
        json.put("size", size);
        Optional.ofNullable(query.getSymbol())
                .ifPresent(item -> json.put("pair", convertSymbol(item)));
        Optional.ofNullable(query.getStatus())
                .ifPresent(item -> json.put("status", Arrays.asList(item.getValue())));
        Optional.ofNullable(ApiOrderSide.lookupOrderSide(
                query.getSide(), query.getAction()))
                .ifPresent(item -> json.put("side", item.getValue()));

        return JSONUtils.parseOrdersByPage(doPost(URL_QUERY_ORDER_HISTORY, json.toJSONString()));
    }

    /**
     * 获取全部合约的信息 包含指数价格 标记价格 资金费率等
     */
    public List<ContractInfo> getContractInfos() throws Throwable {
        return contractInfosWrapper(JSONUtils.parseContractInfo(HttpUtils.doGet(
                rest(URL_MARK_PRICE), newQuery())));
    }

    /**
     * 获取指定合约的信息
     */
    public ContractInfo getContractInfo(String symbol) throws Throwable {
        return getContractInfos().stream()
                .filter(item -> convertSymbol(symbol).equals(item.getSymbol())).findFirst().get();
    }

    /**
     * 获取全部合约的详情
     */
    public List<Contract> getContracts() throws Throwable {
        return contractsWrapper(JSONUtils.parseContracts(HttpUtils.doGet(
                rest(URL_CONTRACT), newQuery())));
    }

    /**
     * 获取指定合约的详情
     */
    public Contract getContract(String symbol) throws Throwable {
        return getContracts().stream()
                .filter(item -> convertSymbol(symbol).equals(item.getSymbol())).findFirst().get();
    }

    /**
     * 获取指定合约的 K 线
     *
     * @param symbol       合约名称
     * @param timeInterval 周期
     * @param limit        数量限制
     */
    public List<Candlestick> getCandlestick(
            String symbol, TimeInterval timeInterval, int limit) throws Throwable {
        return JSONUtils.parseCandlesticks(HttpUtils.doGet(rest(URL_CANDLESTICK), newQuery()
                .put("period", timeInterval.toString())
                .put("pair", convertSymbol(convertSymbol(symbol)))
                .put("size", String.valueOf(limit))));
    }

    /**
     * 获取指定合约的 K 线, 最多 100 周期数据
     *
     * @param symbol   合约名称
     * @param timeInterval 周期
     */
    public List<Candlestick> getCandlestick(
            String symbol, TimeInterval timeInterval) throws Throwable {
        return getCandlestick(symbol, timeInterval, 100);
    }

    /**
     * 获取深度
     *
     * @param symbol 合约名称
     * @param limit  数量限制
     */
    public OrderBook getOrderBook(String symbol, int limit) throws Throwable {
        return JSONUtils.parseOrderBook(HttpUtils.doGet(rest(URL_ORDER_BOOK), newQuery()
                .put("pair", convertSymbol(symbol))
                .put("size", String.valueOf(limit))));
    }

    /**
     * 获取深度
     *
     * @param symbol 合约名称
     */
    public OrderBook getOrderBook(String symbol) throws Throwable {
        return JSONUtils.parseOrderBook(HttpUtils.doGet(rest(URL_ORDER_BOOK), newQuery()
                .put("pair", convertSymbol(symbol))));
    }


    /**
     * 订阅 K 线数据
     *
     * @param symbol       合约名称
     * @param timeInterval 周期
     */
    public void subscribeCandlestick(
            String symbol, TimeInterval timeInterval, Listener<List<Candlestick>> listener) {
        subscribe(new CandlestickSubscription(this, symbol, timeInterval, listener));
    }

    /**
     * 取消 K 线数据的订阅
     */
    public void unsubscribeCandlestick(String symbol, TimeInterval timeInterval) {
        unsubscribe(CandlestickSubscription.buildChannelName(symbol, timeInterval));
    }

    /**
     * 订阅标记价格
     */
    public void subscribeMarketPrice(String symbol, Listener<List<MarkPrice>> listener) {
        subscribe(new MarketPriceSubscription(this, symbol, listener));
    }

    /**
     * 取消订阅标记价格
     */
    public void unsubscribeMarketPrice(String symbol) {
        unsubscribe(MarketPriceSubscription.buildChannelName(symbol));
    }

    /**
     * 订阅指定合约的深度
     */
    public void subscribeOrderBook(String symbol, Listener<OrderBook> listener) {
        subscribe(new OrderBookSubscription(this, symbol, listener));
    }

    /**
     * 取消对指定合约的深度订阅
     */
    public void unsubscribeOrderBook(String symbol) {
        unsubscribe(OrderBookSubscription.buildChannelName(symbol));
    }

    /**
     * 订阅市场成交记录
     */
    public void subscribeTrade(String symbol, Listener<List<Trade>> listener) {
        subscribe(new TradeSubscription(this, symbol, listener));
    }

    /**
     * 取消订阅最新成交价
     */
    public void unsubscribeTrade(String symbol) {
        unsubscribe(TradeSubscription.buildChannelName(symbol));
    }

    /**
     * 订阅指定合约的 Ticker 数据
     */
    public void subscribeTicker(String symbol, Listener<Ticker> listener) {
        subscribe(new TickerSubscription(this, symbol, listener));
    }

    /**
     * 取消订阅指定合约的 Ticker 数据
     */
    public void unsubscribeTicker(String symbol) {
        unsubscribe(TickerSubscription.buildChannelName(symbol));
    }

    /**
     * 订阅资产账户变化信息
     */
    public void subscribeAccount(Listener<List<Account>> listener) {
        subscribe(new AccountSubscription(this, listener));
    }

    /**
     * 订阅持仓变化消息
     */
    public void subscribePosition(Listener<List<Position>> listener) {
        subscribe(new PositionSubscription(this, listener));
    }

    /**
     * 订阅持仓被动变化信息 目前只支持爆仓类型
     */
    public void subscribePositionUpdate(Listener<List<PositionUpdate>> listener) {
        subscribe(new PositionUpdateSubscription(this, listener));
    }

    /**
     * 订阅与委托相关的信息
     */
    public void subscribeOrder(Listener<List<Order>> listener) {
        subscribe(new OrderSubscription(this, listener));
    }

    /**
     * 订阅用户数据解析成交明细
     */
    public void subscribeFill(Listener<List<Fill>> listener) {
        subscribe(new FillSubscription(this, listener));
    }

    /**
     * 取消全部对用户数据的订阅
     */
    public void unsubscribePrivateChannel() {
        unsubscribeAllPrivateSubscriptions();
    }

}
