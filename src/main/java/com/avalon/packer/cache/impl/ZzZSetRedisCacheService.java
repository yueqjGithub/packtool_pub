package com.avalon.packer.cache.impl;

import com.avalon.packer.cache.ZzAbstractZzRedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Author: Wangxb
 * description:
 */
@Service
@Slf4j
public class ZzZSetRedisCacheService extends ZzAbstractZzRedisCacheService {

    @Override
    public boolean zsSet(String key, String val, double score) {
        ZSetOperations<String, String> zSetOperations = getOperations();
        return zSetOperations.add(key, val, score);
    }

    @Override
    public Long zsRemove(String key, String... vals) {
        ZSetOperations<String, String> zSetOperations = getOperations();
        return zSetOperations.remove(key, vals);
    }

    @Override
    public double zsUpdateScore(String key, String value, double score) {
        ZSetOperations<String, String> zSetOperations = getOperations();
        return zSetOperations.incrementScore(key, value, score);
    }

    @Override
    public double zsGetScore(String key, String val) {
        ZSetOperations<String, String> operations = getOperations();
        return operations.score(key, val);
    }

    @Override
    public Long zsGetRank(String key, String val) {
        ZSetOperations<String, String> operations = getOperations();
        return operations.rank(key, val);
    }

    @Override
    public Long zsGetSize(String key) {
        ZSetOperations<String, String> operations = getOperations();
        return operations.zCard(key);
    }

    @Override
    public Set<String> zsRange(String key, int start, int end) {
        return getOperations().range(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> zsRangeWithScore(String key, int start, int end) {
        return getOperations().rangeWithScores(key, start, end);
    }

    @Override
    public Set<String> zsRevRange(String key, int start, int end) {
        return getOperations().reverseRange(key, start, end);
    }

    @Override
    public Set<String> zsSortRange(String key, int min, int max) {
        return getOperations().rangeByScore(key, min, max);
    }

    private ZSetOperations<String, String> getOperations() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations;
    }
}
