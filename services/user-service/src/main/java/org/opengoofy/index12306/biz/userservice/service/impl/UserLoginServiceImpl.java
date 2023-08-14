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

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opengoofy.index12306.biz.userservice.common.enums.UserChainMarkEnum;
import org.opengoofy.index12306.biz.userservice.dao.entity.UserDO;
import org.opengoofy.index12306.biz.userservice.dao.entity.UserDeletionDO;
import org.opengoofy.index12306.biz.userservice.dao.entity.UserMailDO;
import org.opengoofy.index12306.biz.userservice.dao.entity.UserPhoneDO;
import org.opengoofy.index12306.biz.userservice.dao.entity.UserReuseDO;
import org.opengoofy.index12306.biz.userservice.dao.mapper.UserDeletionMapper;
import org.opengoofy.index12306.biz.userservice.dao.mapper.UserMailMapper;
import org.opengoofy.index12306.biz.userservice.dao.mapper.UserMapper;
import org.opengoofy.index12306.biz.userservice.dao.mapper.UserPhoneMapper;
import org.opengoofy.index12306.biz.userservice.dao.mapper.UserReuseMapper;
import org.opengoofy.index12306.biz.userservice.dto.req.UserDeletionReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.req.UserLoginReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.req.UserRegisterReqDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.UserLoginRespDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.UserQueryRespDTO;
import org.opengoofy.index12306.biz.userservice.dto.resp.UserRegisterRespDTO;
import org.opengoofy.index12306.biz.userservice.service.UserLoginService;
import org.opengoofy.index12306.biz.userservice.service.UserService;
import org.opengoofy.index12306.framework.starter.cache.DistributedCache;
import org.opengoofy.index12306.framework.starter.common.toolkit.BeanUtil;
import org.opengoofy.index12306.framework.starter.convention.exception.ClientException;
import org.opengoofy.index12306.framework.starter.convention.exception.ServiceException;
import org.opengoofy.index12306.framework.starter.designpattern.chain.AbstractChainContext;
import org.opengoofy.index12306.frameworks.starter.user.core.UserContext;
import org.opengoofy.index12306.frameworks.starter.user.core.UserInfoDTO;
import org.opengoofy.index12306.frameworks.starter.user.toolkit.JWTUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.opengoofy.index12306.biz.userservice.common.constant.RedisKeyConstant.USER_DELETION;
import static org.opengoofy.index12306.biz.userservice.common.constant.RedisKeyConstant.USER_REGISTER_REUSE_SHARDING;
import static org.opengoofy.index12306.biz.userservice.common.enums.UserRegisterErrorCodeEnum.HAS_USERNAME_NOTNULL;
import static org.opengoofy.index12306.biz.userservice.common.enums.UserRegisterErrorCodeEnum.MAIL_REGISTERED;
import static org.opengoofy.index12306.biz.userservice.common.enums.UserRegisterErrorCodeEnum.PHONE_REGISTERED;
import static org.opengoofy.index12306.biz.userservice.common.enums.UserRegisterErrorCodeEnum.USER_REGISTER_FAIL;
import static org.opengoofy.index12306.biz.userservice.toolkit.UserReuseUtil.hashShardingIdx;

/**
 * 用户登录接口实现
 *
 *
 */
//这段代码实现了用户登录、注册、注销等功能，同时涉及了数据库操作、缓存管理、异常处理等方面的处理。
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserReuseMapper userReuseMapper;
    private final UserDeletionMapper userDeletionMapper;
    private final UserPhoneMapper userPhoneMapper;
    private final UserMailMapper userMailMapper;
    private final RedissonClient redissonClient;
    private final DistributedCache distributedCache;
    private final AbstractChainContext<UserRegisterReqDTO> abstractChainContext;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        String usernameOrMailOrPhone = requestParam.getUsernameOrMailOrPhone();//从传入的 requestParam 中获取用户名、邮箱或手机号。
        boolean mailFlag = false;//初始化一个布尔变量 mailFlag，用于标识是否为邮箱格式。
        // 时间复杂度最佳 O(1)。indexOf or contains 时间复杂度为 O(n)
        for (char c : usernameOrMailOrPhone.toCharArray()) {
            if (c == '@') {
                mailFlag = true;
                break;
            }
        }
        String username;//初始化一个变量 username，用于存储最终确定的用户名。
        if (mailFlag) {//如果 mailFlag 为 true，说明输入是邮箱格式，就通过查询邮箱表，根据邮箱地址找到对应的用户名。如果找到邮箱对应的用户名，则将 username 设置为该用户名。如果未找到对应的用户名，抛出客户端异常，提示 "用户名/手机号/邮箱不存在"。
            LambdaQueryWrapper<UserMailDO> queryWrapper = Wrappers.lambdaQuery(UserMailDO.class)
                    .eq(UserMailDO::getMail, usernameOrMailOrPhone);
            username = Optional.ofNullable(userMailMapper.selectOne(queryWrapper))
                    .map(UserMailDO::getUsername)
                    .orElseThrow(() -> new ClientException("用户名/手机号/邮箱不存在"));
        } else {//如果 mailFlag 为 false，说明输入不是邮箱格式，就通过查询手机号表，根据手机号找到对应的用户名。
            LambdaQueryWrapper<UserPhoneDO> queryWrapper = Wrappers.lambdaQuery(UserPhoneDO.class)
                    .eq(UserPhoneDO::getPhone, usernameOrMailOrPhone);
            username = Optional.ofNullable(userPhoneMapper.selectOne(queryWrapper))
                    .map(UserPhoneDO::getUsername)
                    .orElse(null);
        }
        username = Optional.ofNullable(username).orElse(requestParam.getUsernameOrMailOrPhone());//如果在以上查询中未找到对应用户名，将 username 设置为传入的原始用户名。
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)//创建 queryWrapper，用于构建查询条件，查询用户表中是否存在与输入的用户名和密码匹配的记录。
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getPassword, requestParam.getPassword())
                .select(UserDO::getId, UserDO::getUsername, UserDO::getRealName);//查询用户表中满足用户名和密码条件的记录，选择返回的字段包括 id、username 和 realName。
        UserDO userDO = userMapper.selectOne(queryWrapper);//如果找到匹配的用户记录，创建一个 UserInfoDTO 对象，将用户信息转换为 UserInfoDTO，并使用 JWTUtil 生成访问令牌。
        if (userDO != null) {
            UserInfoDTO userInfo = UserInfoDTO.builder()
                    .userId(String.valueOf(userDO.getId()))
                    .username(userDO.getUsername())
                    .realName(userDO.getRealName())
                    .build();
            String accessToken = JWTUtil.generateAccessToken(userInfo);
            //使用用户信息和访问令牌创建一个 UserLoginRespDTO 对象，并将该对象序列化为 JSON 格式，存储到分布式缓存中，设置有效期为 30 分钟，然后返回登录成功的响应。
            UserLoginRespDTO actual = new UserLoginRespDTO(userInfo.getUserId(), requestParam.getUsernameOrMailOrPhone(), userDO.getRealName(), accessToken);
            distributedCache.put(accessToken, JSON.toJSONString(actual), 30, TimeUnit.MINUTES);
            return actual;
        }
        throw new ServiceException("账号不存在或密码错误");
    }

    //该方法用于检查用户的登录状态。通过传入的访问令牌 accessToken 从分布式缓存中获取登录响应对象 UserLoginRespDTO。
    @Override
    public UserLoginRespDTO checkLogin(String accessToken) {
        return distributedCache.get(accessToken, UserLoginRespDTO.class);
    }

    //该方法用于实现用户的注销操作。如果传入的访问令牌 accessToken 不为空，则从分布式缓存中删除该令牌，即使用户被注销或退出登录。
    @Override
    public void logout(String accessToken) {
        if (StrUtil.isNotBlank(accessToken)) {
            distributedCache.delete(accessToken);
        }
    }

    //该方法用于检查指定的用户名是否存在。首先，它利用布隆过滤器 userRegisterCachePenetrationBloomFilter 判断用户名是否可能存在，如果可能存在，则进一步通过分布式缓存的操作验证用户名是否真实存在。
    @Override
    public Boolean hasUsername(String username) {
        boolean hasUsername = userRegisterCachePenetrationBloomFilter.contains(username);
        if (hasUsername) {
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();
            return instance.opsForSet().isMember(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        }
        return true;//如果布隆过滤器认为用户名不存在，直接返回 true，表示用户名不存在。
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserRegisterRespDTO register(UserRegisterReqDTO requestParam) {
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);//通过抽象的链式上下文处理对象 abstractChainContext，调用过滤器链中的一个特定的处理器，以执行用户注册过滤器逻辑。
        try {
            int inserted = userMapper.insert(BeanUtil.convert(requestParam, UserDO.class));//尝试在数据库中插入一个新的用户记录。首先将传入的 requestParam 转换为 UserDO 对象，然后调用 userMapper.insert 进行插入操作。如果插入不成功（返回值小于 1），则抛出服务异常，提示注册失败。
            if (inserted < 1) {
                throw new ServiceException(USER_REGISTER_FAIL);
            }
        } catch (DuplicateKeyException dke) {//如果捕获到 DuplicateKeyException 异常，说明用户名已经存在，记录日志并抛出服务异常，提示用户名重复。
            log.error("用户名 [{}] 重复注册", requestParam.getUsername());
            throw new ServiceException(HAS_USERNAME_NOTNULL);
        }
        UserPhoneDO userPhoneDO = UserPhoneDO.builder()//创建一个 UserPhoneDO 对象，用于保存用户的手机号和用户名，然后将其插入到用户手机号表中。如果插入时出现手机号重复的异常，记录日志并抛出服务异常，提示手机号已被注册。
                .phone(requestParam.getPhone())
                .username(requestParam.getUsername())
                .build();
        try {
            userPhoneMapper.insert(userPhoneDO);
        } catch (DuplicateKeyException dke) {
            log.error("用户 [{}] 注册手机号 [{}] 重复", requestParam.getUsername(), requestParam.getPhone());
            throw new ServiceException(PHONE_REGISTERED);
        }
        if (StrUtil.isNotBlank(requestParam.getMail())) {
            UserMailDO userMailDO = UserMailDO.builder()
                    .mail(requestParam.getMail())
                    .username(requestParam.getUsername())
                    .build();
            try {
                userMailMapper.insert(userMailDO);
            } catch (DuplicateKeyException dke) {
                log.error("用户 [{}] 注册邮箱 [{}] 重复", requestParam.getUsername(), requestParam.getMail());
                throw new ServiceException(MAIL_REGISTERED);
            }
        }
        String username = requestParam.getUsername();//获取请求中的用户名。
        userReuseMapper.delete(Wrappers.update(new UserReuseDO(username)));//删除用户重用表中的记录，以便表示用户已经注册过。
        //获取分布式缓存的实例，并通过实例的 opsForSet() 方法操作集合。移除用户注册重用缓存的 Bloom 过滤器中的记录，确保用户在注册后不再被判定为已注册。
        StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();//
        instance.opsForSet().remove(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        userRegisterCachePenetrationBloomFilter.add(username);//将新注册的用户名添加到用户注册重用缓存的 Bloom 过滤器中，以备后续判断用户是否已注册。
        return BeanUtil.convert(requestParam, UserRegisterRespDTO.class);//最后，将输入的 requestParam 转换为 UserRegisterRespDTO 对象并返回。
    }

    //这段代码实现了用户账号注销的逻辑。它首先进行账号一致性校验，然后对用户相关信息进行逻辑删除，释放资源，并将账号放入待重用列表。
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletion(UserDeletionReqDTO requestParam) {
        String username = UserContext.getUsername();// 获取当前登录用户的用户名。
        if (!Objects.equals(username, requestParam.getUsername())) {//检查当前登录用户的用户名是否与要注销的用户名一致，如果不一致，抛出客户端异常，提示 "注销账号与登录账号不一致"。
            // 此处严谨来说，需要上报风控中心进行异常检测
            throw new ClientException("注销账号与登录账号不一致");
        }
        RLock lock = redissonClient.getLock(USER_DELETION + requestParam.getUsername());//通过 redissonClient 获取一个分布式锁，锁的名称为 USER_DELETION + requestParam.getUsername()，保证同一账号注销操作的互斥性。
        // 加锁为什么放在 try 语句外？https://www.yuque.com/magestack/12306/pu52u29i6eb1c5wh
        lock.lock();
        try {
            UserQueryRespDTO userQueryRespDTO = userService.queryUserByUsername(username);//查询当前登录用户的详细信息。
            UserDeletionDO userDeletionDO = UserDeletionDO.builder()
                    .idType(userQueryRespDTO.getIdType())
                    .idCard(userQueryRespDTO.getIdCard())
                    .build();
            userDeletionMapper.insert(userDeletionDO);//创建一个 UserDeletionDO 对象，设置身份证类型和身份证号，将该对象插入到注销用户表中。
            UserDO userDO = new UserDO();//创建一个 UserDO 对象，设置注销时间为当前时间，将该对象传递给 MyBatis Plus 的 userMapper.deletionUser() 方法，进行逻辑删除操作（通常是修改 del_flag 字段的状态）。
            userDO.setDeletionTime(System.currentTimeMillis());
            userDO.setUsername(username);
            // MyBatis Plus 不支持修改语句变更 del_flag 字段
            userMapper.deletionUser(userDO);
            UserPhoneDO userPhoneDO = UserPhoneDO.builder()//创建一个 UserPhoneDO 对象，设置手机号和注销时间，将该对象传递给 userPhoneMapper.deletionUser() 方法，进行逻辑删除操作。
                    .phone(userQueryRespDTO.getPhone())
                    .deletionTime(System.currentTimeMillis())
                    .build();
            userPhoneMapper.deletionUser(userPhoneDO);
            if (StrUtil.isNotBlank(userQueryRespDTO.getMail())) {//如果用户存在邮箱信息，创建一个 UserMailDO 对象，设置邮箱和注销时间，将该对象传递给 userMailMapper.deletionUser() 方法，进行逻辑删除操作。
                UserMailDO userMailDO = UserMailDO.builder()
                        .mail(userQueryRespDTO.getMail())
                        .deletionTime(System.currentTimeMillis())
                        .build();
                userMailMapper.deletionUser(userMailDO);
            }
            distributedCache.delete(UserContext.getToken());//从分布式缓存中删除当前用户的登录凭证（令牌）。
            userReuseMapper.insert(new UserReuseDO(username));//向用户重用表中插入一条记录，标记该用户名为可重用状态。
            StringRedisTemplate instance = (StringRedisTemplate) distributedCache.getInstance();//获取分布式缓存的实例，向 "USER_REGISTER_REUSE_SHARDING" 集合中添加一个元素，标记该用户名为可重用状态。
            instance.opsForSet().add(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        } finally {
            lock.unlock();
        }
    }
}
