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

package org.opengoofy.index12306.biz.orderservice.common.constant;

/**
 * RocketMQ 订单服务常量类
 *
 *
 */
//这段代码定义了一些 RocketMQ 相关的常量，用于标识 RocketMQ 中的 Topic（主题）和 Tag（标签），以及消费者组（Consumer Group）。
public final class OrderRocketMQConstant {

    /**
     * 支付服务相关业务 Topic Key
     */
    //这个 Topic 用于处理支付服务的消息。
    public static final String PAY_GLOBAL_TOPIC_KEY = "index12306_pay-service_topic${unique-name:}";

    /**
     * 支付结果回调订单 Tag Key
     */
    //这个 Tag 用于区分不同类型的消息，例如支付结果回调订单消息。
    public static final String PAY_RESULT_CALLBACK_ORDER_TAG_KEY = "index12306_pay-service_pay-result-callback-order_tag${unique-name:}";

    /**
     * 支付结果回调订单消费者组 Key
     */
    //消费者组用于标识一组消费者，这些消费者同时消费相同 Topic 下的消息。
    public static final String PAY_RESULT_CALLBACK_ORDER_CG_KEY = "index12306_pay-service_pay-result-callback-order_cg${unique-name:}";
}
