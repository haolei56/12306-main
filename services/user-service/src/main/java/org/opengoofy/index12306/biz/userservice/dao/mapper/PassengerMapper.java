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

package org.opengoofy.index12306.biz.userservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.opengoofy.index12306.biz.userservice.dao.entity.PassengerDO;

/**
 * 乘车人持久层
 *
 *
 */
//Mapper层起着连接应用程序和数据库之间的桥梁作用，它抽象了数据库操作，使得开发人员能够更方便地进行数据持久化和检索，同时遵循数据库的事务和数据一致性要求。
//BaseMapper 是 MyBatis Plus 提供的接口，它提供了一些通用的数据库操作方法，如插入、更新、删除、查询等。
public interface PassengerMapper extends BaseMapper<PassengerDO> {
}
