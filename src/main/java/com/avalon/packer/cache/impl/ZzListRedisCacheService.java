package com.avalon.packer.cache.impl;

import com.avalon.packer.cache.ZzAbstractZzRedisCacheService;
import com.avalon.packer.exception.AvalonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.avalon.packer.http.AvalonError.REDIS_OPS_LIST;


/**
 * description:
 *
 * @author wangxb
 */
@Slf4j
public class ZzListRedisCacheService extends ZzAbstractZzRedisCacheService {

    @Override
    public <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
        try {
            ListOperations<String, T> listOperations = redisTemplate.opsForList();
            List<T> range = listOperations.range(key, start, end);
            return range;
        } catch (Exception e) {
            log.error("{}", e);
            throw new AvalonException(REDIS_OPS_LIST, "ZzListRedisCacheService lGet");
        }
    }

    @Override
    public long lGetListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public <T> T lGetIndex(String key, long index) {
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        T t = listOperations.index(key, index);
        return t;
    }

    @Override
    public long lSet(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public long lSet(String key, Object value, long time, TimeUnit unit) {
        Long l = redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time, unit);
        }
        return l;
    }

    @Override
    public <T> long lSet(String key, List<T> vs) {
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        return listOperations.rightPushAll(key, vs);
    }

    @Override
    public <T> long lSet(String key, List<T> vs, long time, TimeUnit unit) {
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        Long l = listOperations.rightPushAll(key, vs);
        if (time > 0) {
            expire(key, time, unit);
        }
        return l;
    }

    @Override
    public <T> boolean lUpdateByIndex(String key, long index, T v) {
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        listOperations.set(key, index, v);
        return true;
    }

    @Override
    public long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }
}
