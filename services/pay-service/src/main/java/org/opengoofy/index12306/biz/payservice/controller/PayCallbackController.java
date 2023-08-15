/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengoofy.index12306.biz.payservice.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import org.opengoofy.index12306.biz.payservice.common.enums.PayChannelEnum;
import org.opengoofy.index12306.biz.payservice.convert.PayCallbackRequestConvert;
import org.opengoofy.index12306.biz.payservice.dto.PayCallbackCommand;
import org.opengoofy.index12306.biz.payservice.dto.base.PayCallbackRequest;
import org.opengoofy.index12306.biz.payservice.handler.AliPayCallbackHandler;
import org.opengoofy.index12306.framework.starter.designpattern.strategy.AbstractStrategyChoose;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付结果回调
 *
 *
 */
@RestController
@RequiredArgsConstructor
public class PayCallbackController {

    private final AbstractStrategyChoose abstractStrategyChoose;// 这是一个控制器类的成员变量，用于注入一个 AbstractStrategyChoose 的实例，这个实例用于策略模式的实现。

    /**
     * 支付宝回调
     * 调用支付宝支付后，支付宝会调用此接口发送支付结果
     */
    @PostMapping("/api/pay-service/callback/alipay")
    public void callbackAlipay(@RequestParam Map<String, Object> requestParam) {//这是一个处理支付宝支付回调的方法。它接收一个名为 requestParam 的参数，类型是 Map<String, Object>，表示请求中的所有参数。该方法没有返回值（void）。
        //这一行代码将从 requestParam 构造一个 PayCallbackCommand 对象。BeanUtil 是一个工具类，用于进行 Java 对象之间的属性复制。这里的作用是将请求参数映射到 PayCallbackCommand 对象中。
        PayCallbackCommand payCallbackCommand = BeanUtil.mapToBean(requestParam, PayCallbackCommand.class, true, CopyOptions.create());
        payCallbackCommand.setChannel(PayChannelEnum.ALI_PAY.getCode());//这一行代码设置支付渠道为支付宝支付。
        payCallbackCommand.setOrderRequestId(requestParam.get("out_trade_no").toString());//这一行代码设置订单请求 ID，从请求参数中获取。
        payCallbackCommand.setGmtPayment(DateUtil.parse(requestParam.get("gmt_payment").toString()));//这一行代码设置支付时间，从请求参数中获取并转换为日期格式。
        //这一行代码将 PayCallbackCommand 对象转换为 PayCallbackRequest 对象，通过调用静态方法 PayCallbackRequestConvert.command2PayCallbackRequest。
        PayCallbackRequest payCallbackRequest = PayCallbackRequestConvert.command2PayCallbackRequest(payCallbackCommand);
        /**
         * {@link AliPayCallbackHandler}
         */
        // 策略模式：通过策略模式封装支付回调渠道，支付回调时动态选择对应的支付回调组件
        //这一行代码使用策略模式，通过调用 abstractStrategyChoose 对应的方法，根据传入的标识选择并执行对应的支付回调处理组件（策略）。具体的处理过程会根据策略模式的实现进行选择和执行。
        abstractStrategyChoose.chooseAndExecute(payCallbackRequest.buildMark(), payCallbackRequest);
    }
}
