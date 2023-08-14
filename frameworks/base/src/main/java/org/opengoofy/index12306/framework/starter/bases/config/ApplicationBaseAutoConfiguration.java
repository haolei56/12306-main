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

package org.opengoofy.index12306.framework.starter.bases.config;

import org.opengoofy.index12306.framework.starter.bases.ApplicationContextHolder;
import org.opengoofy.index12306.framework.starter.bases.init.ApplicationContentPostProcessor;
import org.opengoofy.index12306.framework.starter.bases.safa.FastJsonSafeMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 应用基础自动装配
 *
 *
 */

//这段代码是为Spring Boot应用程序定义了一些自动配置的Bean。如果应用中没有相应类型的Bean存在，这些Bean将被创建并添加到应用上下文中。
public class ApplicationBaseAutoConfiguration {

    @Bean                                               //注解标志着一个方法，告诉Spring容器将方法返回的对象作为一个Bean注册到容器中。
    @ConditionalOnMissingBean                           //注解表示仅当没有与指定类型相匹配的Bean存在时，才会创建这个Bean。这可以防止覆盖用户自己定义的同类型Bean。
    public ApplicationContextHolder congoApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContentPostProcessor congoApplicationContentPostProcessor(ApplicationContext applicationContext) {
        return new ApplicationContentPostProcessor(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "framework.fastjson.safa-mode", havingValue = "true")//注解用于基于属性的条件装配。在这里，它表示只有当名为 framework.fastjson.safa-mode 的属性值为 "true" 时，才会创建 FastJsonSafeMode Bean。
    public FastJsonSafeMode congoFastJsonSafeMode() {
        return new FastJsonSafeMode();
    }
}
