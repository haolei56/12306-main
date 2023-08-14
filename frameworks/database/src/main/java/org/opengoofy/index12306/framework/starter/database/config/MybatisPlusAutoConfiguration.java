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

package org.opengoofy.index12306.framework.starter.database.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.opengoofy.index12306.framework.starter.database.handler.CustomIdGenerator;
import org.opengoofy.index12306.framework.starter.database.handler.MyMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * MybatisPlus 配置文件
 *
 *
 */
public class MybatisPlusAutoConfiguration {

    /**
     * 分页插件
     */
    //配置 Mybatis Plus 的分页插件。分页插件用于支持数据库分页查询功能。在这里，创建了一个 MybatisPlusInterceptor 实例，并添加了一个内部的分页插件 PaginationInnerInterceptor，并指定数据库类型为 MySQL。
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 元数据填充
     */
    //配置 Mybatis Plus 的元数据填充功能。元数据填充可以在插入和更新数据时自动填充一些字段，比如创建时间和修改时间。在这里，创建了一个 MyMetaObjectHandler 实例，该类会根据实际情况自动填充时间和其他元数据。
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 自定义雪花算法 ID 生成器
     */
    //配置 Mybatis Plus 的主键 ID 生成器。在这里，使用了自定义的雪花算法 ID 生成器 CustomIdGenerator，并将它标记为主要的（@Primary）。主键 ID 生成器用于生成数据库记录的唯一标识。
    @Bean
    @Primary
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }
}
