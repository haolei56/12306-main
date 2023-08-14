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

package org.opengoofy.index12306.framework.starter.common.threadpool.build;

import org.opengoofy.index12306.framework.starter.designpattern.builder.Builder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程工厂 {@link ThreadFactory} 构建器, 构建者模式
 *
 *
 */
//这段代码定义了一个名为 ThreadFactoryBuilder 的类，用于创建和配置线程工厂（ThreadFactory）实例。线程工厂用于创建线程，并可以设置线程的各种属性，如线程名称、是否为守护线程、优先级等。
public final class ThreadFactoryBuilder implements Builder<ThreadFactory> {

    private static final long serialVersionUID = 1L;

    private ThreadFactory backingThreadFactory;//：一个可选的成员变量，表示后备的线程工厂。如果提供了后备线程工厂，则使用它来创建线程；否则将使用默认的线程工厂。

    private String namePrefix;//用于指定线程名称的前缀。

    private Boolean daemon;//一个布尔值，表示线程是否为守护线程。

    private Integer priority;//一个整数，表示线程的优先级。范围在 Thread.MIN_PRIORITY 和 Thread.MAX_PRIORITY 之间。

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;//一个用于处理未捕获异常的处理器。

    public ThreadFactoryBuilder threadFactory(ThreadFactory backingThreadFactory) {//设置后备线程工厂。
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    public ThreadFactoryBuilder prefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder priority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(String.format("Thread priority ({}) must be >= {}", priority, Thread.MIN_PRIORITY));
        }
        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format("Thread priority ({}) must be <= {}", priority, Thread.MAX_PRIORITY));
        }
        this.priority = priority;
        return this;
    }

    public void uncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }//构建并返回一个配置好的线程工厂实例。

    @Override
    public ThreadFactory build() {
        return build(this);
    }

    private static ThreadFactory build(ThreadFactoryBuilder builder) {//根据配置创建线程工厂实例的内部方法。该方法根据提供的配置参数，构建并返回一个线程工厂的实现。
        final ThreadFactory backingThreadFactory = (null != builder.backingThreadFactory)
                ? builder.backingThreadFactory
                : Executors.defaultThreadFactory();
        final String namePrefix = builder.namePrefix;
        final Boolean daemon = builder.daemon;
        final Integer priority = builder.priority;
        final Thread.UncaughtExceptionHandler handler = builder.uncaughtExceptionHandler;
        final AtomicLong count = (null == namePrefix) ? null : new AtomicLong();
        return r -> {
            final Thread thread = backingThreadFactory.newThread(r);
            if (null != namePrefix) {
                thread.setName(namePrefix + "_" + count.getAndIncrement());
            }
            if (null != daemon) {
                thread.setDaemon(daemon);
            }
            if (null != priority) {
                thread.setPriority(priority);
            }
            if (null != handler) {
                thread.setUncaughtExceptionHandler(handler);
            }
            return thread;
        };
    }
}