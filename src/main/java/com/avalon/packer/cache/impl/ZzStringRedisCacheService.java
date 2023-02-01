package com.avalon.packer.cache.impl;

import com.avalon.packer.cache.ZzAbstractZzRedisCacheService;
import com.avalon.packer.exception.AvalonException;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.avalon.packer.http.AvalonError.REDIS_OPS_STRING;


/**
 * @author wangxb
 * @date 2021/04/12 02:42
 **/
@Component
public class ZzStringRedisCacheService extends ZzAbstractZzRedisCacheService {
    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        ValueOperations<String, T> vos = redisTemplate.opsForValue();
        T t = vos.get(key);
        return t;
    }

    @Override
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_STRING, "Redis ops String is Error : [set]");
        }
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit unit) {
        try {
            if (null == unit) {
                unit = TimeUnit.SECONDS;
            }
            ValueOperations<String, Object> vos = redisTemplate.opsForValue();
            if (time > 0) {
                vos.set(key, value, time, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_STRING, "Redis ops String is Error : [set]");
        }
    }

    @Override
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new AvalonException(REDIS_OPS_STRING, "delta is < 0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new AvalonException(REDIS_OPS_STRING, "delta is < 0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
}
