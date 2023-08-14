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

package org.opengoofy.index12306.frameworks.starter.user.config;

import org.opengoofy.index12306.frameworks.starter.user.core.UserTransmitFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static org.opengoofy.index12306.framework.starter.bases.constant.FilterOrderConstant.USER_TRANSMIT_FILTER_ORDER;

/**
 * 用户配置自动装配
 *
 *
 */
@ConditionalOnWebApplication//这是一个 Spring Boot 的条件注解，它用于在满足 Web 应用条件时启用该配置类。如果应用程序是一个 Web 应用，才会应用这个配置。
public class UserAutoConfiguration {

    /**
     * 用户信息传递过滤器
     */
    @Bean//这是一个 Spring 注解，用于标识一个方法，该方法返回一个 Bean 实例，Spring 容器将该实例纳入管理。
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        //创建一个 FilterRegistrationBean 实例用于注册过滤器。
        // 使用 new UserTransmitFilter() 创建一个 UserTransmitFilter 过滤器实例。
        // 使用 addUrlPatterns("/*") 将过滤器映射到所有 URL 路径。
        // 使用 setOrder(USER_TRANSMIT_FILTER_ORDER) 设置过滤器的执行顺序。USER_TRANSMIT_FILTER_ORDER 是一个常量，表示过滤器的执行顺序。
        // 返回注册的 FilterRegistrationBean 实例。
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(USER_TRANSMIT_FILTER_ORDER);
        return registration;
    }
}
