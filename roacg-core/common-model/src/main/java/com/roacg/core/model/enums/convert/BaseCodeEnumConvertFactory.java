package com.roacg.core.model.enums.convert;

import com.roacg.core.model.enums.DeletedStatusEnum;

import javax.persistence.Converter;

public final class BaseCodeEnumConvertFactory {

    @Converter(autoApply = true)
    public static class DeletedStatusEnumConvert extends AbstractBaseCodeEnumConverter<DeletedStatusEnum>{

        public DeletedStatusEnumConvert() {
            super(DeletedStatusEnum.class);
        }
    }


}
