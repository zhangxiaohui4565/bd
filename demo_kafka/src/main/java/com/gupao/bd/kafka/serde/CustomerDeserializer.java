package com.gupao.bd.kafka.serde;


import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * 从字节数组中恢复出Customer对象
 * 字节数组的构成：
 * 第一部分：4字节整数代表id
 * 第二部分：4字节整数代表name的长度
 * 第三部分：N个字节的数组代表name的内容
 */
public class CustomerDeserializer implements Deserializer<Customer> {


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Customer deserialize(String topic, byte[] data) {
        int id;
        int nameSize;
        String name;

        try {
            if (data == null) {
                return null;
            }

            if (data.length < 8) {
                throw new SerializationException("Size of data recedived is shorter than expected");
            }

            ByteBuffer buffer = ByteBuffer.wrap(data);
            //第一部分：id固定占用4字节
            id = buffer.getInt();

            //第二部分：name的长度
            nameSize = buffer.getInt();

            //第三部分：根据name的长度取值
            byte[] nameBytes = new byte[nameSize];
            buffer.get(nameBytes);
            name = new String(nameBytes, "UTF-8");

            //构造Customer对象
            return new Customer(id, name);

        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Customer " + e);
        }

    }

    @Override
    public void close() {

    }
}
