package com.avalon.packer.config;

import com.avalon.packer.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: wangxb Date: 2021/4/13  Version: 1.0
 */
@Slf4j
public class MybatisRedisCache implements Cache {
    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private RedisTemplate<String, Object> redisTemplate;
    private ApplicationContext applicationContext;

    private String id;

    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }


    public RedisTemplate<String, Object> getRedisTemplate() {
        redisTemplate = (RedisTemplate<String, Object>) SpringContextUtil.getBean("redisTemplate");
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        redisTemplate = getRedisTemplate();
        if (value != null) {
            redisTemplate.opsForHash().put(id.toString(), key.toString(), value);
        }
    }

    @Override
    public Object getObject(Object key) {
        redisTemplate = getRedisTemplate();
        try {
            if (key != null) {
                return redisTemplate.opsForHash().get(id.toString(), key.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("缓存出错 ");
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        redisTemplate = getRedisTemplate();
        if (key != null) {
            redisTemplate.delete(key.toString());
        }
        return null;
    }

    @Override
    public void clear() {
        //System.out.println("清空缓存");
        log.debug("清空缓存");
        redisTemplate = getRedisTemplate();
        redisTemplate.delete(id.toString());
    }

    @Override
    public int getSize() {
        redisTemplate = getRedisTemplate();
        Long size = redisTemplate.opsForHash().size(id.toString());
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

}
