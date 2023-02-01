package com.avalon.packer.cache;

import com.avalon.packer.exception.AvalonException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.avalon.packer.http.AvalonError.REDIS_CACHE;


/**
 * @author wangxb
 * @date 2021/04/12 12:28
 **/
public abstract class ZzAbstractZzRedisCacheService implements IZzRedisCacheService {
    @Resource
    public RedisTemplate redisTemplate;

    public void initTimeUnit(TimeUnit unit) {
        if (null == unit) {
            unit = TimeUnit.SECONDS;
        }
    }

    @Override
    public boolean expire(String key, long time, TimeUnit unit) {
        try {
            if (null == unit) {
                unit = TimeUnit.SECONDS;
            }
            if (time > 0) {
                redisTemplate.expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            throw new AvalonException(REDIS_CACHE, "Redis ops Common is Error : [expire]");
        }
    }

    @Override
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            throw new AvalonException(REDIS_CACHE, "Redis ops Common is Error : [hasKey]");
        }
    }

    @Override
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public boolean set(String key, Object value) {
        return false;
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit unit) {
        return false;
    }

    @Override
    public long incr(String key, long delta) {
        return 0;
    }

    @Override
    public long decr(String key, long delta) {
        return 0;
    }

    @Override
    public <T> T hGet(String key, String item, Class<T> clazz) {
        return null;
    }

    @Override
    public <K, V> Map<K, V> hmGet(String key, Class<K> var1, Class<V> clazz) {
        return null;
    }

    @Override
    public <T> boolean hmSet(String key, Map<String, T> map) {
        return false;
    }

    @Override
    public boolean hmSet(String key, Map<String, Object> map, long time, TimeUnit unit) {
        return false;
    }

    @Override
    public boolean hSet(String key, String item, Object value) {
        return false;
    }

    @Override
    public boolean hSet(String key, String item, Object value, long time, TimeUnit unit) {
        return false;
    }

    @Override
    public void hDel(String key, Object... item) {

    }

    @Override
    public boolean hHasKey(String key, String item) {
        return false;
    }

    @Override
    public double hIncr(String key, String item, double by) {
        return 0;
    }

    @Override
    public double hDecr(String key, String item, double by) {
        return 0;
    }

    @Override
    public boolean zsSet(String key, String val, double score) {
        return false;
    }

    @Override
    public Long zsRemove(String key, String... vals) {
        return null;
    }

    @Override
    public double zsUpdateScore(String key, String value, double score) {
        return 0;
    }

    @Override
    public double zsGetScore(String key, String val) {
        return 0;
    }

    @Override
    public Long zsGetRank(String key, String val) {
        return null;
    }

    @Override
    public Long zsGetSize(String key) {
        return null;
    }

    @Override
    public Set<String> zsRange(String key, int start, int end) {
        return null;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zsRangeWithScore(String key, int start, int end) {
        return null;
    }

    @Override
    public Set<String> zsRevRange(String key, int start, int end) {
        return null;
    }

    @Override
    public Set<String> zsSortRange(String key, int min, int max) {
        return null;
    }

    @Override
    public <T> Set<T> sGet(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public boolean sHasKey(String key, Object value) {
        return false;
    }

    @Override
    public Long sSet(String key, Object... vs) {
        return null;
    }

    @Override
    public Long sSet(String key, long time, TimeUnit unit, Object... vs) {
        return null;
    }

    @Override
    public long sGetSetSize(String key) {
        return 0;
    }

    @Override
    public long sRemoveVal(String key, Object vs) {
        return 0;
    }

    @Override
    public <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
        return null;
    }

    @Override
    public long lGetListSize(String key) {
        return 0;
    }

    @Override
    public long lSet(String key, Object value) {
        return 0;
    }

    @Override
    public long lSet(String key, Object value, long time, TimeUnit unit) {
        return 0;
    }

    @Override
    public <T> long lSet(String key, List<T> vs) {
        return 0;
    }

    @Override
    public <T> long lSet(String key, List<T> vs, long time, TimeUnit unit) {
        return 0;
    }

    @Override
    public <T> boolean lUpdateByIndex(String key, long index, T v) {
        return false;
    }

    @Override
    public long lRemove(String key, long count, Object value) {
        return 0;
    }

    @Override
    public <T> T lGetIndex(String key, long index) {
        return null;
    }
}
