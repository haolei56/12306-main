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

package org.opengoofy.index12306.biz.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.opengoofy.index12306.biz.userservice.dto.req.PassengerRemoveReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.req.PassengerReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.PassengerActualRespDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.PassengerRespDTO;
import org.opengoofy.index12306.biz.userservice.service.PassengerService;
import org.opengoofy.index12306.framework.starter.convention.result.Result;
import org.opengoofy.index12306.framework.starter.idempotent.annotation.Idempotent;
import org.opengoofy.index12306.framework.starter.idempotent.enums.IdempotentSceneEnum;
import org.opengoofy.index12306.framework.starter.idempotent.enums.IdempotentTypeEnum;
import org.opengoofy.index12306.framework.starter.web.Results;
import org.opengoofy.index12306.frameworks.starter.user.core.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 乘车人控制层
 *
 *
 */
//在传统的 Web 应用中，控制器主要负责接收用户请求，调用业务逻辑，然后返回相应的视图。而在 REST 风格的应用中，控制器不再返回视图，而是直接返回数据，通常是 JSON 或 XML 格式的数据。
//这种方式更适合构建 Web API，也就是提供给其他系统或前端应用程序使用的接口。前后端分离，不用在写jsp了。REST 风格的应用更加专注于数据的传输和处理，而不涉及视图渲染的问题。
// REST 风格的控制器是面向数据的，返回的是数据而不是视图，而视图解析器在这种场景下通常不会使用。这种方式更适合构建 Web API 或前后端分离的应用。
// REST 风格的控制器使用一系列的 HTTP 请求方法（如 GET、POST、PUT、DELETE 等）来表示不同的操作，而不再依赖于特定的 URL 来表示不同的视图。它们通常使用注解来标识处理不同请求的方法，比如 Spring Framework 中的 @GetMapping、@PostMapping 等。
//视图解析器是传统 Web 应用中用于将控制器返回的数据和视图模板（如 JSP、Thymeleaf、FreeMarker 等）结合生成最终的 HTML 页面。
@RestController//这个注解表明这是一个 REST 风格的控制器类，它会将处理结果直接返回给客户端，而不是依赖于视图解析器。
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    /**
     * 根据用户名查询乘车人列表
     */
    @GetMapping("/api/user-service/passenger/query")//定义了一个处理 GET 请求的方法，对应的路径是 /api/user-service/passenger/query。@GetMapping 的方法应该只处理获取数据的逻辑，不应该产生副作用，即不应该改变服务器状态。
    public Result<List<PassengerRespDTO>> listPassengerQueryByUsername() {
        return Results.success(passengerService.listPassengerQueryByUsername(UserContext.getUsername()));
    }

    /**
     * 根据乘车人 ID 集合查询乘车人列表
     */
    @GetMapping("/api/user-service/inner/passenger/actual/query/ids")
    //@RequestParam("username") String username: 通过请求参数获取用户名。
    // @RequestParam("ids") List<Long> ids: 通过请求参数获取乘车人 ID 列表。
    public Result<List<PassengerActualRespDTO>> listPassengerQueryByIds(@RequestParam("username") String username, @RequestParam("ids") List<Long> ids) {
        return Results.success(passengerService.listPassengerQueryByIds(username, ids));
    }

    /**
     * 新增乘车人
     */
    //这是一个自定义注解，用于幂等验证。它指定了在调用这个方法时需要进行幂等验证，防止重复请求导致重复操作。
    @Idempotent(
            uniqueKeyPrefix = "index12306-user:lock_passenger-alter:",
            key = "T(org.opengoofy.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.RESTAPI,
            message = "正在新增乘车人，请稍后再试..."
    )
    @PostMapping("/api/user-service/passenger/save")//@PostMapping 的方法可以用于提交数据，可能会产生副作用，如创建、修改数据等。
    public Result<Void> savePassenger(@RequestBody PassengerReqDTO requestParam) {
        passengerService.savePassenger(requestParam);
        return Results.success();
    }

    /**
     * 修改乘车人
     */
    @Idempotent(
            uniqueKeyPrefix = "index12306-user:lock_passenger-alter:",
            key = "T(org.opengoofy.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.RESTAPI,
            message = "正在修改乘车人，请稍后再试..."
    )
    @PostMapping("/api/user-service/passenger/update")
    public Result<Void> updatePassenger(@RequestBody PassengerReqDTO requestParam) {
        passengerService.updatePassenger(requestParam);
        return Results.success();
    }

    /**
     * 移除乘车人
     * TODO 方法命名错误，remote -> remove
     */
    @Idempotent(
            uniqueKeyPrefix = "index12306-user:lock_passenger-alter:",
            key = "T(org.opengoofy.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.RESTAPI,
            message = "正在移除乘车人，请稍后再试..."
    )
    @PostMapping("/api/user-service/passenger/remote")
    public Result<Void> removePassenger(@RequestBody PassengerRemoveReqDTO requestParam) {
        passengerService.removePassenger(requestParam);
        return Results.success();
    }
}
