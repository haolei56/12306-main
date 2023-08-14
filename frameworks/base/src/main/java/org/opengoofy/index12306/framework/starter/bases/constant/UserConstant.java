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

package org.opengoofy.index12306.framework.starter.bases.constant;

/**
 * 用户常量
 *
 *
 */
//static 关键字的用法是将成员标识为与类相关，而不是与类的实例相关。这使得它们可以通过类名直接访问，而无需创建类的实例。静态成员在类加载时初始化，且在类的所有实例之间共享。
public final class UserConstant {

    /**
     * 用户 ID Key
     */
    public static final String USER_ID_KEY = "userId";//它被声明为 public 表示它可以在其他类中直接访问，而 static 表示它是类级别的，不依赖于类的实例，而 final 则表示该变量的值在初始化后不可更改。

    /**
     * 用户名 Key
     */
    public static final String USER_NAME_KEY = "username";

    /**
     * 用户真实名称 Key
     */
    public static final String REAL_NAME_KEY = "realName";

    /**
     * 用户 Token Key
     */
    public static final String USER_TOKEN_KEY = "token";
}
