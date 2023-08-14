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

package org.opengoofy.index12306.framework.starter.web.initialize;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import static org.opengoofy.index12306.framework.starter.web.config.WebAutoConfiguration.INITIALIZE_PATH;

/**
 * 通过 {@link InitializeDispatcherServletController} 初始化 {@link DispatcherServlet}
 *
 *
 */
//这段代码的目的是在应用程序启动完成后，通过 RestTemplate 向本地服务器发起一个初始化请求。这种操作通常用于触发某些初始化逻辑，例如在应用程序启动后进行一些数据加载或设置操作。
@RequiredArgsConstructor
public final class InitializeDispatcherServletHandler implements CommandLineRunner {

    private final RestTemplate restTemplate;

    private final ConfigurableEnvironment configurableEnvironment;

    //这是 CommandLineRunner 接口的方法，会在应用程序启动完成后自动执行。在这个方法中，首先构建了一个 URL，用于向本地服务器发起 HTTP GET 请求。URL 的构建基于配置属性 server.port 和 server.servlet.context-path。
    // configurableEnvironment.getProperty("server.port", "8080")：获取配置属性 "server.port"，如果不存在则默认为 "8080"。
    // configurableEnvironment.getProperty("server.servlet.context-path", "")：获取配置属性 "server.servlet.context-path"，如果不存在则默认为空字符串。
    // restTemplate.execute(url, HttpMethod.GET, null, null)：使用 RestTemplate 对象执行 HTTP GET 请求，访问构建的 URL。
    @Override
    public void run(String... args) throws Exception {
        String url = String.format("http://127.0.0.1:%s%s",
                configurableEnvironment.getProperty("server.port", "8080") + configurableEnvironment.getProperty("server.servlet.context-path", ""),
                INITIALIZE_PATH);
        try {
            restTemplate.execute(url, HttpMethod.GET, null, null);
        } catch (Throwable ignored) {
        }
    }
}
