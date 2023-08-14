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

package org.opengoofy.index12306.frameworks.starter.user.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息实体
 *
 *
 */
@Data//@Data：这是 Lombok 注解，它自动生成了以下内容：
// 所有字段的 getters 和 setters 方法。
// 一个默认的无参构造函数。
//toString 方法，用于生成对象的字符串表示。
//equals 和 hashCode 方法，用于比较对象的内容
@NoArgsConstructor//这是 Lombok 注解，它生成了一个无参的构造函数，用于创建一个对象实例时不需要传递参数。
@AllArgsConstructor//这是 Lombok 注解，它生成了一个包含所有字段的参数的构造函数，用于创建对象实例时需要传递所有字段的值。
@Builder//这是 Lombok 注解，它生成了一个建造者模式的构造器，允许您通过链式调用设置对象的字段值。通过使用建造者模式，您可以更加灵活地创建对象，不必记住字段的顺序。
public class UserInfoDTO {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户 Token
     */
    private String token;
}
