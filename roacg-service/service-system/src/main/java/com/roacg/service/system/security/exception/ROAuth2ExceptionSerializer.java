package com.roacg.service.system.security.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @see com.roacg.core.model.exception.RoApiException
 * @see com.roacg.core.model.resource.RoApiResponse
 */
public class ROAuth2ExceptionSerializer extends StdSerializer<ROAuth2Exception> {

    public ROAuth2ExceptionSerializer() {
        super(ROAuth2Exception.class);
    }

    @Override
    public void serialize(ROAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("success", false);
        gen.writeObjectField("code", value.getCode());
        gen.writeStringField("msg", value.getOAuth2ErrorCode());
        gen.writeStringField("data", value.getMessage());
        gen.writeEndObject();
    }
}
