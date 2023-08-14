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
 * 全局过滤器顺序执行常量类
 *
 *
 */
//final 关键字的用法是确保某个实体的不可更改性：
// 对于类，它防止其他类继承该类。
// 对于方法，它防止子类重写该方法。
// 对于变量，它标识变量为一个常量，其值在初始化后不能再改变。
public final class FilterOrderConstant {

    /**
     * 用户信息传递过滤器执行顺序排序
     */
    public static final int USER_TRANSMIT_FILTER_ORDER = 100;//这是一个常量，用于表示某种过滤器的执行顺序。
    // 过滤器可以有不同的执行顺序，这是通过为每个过滤器指定一个整数值来实现的。在这里，USER_TRANSMIT_FILTER_ORDER 常量的值 100 表示用户信息传递过滤器应该在过滤器链中的其他过滤器之前执行。
}
