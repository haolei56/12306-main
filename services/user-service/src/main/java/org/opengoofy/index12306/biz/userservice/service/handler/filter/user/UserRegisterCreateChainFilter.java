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

package org.opengoofy.index12306.biz.userservice.service.handler.filter.user;

import org.opengoofy.index12306.biz.userservice.common.enums.UserChainMarkEnum;
import org.opengoofy.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.opengoofy.index12306.framework.starter.designpattern.chain.AbstractChainHandler;

/**
 * 用户注册责任链过滤器
 *
 *
 */
public interface UserRegisterCreateChainFilter<T extends UserRegisterReqDTO> extends AbstractChainHandler<UserRegisterReqDTO> {

    @Override
    default String mark() {
        return UserChainMarkEnum.USER_REGISTER_FILTER.name();
    }//获取一个枚举值的名称（即枚举常量的字符串表示）。
}
