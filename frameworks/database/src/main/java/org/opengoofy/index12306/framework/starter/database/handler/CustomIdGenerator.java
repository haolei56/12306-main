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

package org.opengoofy.index12306.framework.starter.database.handler;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.opengoofy.index12306.framework.starter.distributedid.toolkit.SnowflakeIdUtil;

/**
 * 自定义雪花算法生成器
 *
 *
 */
//这段代码定义了一个自定义的主键 ID 生成器类 CustomIdGenerator，实现了 Mybatis Plus 框架的 IdentifierGenerator 接口。
// IdentifierGenerator 接口是 Mybatis Plus 提供的用于生成主键 ID 的接口，通过实现该接口可以自定义主键 ID 的生成方式。

//雪花算法的特点是将生成的 ID 分为多个部分，包括时间戳、机器标识、数据中心标识、序列号等。通过组合这些部分，可以生成全局唯一的主键 ID。这种方式避免了传统的数据库自增主键在分布式系统中可能导致的性能瓶颈和冲突问题。
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }
}
