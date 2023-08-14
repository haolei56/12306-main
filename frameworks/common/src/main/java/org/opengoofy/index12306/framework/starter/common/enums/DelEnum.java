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

package org.opengoofy.index12306.framework.starter.common.enums;

/**
 * 删除标记枚举
 *
 *
 */
//Java 的枚举类是一种特殊的类，用于定义一个固定数量的命名常量集合。
//枚举类使用 enum 关键字进行定义。每个枚举成员都是枚举类的一个实例对象，它们被定义在枚举类的内部，并通过逗号分隔。每个枚举成员都可以附加值或者方法。

//这段代码定义了一个枚举类型 DelEnum，其中包含了正常状态和删除状态两个枚举成员，以及与状态代码相关的方法。
// 通常，这种枚举类型用于表示记录在数据库中的状态，比如表示数据是否被删除，以便在代码中更好地处理这些状态。
public enum DelEnum {

    /**
     * 正常状态
     */
    NORMAL(0),

    /**
     * 删除状态
     */
    DELETE(1);

    private final Integer statusCode;

    DelEnum(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer code() {
        return this.statusCode;
    }

    public String strCode() {
        return String.valueOf(this.statusCode);
    }

    @Override
    public String toString() {
        return strCode();
    }
}
