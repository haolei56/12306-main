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

import org.springframework.core.Ordered;

/**
 * 抽象业务责任链组件
 *
 *
 */
//此接口的目的是为了定义一个通用的责任链组件规范，任何实现了这个接口的类都必须提供责任链的处理逻辑以及标识，以便构建更加灵活和可扩展的责任链模式。接口还继承了 Ordered 接口，表示责任链组件可以通过指定优先级来控制处理顺序。
//责任链模式是一种行为设计模式，它将请求从一个处理器传递到另一个，形成一个处理链。每个处理器可以选择处理请求或将其传递给链中的下一个处理器。
public interface AbstractChainHandler<T> extends Ordered {

    /**
     * 执行责任链逻辑
     *
     * @param requestParam 责任链执行入参
     */
    void handler(T requestParam);//这个方法用于执行责任链逻辑，接受一个类型为 T 的参数 requestParam，表示责任链的执行入参。每个实现类需要在这个方法中定义具体的业务逻辑处理。

    /**
     * @return 责任链组件标识
     */
    String mark();// 这个方法返回责任链组件的标识，用于标识不同的责任链组件。通常在责任链中，不同的组件会根据标识来决定是否处理请求或将其传递给下一个组件。
}
