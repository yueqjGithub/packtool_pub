package com.avalon.packer.cache.impl;

import com.avalon.packer.cache.ZzAbstractZzRedisCacheService;
import com.avalon.packer.exception.AvalonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.avalon.packer.http.AvalonError.REDIS_OPS_HASH;


/**
 * @author wangxb
 * @date 2021/04/12 02:42
 **/
@Component
@Slf4j
public class ZzHashRedisCacheService extends ZzAbstractZzRedisCacheService {
    @Override
    public <T> T hGet(String key, String item, Class<T> clazz) {
        try {
            HashOperations<String, String, T> var = redisTemplate.opsForHash();
            T t = var.get(key, item);
            return t;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hGet]");
        }
    }

    @Override
    public <K, V> Map<K, V> hmGet(String key, Class<K> var1, Class<V> var2) {
        try {
            HashOperations<String, K, V> var = redisTemplate.opsForHash();
            Map<K, V> entries = var.entries(key);
            return entries;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hmGet]");
        }
    }

    @Override
    public <T> boolean hmSet(String key, Map<String, T> map) {
        try {
            HashOperations<String, String, T> var = redisTemplate.opsForHash();
            var.putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("{}",e);
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hmSet]");
        }
    }

    @Override
    public boolean hmSet(String key, Map<String, Object> map, long time, TimeUnit unit) {
        try {
            HashOperations<String, String, Object> var = redisTemplate.opsForHash();
            var.putAll(key, map);
            if (time > 0) {
                if (null == unit) {
                    unit = TimeUnit.SECONDS;
                }
                expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hmSet]");
        }
    }

    @Override
    public boolean hSet(String key, String item, Object value) {
        try {
            HashOperations<String, String, Object> var = redisTemplate.opsForHash();
            var.put(key, item, value);
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hSet]");
        }

    }

    @Override
    public boolean hSet(String key, String item, Object value, long time, TimeUnit unit) {
        try {
            hSet(key, item, value);
            if (time > 0) {
                initTimeUnit(unit);
                expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_OPS_HASH, "Redis ops Hash is Error : [hSet]");
        }
    }

    @Override
    public void hDel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    @Override
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    @Override
    public double hIncr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    @Override
    public double hDecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
}
