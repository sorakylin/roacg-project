package com.roacg.core.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Long 精度兼容的序列化器
 */
public class LongAccuracyCompatibleSerializer extends JsonSerializer<Long> {

    public static LongAccuracyCompatibleSerializer INSTANCE = new LongAccuracyCompatibleSerializer();
    // JavaScript 中能精准表示的最大整数 2^53: 9007199254740992
    public static final Long JS_MAX_NUMBER = Double.valueOf(Math.pow(2, 53)).longValue();

    private LongAccuracyCompatibleSerializer() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.compareTo(JS_MAX_NUMBER) == -1) gen.writeNumber(value);
        else gen.writeString(value.toString());
    }
}
