package com.gupao.bd.kafka.serde;


import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Customer对象被序列化成字节数组
 * 字节数组的构成：
 * 第一部分：4字节整数代表id
 * 第二部分：4字节整数代表name的长度
 * 第三部分：N个字节的数组代表name的内容
 */
public class CustomerSerializer implements Serializer<Customer> {


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }


    @Override
    public byte[] serialize(String topic, Customer data) {
        try {
            byte[] serializedName;
            int stringSize;
            if (data == null) {
                return null;
            } else {
                if (data.getName() != null) {
                    serializedName = data.getName().getBytes("UTF-8");
                    stringSize = serializedName.length;
                } else {
                    serializedName = new byte[0];
                    stringSize = 0;
                }
            }

            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + stringSize);
            buffer.putInt(data.getId());
            buffer.putInt(stringSize);
            buffer.put(serializedName);

            return buffer.array();
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Customer to byte[] " + e);
        }
    }

    @Override
    public void close() {

    }
}
