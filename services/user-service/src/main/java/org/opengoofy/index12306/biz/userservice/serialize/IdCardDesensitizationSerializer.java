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

package org.opengoofy.index12306.biz.userservice.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 身份证号脱敏反序列化
 *
 *
 */
//脱敏处理会将敏感信息中的部分字符替换为其他字符，从而隐藏敏感信息的真实值，使得即使数据被泄露或不当处理，也不会直接暴露用户的敏感信息。只显示身份证号的前几位和后几位，例如：340**********34。
public class IdCardDesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String idCard, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        //这行代码调用了名为 DesensitizedUtil 的工具类的 idCardNum 方法，将原始身份证号进行脱敏处理。该方法接收三个参数：
        // idCard: 原始身份证号。
        // 4: 脱敏处理后保留的前四位字符。
        // 4: 脱敏处理后保留的后四位字符。
        String phoneDesensitization = DesensitizedUtil.idCardNum(idCard, 4, 4);
        jsonGenerator.writeString(phoneDesensitization);//这行代码使用 JSON 生成器将经过脱敏处理后的身份证号写入 JSON 数据中，以字符串形式。
    }
}
