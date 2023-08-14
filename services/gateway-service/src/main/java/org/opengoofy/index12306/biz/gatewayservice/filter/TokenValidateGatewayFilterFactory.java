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

package org.opengoofy.index12306.biz.gatewayservice.filter;

import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import org.opengoofy.index12306.biz.gatewayservice.config.Config;
import org.opengoofy.index12306.biz.gatewayservice.toolkit.JWTUtil;
import org.opengoofy.index12306.biz.gatewayservice.toolkit.UserInfoDTO;
import org.opengoofy.index12306.framework.starter.bases.constant.UserConstant;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * SpringCloud Gateway Token 拦截器
 *
 *
 */
//这个过滤器工厂用于在 Spring Cloud Gateway 中实现了一个 Token 验证的功能，可以确保在指定条件下，请求的 Token 是否有效，并在需要的情况下将用户信息添加到请求头中。
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public TokenValidateGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * 注销用户时需要传递 Token
     */
    public static final String DELETION_PATH = "/api/user-service/deletion";


    //apply 方法：这是 TokenValidateGatewayFilterFactory 的主要逻辑方法，用于创建和应用过滤器。它接受一个 Config 对象作为参数。
    // 首先，从请求中获取请求路径 requestPath。
    // 然后，通过调用 isPathInBlackPreList 方法判断请求路径是否在黑名单列表中，黑名单列表保存在配置类 Config 的 blackPathPre 字段中。如果请求路径在黑名单中，就进行进一步处理，否则直接放行。
    // 如果请求路径在黑名单中，从请求头中获取 Token（通常在请求头的 "Authorization" 字段中），然后通过调用 JWTUtil.parseJwtToken(token) 来解析 Token 中的用户信息。
    // 使用 validateToken 方法判断解析的用户信息是否有效。如果有效，将用户信息添加到请求头中，并继续传递请求；否则，返回未授权的响应。
    // 特别地，在特定的请求路径 DELETION_PATH 下（通常用于注销用户时），还会将 Token 添加到请求头中。
    // 最后，根据处理结果，将请求继续传递给下一个过滤器或路由处理链。
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            if (isPathInBlackPreList(requestPath, config.getBlackPathPre())) {
                String token = request.getHeaders().getFirst("Authorization");
                // TODO 需要验证 Token 是否有效，有可能用户注销了账户，但是 Token 有效期还未过
                UserInfoDTO userInfo = JWTUtil.parseJwtToken(token);
                if (!validateToken(userInfo)) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                    httpHeaders.set(UserConstant.USER_ID_KEY, userInfo.getUserId());
                    httpHeaders.set(UserConstant.USER_NAME_KEY, userInfo.getUsername());
                    httpHeaders.set(UserConstant.REAL_NAME_KEY, URLEncoder.encode(userInfo.getRealName(), StandardCharsets.UTF_8));
                    if (Objects.equals(requestPath, DELETION_PATH)) {
                        httpHeaders.set(UserConstant.USER_TOKEN_KEY, token);
                    }
                });
                return chain.filter(exchange.mutate().request(builder.build()).build());
            }
            return chain.filter(exchange);
        };
    }

    private boolean isPathInBlackPreList(String requestPath, List<String> blackPathPre) {
        if (CollectionUtils.isEmpty(blackPathPre)) {
            return false;
        }
        return blackPathPre.stream().anyMatch(requestPath::startsWith);
    }

    private boolean validateToken(UserInfoDTO userInfo) {
        return userInfo != null;
    }
}
