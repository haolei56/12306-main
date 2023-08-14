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

package org.opengoofy.index12306.framework.starter.designpattern.chain;

import org.opengoofy.index12306.framework.starter.bases.ApplicationContextHolder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 抽象责任链上下文
 *
 *
 */
//这段代码定义了一个名为 AbstractChainContext 的类，用于管理和执行责任链模式中的责任链组件。它实现了 CommandLineRunner 接口，这意味着在 Spring Boot 应用程序启动时，会执行 run 方法。
// 这个类的主要作用是管理责任链组件并执行责任链模式的逻辑。它通过在 Spring Boot 应用程序启动时获取所有的责任链组件，并将它们按照优先级排序，以确保责任链的顺序执行。
public final class AbstractChainContext<T> implements CommandLineRunner {

    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();//这是一个成员变量，它是一个映射，用于存储责任链组件。每个键表示责任链组件的标识，对应的值是一个责任链组件列表。

    /**
     * 责任链组件执行
     *
     * @param mark         责任链组件标识
     * @param requestParam 请求参数
     */
    //这个方法用于执行责任链模式中的责任链组件。它接受两个参数，mark 表示责任链组件的标识，requestParam 是请求参数。根据标识从映射中获取对应的责任链组件列表，然后依次调用每个组件的 handler 方法来处理请求。
    public void handler(String mark, T requestParam) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        abstractChainHandlers.forEach(each -> each.handler(requestParam));
    }

    //这是实现了 CommandLineRunner 接口的方法，会在应用程序启动时执行。在这个方法中，首先使用 ApplicationContextHolder 获取所有类型为 AbstractChainHandler 的 bean。
    // 然后将这些责任链组件添加到 abstractChainHandlerContainer 中，根据它们的标识进行分类和排序，以便在后续的责任链执行中按照优先级依次调用。
    @Override
    public void run(String... args) throws Exception {
        Map<String, AbstractChainHandler> chainFilterMap = ApplicationContextHolder
                .getBeansOfType(AbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(bean.mark());
            if (CollectionUtils.isEmpty(abstractChainHandlers)) {
                abstractChainHandlers = new ArrayList();
            }
            abstractChainHandlers.add(bean);
            List<AbstractChainHandler> actualAbstractChainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            abstractChainHandlerContainer.put(bean.mark(), actualAbstractChainHandlers);
        });
    }
}
