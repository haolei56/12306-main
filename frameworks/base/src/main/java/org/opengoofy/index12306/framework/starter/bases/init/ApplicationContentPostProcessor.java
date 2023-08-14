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

package org.opengoofy.index12306.framework.starter.bases.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

/**
 * 应用初始化后置处理器，防止Spring事件被多次执行
 *
 *
 */
//
@RequiredArgsConstructor//这是 Lombok 注解，用于自动生成带有标识为 final 的成员变量的构造函数。
public class ApplicationContentPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationContext applicationContext;

    /**
     * 执行标识，确保Spring事件 {@link ApplicationReadyEvent} 有且执行一次
     */
    private boolean executeOnlyOnce = true;//这是一个布尔型成员变量，用于标识执行状态。初始状态是 true，表示可以执行操作。

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        synchronized (ApplicationContentPostProcessor.class) {//这是一个同步块，用于确保在多线程环境中只有一个线程可以执行其中的代码。
            //首先检查 executeOnlyOnce 变量的值。
            //如果它为 true，表示该代码块尚未执行过，进入条件分支。
            //在条件分支内部，通过应用程序上下文发布一个自定义的 ApplicationInitializingEvent 事件，并将当前类的实例作为事件源。
            //将 executeOnlyOnce 设置为 false，以确保代码块只执行一次。
            if (executeOnlyOnce) {
                applicationContext.publishEvent(new ApplicationInitializingEvent(this));
                executeOnlyOnce = false;
            }
        }
    }
}
