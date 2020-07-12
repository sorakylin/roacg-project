package com.roacg.core.utils.bean;

import com.roacg.core.model.exception.RoApiException;
import org.springframework.beans.BeanUtils;

import static com.roacg.core.model.enums.RoApiStatusEnum.SYSTEM_ERROR;

/**
 * Bean copu utils
 * Create by skypyb on 2020.06.26
 */
public final class BeanMapper {
/*
    private static Cache<String, BeanCopier> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofDays(1))//缓存项在给定时间内没有被读/写访问，则回收
            .build();*/

    public static <S, T> T map(final S source, final Class<T> targetClass) {

        T target = null;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
            map(source, target);
        } catch (ReflectiveOperationException e) {
            RoApiException exception = new RoApiException(SYSTEM_ERROR, e.getMessage());
            exception.setCause(e);
            throw exception;
        }
        return target;
    }

    public static <S, T> T map(final S source, final T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
