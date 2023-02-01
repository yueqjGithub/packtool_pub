package com.avalon.packer.cache;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wangxb
 **/
public interface IZzRedisCacheService {
    /**
     * 指定缓存的失效时间
     *
     * @param key
     * @param time
     * @param unit 单位 默认是秒
     * @return boolean
     * @author wangxb
     * @date 2021/04/05 17:50
     */
    boolean expire(String key, long time, TimeUnit unit);


    /**
     * 获取过期时间
     *
     * @param key
     * @return long 返回0代表永久有效
     * @author wangxb
     * @date 2021/04/05 17:54
     */
    long getExpire(String key);


    /**
     * 判断key是否存在
     *
     * @param key
     * @return boolean true 存在 false不存在
     * @author wangxb
     * @date 2021/04/05 17:55
     */
    boolean hasKey(String key);

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     * @return void
     * @author wangxb
     * @date 2021/04/05 17:57
     */
    void del(String... key);

    /**
     * ------------------------------------------------
     * Redis 操作String
     * ------------------------------------------------
     */

    /**
     * 普通缓存获取
     *
     * @param key
     * @param clazz
     * @return T
     * @author wangxb
     * @date 2021/04/12 02:45
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 普通缓存放入
     *
     * @param key
     * @param value
     * @return boolean
     * @author wangxb
     * @date 2021/04/12 02:46
     */
    boolean set(String key, Object value);

    /**
     * 存放带有有效期的缓存
     *
     * @param key
     * @param value
     * @param time
     * @param unit
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:33
     */
    boolean set(String key, Object value, long time, TimeUnit unit);

    /**
     * 递增
     *
     * @param key
     * @param delta
     * @return long
     * @author wangxb
     * @date 2021/04/14 02:35
     */
    long incr(String key, long delta);

    /**
     * 递减
     *
     * @param key
     * @param delta
     * @return long
     * @author wangxb
     * @date 2021/04/14 02:35
     */
    long decr(String key, long delta);

    /**
     * ------------------------------------------------
     * Redis 操作hash
     * ------------------------------------------------
     */

    /**
     * HashGet
     *
     * @param key
     * @param item
     * @param clazz
     * @return T
     * @author wangxb
     * @date 2021/04/14 02:44
     */
    <T> T hGet(String key, String item, Class<T> clazz);

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key
     * @param va1
     * @return java.util.Map<java.lang.Object, java.lang.Object>
     * @author wangxb
     * @date 2021/04/14 02:47
     */
    <K, V> Map<K, V> hmGet(String key, Class<K> va1, Class<V> var2);

    /**
     * HashSet
     *
     * @param key
     * @param map
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:49
     */
    <T> boolean hmSet(String key, Map<String, T> map);

    /**
     * HashSet 并设置时间
     *
     * @param key
     * @param map
     * @param time
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:49
     */
    boolean hmSet(String key, Map<String, Object> map, long time, TimeUnit unit);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key
     * @param item
     * @param value
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:51
     */
    boolean hSet(String key, String item, Object value);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key
     * @param item
     * @param value
     * @param time  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @param unit
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:51
     */
    boolean hSet(String key, String item, Object value, long time, TimeUnit unit);

    /**
     * 删除hash表中的值
     *
     * @param key
     * @param item
     * @return void
     * @author wangxb
     * @date 2021/04/14 02:52
     */
    void hDel(String key, Object... item);

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key
     * @param item
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:53
     */
    boolean hHasKey(String key, String item);

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by   by 要增加几(大于0)
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    double hIncr(String key, String item, double by);

    /**
     * hash递减
     *
     * @param key
     * @param item
     * @param by   要递减几(大于0)
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    double hDecr(String key, String item, double by);

    /**
     * ------------------------------------------------
     * Redis 操作 Zset
     * ------------------------------------------------
     */
    /**
     * 添加一个元素, zset与set最大的区别就是每个元素都有一个score，因此有个排序的辅助功能;  zadd
     *
     * @param key
     * @param key
     * @param val
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    boolean zsSet(String key, String val, double score);

    /**
     * 删除元素
     *
     * @param key
     * @param vals
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Long zsRemove(String key, String... vals);

    /**
     * 修改值
     *
     * @param key
     * @param value
     * @param score
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    double zsUpdateScore(String key, String value, double score);

    /**
     * 查询value对应的score   zscore
     *
     * @param key
     * @param val
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    double zsGetScore(String key, String val);

    /**
     * 获取排名
     *
     * @param key
     * @param val
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Long zsGetRank(String key, String val);

    /**
     * 获取长度
     *
     * @param key
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Long zsGetSize(String key);

    /**
     * 查询集合中指定顺序的值， 0 -1 表示获取全部的集合内容  zrange
     * 返回有序的集合，score小的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Set<String> zsRange(String key, int start, int end);

    /**
     * 查询集合中指定顺序的值和score，0, -1 表示获取全部的集合内容
     * 返回有序的集合，score小的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return double
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Set<ZSetOperations.TypedTuple<String>> zsRangeWithScore(String key, int start, int end);

    /**
     * 查询集合中指定顺序的值  zrevrange
     * 返回有序的集合中，score大的在前面
     *
     * @param key
     * @param start
     * @param end
     * @return Set<String>
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Set<String> zsRevRange(String key, int start, int end);

    /**
     * 根据score的值，来获取满足条件的集合  zrangebyscore
     *
     * @param key
     * @param min
     * @param max
     * @return Set<String>
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Set<String> zsSortRange(String key, int min, int max);


    /**
     * ------------------------------------------------
     * Redis 操作 Set
     * ------------------------------------------------
     */
    /**
     * 获取set中的所有元素
     *
     * @param key
     * @param clazz
     * @return Set<T>
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> Set<T> sGet(String key, Class<T> clazz);

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key
     * @param value
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    boolean sHasKey(String key, Object value);

    /**
     * 将数据放入set缓存
     *
     * @param key
     * @param vs
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Long sSet(String key, Object... vs);

    /**
     * 将数据放入set缓存 带有有效时间,默认是秒
     *
     * @param key
     * @param vs
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    Long sSet(String key, long time, TimeUnit unit, Object... vs);

    /**
     * 获取set缓存的长度
     *
     * @param key
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long sGetSetSize(String key);

    /**
     * 移除值为value的
     *
     * @param key
     * @param vs
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long sRemoveVal(String key, Object vs);


    /**
     * ------------------------------------------------
     * Redis 操作 List
     * ------------------------------------------------
     */
    /**
     * 获取list缓存的内容  0 到 -1代表所有值
     *
     * @param key
     * @param start
     * @param end
     * @param clazz
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> List<T> lGet(String key, long start, long end, Class<T> clazz);

    /**
     * 获取list缓存的长度
     *
     * @param key
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long lGetListSize(String key);

    /**
     * 通过索引 获取list中的值  index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     *
     * @param key
     * @param index
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> T lGetIndex(String key, long index);

    /**
     * 将list放入缓存
     *
     * @param key
     * @param value
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long lSet(String key, Object value);

    /**
     * 将list放入缓存
     *
     * @param key
     * @param value
     * @param time
     * @param unit
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long lSet(String key, Object value, long time, TimeUnit unit);

    /**
     * 将list放入缓存 带有过期时间
     *
     * @param key
     * @param vs
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> long lSet(String key, List<T> vs);

    /**
     * 将list放入缓存 带有过期时间
     *
     * @param key
     * @param vs
     * @param time
     * @param unit
     * @return Long
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> long lSet(String key, List<T> vs, long time, TimeUnit unit);

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key
     * @param index
     * @param v
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    <T> boolean lUpdateByIndex(String key, long index, T v);

    /**
     * 移除N个值为value,
     *
     * @param key
     * @param count count 移除多少个
     * @param value value 值
     * @return boolean
     * @author wangxb
     * @date 2021/04/14 02:54
     */
    long lRemove(String key, long count, Object value);

}
