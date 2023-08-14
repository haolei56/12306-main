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

package org.opengoofy.index12306.biz.aggregationservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 12306 聚合服务应用启动器
 *
 *
 */
@SpringBootApplication(scanBasePackages = {
        "org.opengoofy.index12306.biz.userservice",
        "org.opengoofy.index12306.biz.ticketservice",
        "org.opengoofy.index12306.biz.orderservice",
        "org.opengoofy.index12306.biz.payservice"
})
// 这个注解告诉 Spring Boot 扫描并加载指定的包（以及子包）中的 MyBatis Mapper 接口，使这些接口可以被自动实例化为 MyBatis 的映射对象。
@MapperScan(value = {
        "org.opengoofy.index12306.biz.userservice.dao.mapper",
        "org.opengoofy.index12306.biz.ticketservice.dao.mapper",
        "org.opengoofy.index12306.biz.orderservice.dao.mapper",
        "org.opengoofy.index12306.biz.payservice.dao.mapper"
})
//这个注解启用了 Spring Cloud Feign 客户端，用于定义和创建远程服务调用的接口。指定的包路径表示扫描并加载远程服务调用的接口定义。
@EnableFeignClients("org.opengoofy.index12306.biz.ticketservice.remote")
public class AggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregationServiceApplication.class, args);
    }
}
