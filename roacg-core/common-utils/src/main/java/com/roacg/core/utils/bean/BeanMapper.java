package com.roacg.core.utils.bean;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.roacg.core.model.exception.RoApiException;
import org.springframework.cglib.beans.BeanCopier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.roacg.core.model.enums.RoApiStatusEnum.SYSTEM_ERROR;

public final class BeanMapper {

    private static Cache<String, BeanCopier> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(Duration.ofDays(1))//缓存项在给定时间内没有被读/写访问，则回收
            .build();


    private Map<String, BeanCopier> copierCache = new HashMap<>();

    public static <S, T> T map(final S source, final Class<T> targetClass) {

        T target = null;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            RoApiException exception = new RoApiException(SYSTEM_ERROR, e.getMessage());
            exception.setCause(e);
            throw exception;
        }

        return target;
    }

    public static <S, T> void map(final S source, final T target) {
        BeanCopier beanCopier = findCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
    }

    private static <S, T> BeanCopier findCopier(final Class<S> source, final Class<T> target) {
        String key = source.getName() + "-" + target.getName();

        BeanCopier beanCopier = null;
        try {
            beanCopier = cache.get(key, () -> BeanCopier.create(source.getClass(), target.getClass(), false));
        } catch (ExecutionException e) {
            RoApiException exception = new RoApiException(SYSTEM_ERROR, e.getMessage());
            exception.setCause(e);
            throw exception;
        }

        return beanCopier;
    }
}
