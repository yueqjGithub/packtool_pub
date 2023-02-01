package com.avalon.packer.cache.impl;


import com.avalon.packer.cache.ZzAbstractZzRedisCacheService;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wangxb
 **/
public class ZzSetRedisCacheService extends ZzAbstractZzRedisCacheService {

    @Override
    public <T> Set<T> sGet(String key, Class<T> clazz) {
        SetOperations<String, T> setOperations = redisTemplate.opsForSet();
        Set<T> members = setOperations.members(key);
        return members;
    }

    @Override
    public boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSet(String key, Object... vs) {
        return redisTemplate.opsForSet().add(key, vs);
    }

    @Override
    public Long sSet(String key, long time, TimeUnit unit, Object... vs) {
        Long add = redisTemplate.opsForSet().add(key, vs);
        if (time > 0) {
            expire(key, time, unit);
        }
        return add;
    }

    @Override
    public long sGetSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public long sRemoveVal(String key, Object vs) {
        return redisTemplate.opsForSet().remove(key, vs);
    }
}
