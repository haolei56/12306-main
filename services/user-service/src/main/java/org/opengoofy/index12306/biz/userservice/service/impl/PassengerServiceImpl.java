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

package org.opengoofy.index12306.biz.userservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opengoofy.index12306.biz.userservice.common.enums.VerifyStatusEnum;
import org.opengoofy.index12306.biz.userservice.dao.entity.PassengerDO;
import org.opengoofy.index12306.biz.userservice.dao.mapper.PassengerMapper;
import org.opengoofy.index12306.biz.userservice.dto.req.PassengerRemoveReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.req.PassengerReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.PassengerActualRespDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.PassengerRespDTO;
import org.opengoofy.index12306.biz.userservice.service.PassengerService;
import org.opengoofy.index12306.framework.starter.cache.DistributedCache;
import org.opengoofy.index12306.framework.starter.common.toolkit.BeanUtil;
import org.opengoofy.index12306.framework.starter.convention.exception.ClientException;
import org.opengoofy.index12306.framework.starter.convention.exception.ServiceException;
import org.opengoofy.index12306.frameworks.starter.user.core.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.opengoofy.index12306.biz.userservice.common.constant.RedisKeyConstant.USER_PASSENGER_LIST;

/**
 * 乘车人接口实现层
 *
 *
 */
@Slf4j
@Service//这是Spring Framework提供的注解，用于标识这个类是一个服务（业务逻辑）组件，会被Spring容器管理和注入。
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerMapper passengerMapper;// 这是一个持久化操作的Mapper接口，用于与数据库交互，执行数据的插入、更新、查询等操作。
    private final PlatformTransactionManager transactionManager;//用于管理数据库事务的接口。
    private final DistributedCache distributedCache;//分布式缓存对象，用于存储和获取数据。

    //根据用户名查询乘车人列表。这个方法通过在分布式缓存中查找用户的乘车人列表，如果找到了则将其转换为指定的数据传输对象（DTO），否则返回空。
    @Override
    public List<PassengerRespDTO> listPassengerQueryByUsername(String username) {
        String actualUserPassengerListStr = getActualUserPassengerListStr(username);
        return Optional.ofNullable(actualUserPassengerListStr)
                .map(each -> JSON.parseArray(each, PassengerDO.class))
                .map(each -> BeanUtil.convert(each, PassengerRespDTO.class))
                .orElse(null);
    }

    //从分布式缓存中获取实际的用户乘车人列表字符串，如果缓存中没有则从数据库查询，并将查询结果缓存起来。
    private String getActualUserPassengerListStr(String username) {
        return  distributedCache.safeGet(
                USER_PASSENGER_LIST + username,
                String.class,
                () -> {
                    LambdaQueryWrapper<PassengerDO> queryWrapper = Wrappers.lambdaQuery(PassengerDO.class)
                            .eq(PassengerDO::getUsername, username);
                    List<PassengerDO> passengerDOList = passengerMapper.selectList(queryWrapper);
                    return CollUtil.isNotEmpty(passengerDOList) ? JSON.toJSONString(passengerDOList) : null;
                },
                1,
                TimeUnit.DAYS
        );
    }

    //根据用户名和指定的乘车人ID列表查询乘车人列表。方法会先从缓存中获取用户乘车人列表，然后根据传入的ID列表过滤出符合条件的乘车人，并将其转换为指定的数据传输对象。
    @Override
    public List<PassengerActualRespDTO> listPassengerQueryByIds(String username, List<Long> ids) {
        String actualUserPassengerListStr = getActualUserPassengerListStr(username);
        if (StrUtil.isEmpty(actualUserPassengerListStr)) {
            return null;
        }
        return JSON.parseArray(actualUserPassengerListStr, PassengerDO.class)
                .stream().filter(passengerDO -> ids.contains(passengerDO.getId()))
                .map(each -> BeanUtil.convert(each, PassengerActualRespDTO.class))
                .collect(Collectors.toList());
    }

    //保存乘车人信息的方法。该方法会在数据库中插入新的乘车人记录，并在事务中进行管理。
    @Override
    public void savePassenger(PassengerReqDTO requestParam) {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();//创建一个事务定义对象，表示一个新的事务。
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);//根据事务定义对象，从事务管理器（transactionManager）获取事务状态对象（transactionStatus）。这将启动一个新的数据库事务。
        String username = UserContext.getUsername();//从上下文中获取当前用户的用户名。
        try {//进入 try 块，这里包含了一个事务操作的代码块。
            PassengerDO passengerDO = BeanUtil.convert(requestParam, PassengerDO.class);// 将传入的 requestParam 对象转换为 PassengerDO 类型的对象。
            passengerDO.setUsername(username);//设置乘车人对象的用户名为当前用户的用户名。
            passengerDO.setCreateDate(new Date());//设置乘车人对象的创建日期为当前日期
            passengerDO.setVerifyStatus(VerifyStatusEnum.REVIEWED.getCode());// 设置乘车人对象的审核状态为已审核。
            int inserted = passengerMapper.insert(passengerDO);//调用 passengerMapper 对象的 insert 方法，将乘车人对象插入数据库。inserted 表示插入的行数。
            if (!SqlHelper.retBool(inserted)) {//如果插入的行数为 0（表示插入失败）。
                throw new ServiceException(String.format("[%s] 新增乘车人失败", username));
            }
            transactionManager.commit(transactionStatus);//提交事务，将之前开启的数据库事务进行提交，将乘车人信息保存到数据库。
        } catch (Exception ex) {
            if (ex instanceof ServiceException) {
                log.error("{}，请求参数：{}", ex.getMessage(), JSON.toJSONString(requestParam));
            } else {
                log.error("[{}] 新增乘车人失败，请求参数：{}", username, JSON.toJSONString(requestParam), ex);
            }
            transactionManager.rollback(transactionStatus);//回滚事务，取消之前开启的数据库事务。
            throw ex;
        }
        delUserPassengerCache(username);//删除分布式缓存中的用户乘车人列表。
    }

    //更新乘车人信息的方法。该方法会在数据库中根据乘车人ID和用户名更新乘车人记录，并在事务中进行管理。
    @Override
    public void updatePassenger(PassengerReqDTO requestParam) {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        String username = UserContext.getUsername();
        try {
            PassengerDO passengerDO = BeanUtil.convert(requestParam, PassengerDO.class);
            passengerDO.setUsername(username);
            //这段代码使用了 MyBatis-Plus 的 LambdaUpdateWrapper 类来构建一个更新操作的条件对象，该对象用于更新数据库记录的条件。
            LambdaUpdateWrapper<PassengerDO> updateWrapper = Wrappers.lambdaUpdate(PassengerDO.class)//这段代码的目的是创建一个条件对象，该对象用于更新数据库表中用户名与指定值相等且主键与指定值相等的记录。这个条件将被用于数据库的更新操作。
                    .eq(PassengerDO::getUsername, username)
                    .eq(PassengerDO::getId, requestParam.getId());
            int updated = passengerMapper.update(passengerDO, updateWrapper);
            if (!SqlHelper.retBool(updated)) {
                throw new ServiceException(String.format("[%s] 修改乘车人失败", username));
            }
            transactionManager.commit(transactionStatus);
        } catch (Exception ex) {
            if (ex instanceof ServiceException) {
                log.error("{}，请求参数：{}", ex.getMessage(), JSON.toJSONString(requestParam));
            } else {
                log.error("[{}] 修改乘车人失败，请求参数：{}", username, JSON.toJSONString(requestParam), ex);
            }
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
        delUserPassengerCache(username);
    }

    @Override
    public void removePassenger(PassengerRemoveReqDTO requestParam) {
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        String username = UserContext.getUsername();
        PassengerDO passengerDO = selectPassenger(username, requestParam.getId());
        if (Objects.isNull(passengerDO)) {
            throw new ClientException("乘车人数据不存在");
        }
        try {
            LambdaUpdateWrapper<PassengerDO> deleteWrapper = Wrappers.lambdaUpdate(PassengerDO.class)
                    .eq(PassengerDO::getUsername, username)
                    .eq(PassengerDO::getId, requestParam.getId());
            // 逻辑删除，修改数据库表记录 del_flag
            int deleted = passengerMapper.delete(deleteWrapper);
            if (!SqlHelper.retBool(deleted)) {
                throw new ServiceException(String.format("[%s] 删除乘车人失败", username));
            }
            transactionManager.commit(transactionStatus);
        } catch (Exception ex) {
            if (ex instanceof ServiceException) {
                log.error("{}，请求参数：{}", ex.getMessage(), JSON.toJSONString(requestParam));
            } else {
                log.error("[{}] 删除乘车人失败，请求参数：{}", username, JSON.toJSONString(requestParam), ex);
            }
            transactionManager.rollback(transactionStatus);
            throw ex;
        }
        delUserPassengerCache(username);
    }

    private PassengerDO selectPassenger(String username, String passengerId) {
        LambdaQueryWrapper<PassengerDO> queryWrapper = Wrappers.lambdaQuery(PassengerDO.class)
                .eq(PassengerDO::getUsername, username)
                .eq(PassengerDO::getId, passengerId);
        return passengerMapper.selectOne(queryWrapper);
    }

    //: 删除分布式缓存中的用户乘车人列表。
    private void delUserPassengerCache(String username) {
        distributedCache.delete(USER_PASSENGER_LIST + username);
    }
}
