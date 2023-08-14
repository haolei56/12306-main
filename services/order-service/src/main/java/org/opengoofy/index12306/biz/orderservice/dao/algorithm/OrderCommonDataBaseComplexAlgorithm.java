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

package org.opengoofy.index12306.biz.orderservice.dao.algorithm;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

/**
 * 订单数据库复合分片算法配置
 *
 *
 */
//这段代码实现了一个复杂的分库分表算法，用于在分布式数据库环境中决定将数据存储在哪个数据库实例（分库）和哪个表（分表）中
public class OrderCommonDataBaseComplexAlgorithm implements ComplexKeysShardingAlgorithm {//实现了 ComplexKeysShardingAlgorithm 接口，这是 MyBatis Plus 提供的用于定制分库分表策略的接口。

    @Getter
    private Properties props;//用于存储算法的配置信息。

    private int shardingCount;//表示分片的数量。

    private static final String SHARDING_COUNT_KEY = "sharding-count";//配置属性的键，用于指定分片数量。

    /**
     *
     * @param availableTargetNames 分片后的目标数据源名称集合。
     * @param shardingValue 分片条件对象，包含了分片所需的列名和对应的分片值。
     * @return
     */
    //这段代码根据传入的分片键和分片值，通过哈希运算计算出数据应该存储在哪些分片中，然后将分片结果添加到一个集合中返回。这样就实现了根据分片条件进行数据分片存储的功能。
    //如果分片键是用户ID，而分片值是某个具体的用户ID，那么根据这个用户ID的分片值就可以确定数据属于哪个分片。
    @Override
    public Collection<String> doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {//根据分片条件进行实际的分片操作。
        Map<String, Collection<Comparable<Long>>> columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();//这一行获取了传入的分片键和对应的分片值的映射关系。
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());//创建了一个用于存储分片结果的集合。
        if (CollUtil.isNotEmpty(columnNameAndShardingValuesMap)) {//判断分片键和值的映射是否为空。
            String userId = "user_id";//设置了用于分片的键名。
            Collection<Comparable<Long>> customerUserIdCollection = columnNameAndShardingValuesMap.get(userId);//获取与指定分片键对应的分片值集合。
            if (CollUtil.isNotEmpty(customerUserIdCollection)) {//判断分片值集合是否为空。
                Comparable<?> comparable = customerUserIdCollection.stream().findFirst().get();//从分片值集合中取出第一个分片值。
                if (comparable instanceof String) {//判断分片值的类型是否为字符串。
                    String actualOrderSn = comparable.toString();
                    //这部分代码对字符串类型的分片值 actualOrderSn 进行哈希运算，确保得到一个均匀分布的哈希值。Math.max(actualOrderSn.length() - 6, 0) 用于截取分片值的后6个字符，以保证哈希的输入不会太长。哈希运算的目的是将分片值映射到一个整数值。
                    // % shardingCount：对哈希运算的结果进行取模操作，将哈希值映射到一个范围内，确保分片结果在合理的分片数量范围内。
                    // result.add("ds_" + ...)：将分片结果添加到结果集合中，这里使用 "ds_" 前缀表示数据源的名称，然后加上计算得到的分片结果。
                    result.add("ds_" + hashShardingValue(actualOrderSn.substring(Math.max(actualOrderSn.length() - 6, 0))) % shardingCount);//对分片值进行哈希运算，并将分片结果添加到结果集合中。
                } else {//如果分片值不是字符串类型
                    //对不是字符串类型的分片值 comparable 进行哈希运算，然后进行类似的取模操作，根据特定规则计算出分片结果。
                    // 在这里，comparable 是一个可能是长整型的值，首先对其进行取模操作，将其映射到 0 到 999999 的范围，然后再进行哈希运算并取模，得到分片结果。
                    String dbSuffix = String.valueOf(hashShardingValue((Long) comparable % 1000000) % shardingCount);//对分片值进行哈希运算，然后根据指定规则计算出分片结果。
                    result.add("ds_" + dbSuffix);
                }
            } else {
                String orderSn = "order_sn";
                Collection<Comparable<Long>> orderSnCollection = columnNameAndShardingValuesMap.get(orderSn);
                Comparable<?> comparable = orderSnCollection.stream().findFirst().get();
                if (comparable instanceof String) {
                    String actualOrderSn = comparable.toString();
                    result.add("ds_" + hashShardingValue(actualOrderSn.substring(Math.max(actualOrderSn.length() - 6, 0))) % shardingCount);
                } else {
                    result.add("ds_" + hashShardingValue((Long) comparable % 1000000) % shardingCount);
                }
            }
        }
        return result;
    }

    @Override
    public void init(Properties props) {//初始化方法，用于读取配置信息并初始化算法。
        this.props = props;
        shardingCount = getShardingCount(props);
    }

    private int getShardingCount(final Properties props) {//从配置属性中获取分片数量。
        Preconditions.checkArgument(props.containsKey(SHARDING_COUNT_KEY), "Sharding count cannot be null.");
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    private long hashShardingValue(final Comparable<?> shardingValue) {//根据分片值计算一个散列值，以便在分片时进行选择。
        return Math.abs((long) shardingValue.hashCode());
    }
}
