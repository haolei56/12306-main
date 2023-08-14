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

package org.opengoofy.index12306.framework.starter.common.threadpool.proxy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池拒绝策略代理执行器
 *
 *
 */
//这段代码定义了一个类 RejectedProxyInvocationHandler，它实现了 Java 动态代理中的 InvocationHandler 接口，用于创建代理对象并在目标方法被拒绝执行时进行处理。
@Slf4j
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    /**
     * Target object
     */
    private final Object target;

    /**
     * Reject count
     */
    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {//实现了 InvocationHandler 接口的核心方法，用于拦截被代理对象的方法调用。
        // 在这个方法中，首先递增拒绝次数，然后输出日志（模拟报警），最后通过反射调用目标对象的方法。如果调用目标方法时发生异常，它会抛出 InvocationTargetException，并将原始异常抛出。
        rejectCount.incrementAndGet();
        try {
            log.error("线程池执行拒绝策略, 此处模拟报警...");
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}