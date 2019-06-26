package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.LoginUserCachePhp;
import cn.onlyhi.client.dto.TupleDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/16.
 */
public interface RedisService {

    /**
     * 将登录用户信息存入缓存
     *
     * @param token
     * @param loginUserCache
     * @return
     */
    String setLoginUserCache(String token, LoginUserCache loginUserCache);
    /**
     *
     * @param token
     * @return
     */
    LoginUserCachePhp getLoginUserCachePhp(String token);
    /**
     * 根据token获取缓存的登录用户信息
     *
     * @param token
     * @return
     */
    LoginUserCache getLoginUserCache(String token);

    /**
     * 将key对应的value加1，只有value可以转为数字时该方法才可用
     *
     * @param key
     * @return long 加1后的值
     */
    long incr(String key);

    /**
     * 将key对应的value减1，只有value可以转为数字时该方法才可用
     *
     * @param key
     * @return long 减1后的值
     */
    long decr(String key);

    /**
     * 设置key和value
     *
     * @param key
     * @param value
     * @return
     */
    String set(String key, String value);

    /**
     * 设置key和value,并设置过期时间
     *
     * @param key
     * @param value
     * @param second 秒为单位
     * @return
     */
    String set(String key, String value, int second);

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 重命名key
     *
     * @param oldKey
     * @param newKey
     * @return 状态码
     */
    String rename(String oldKey, String newKey);

    /**
     * 为key设置默认过期时间：7天
     *
     * @param key
     * @return
     */
    void expire(String key);

    /**
     * 为key设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    void expire(String key, int seconds);

    /**
     * 删除keys对应的记录,可以是多个key
     *
     * @param keys
     * @return 删除的记录数
     */
    long del(String... keys);

    /**
     * 添加一个或多个成员到有序集合，或者如果它已经存在更新其分数
     *
     * @param key
     * @param scoreMembers
     * @return
     */
    long zadd(String key, Map<String, Double> scoreMembers);

    /**
     * 添加一个或多个成员到有序集合，或者如果它已经存在更新其分数
     *
     * @param key
     * @param score
     * @param members
     * @return
     */
    long zadd(String key, double score, String members);

    /**
     * 根据key和集合成员返回其索引值，若其不存在则返回null
     *
     * @param key
     * @param members
     * @return
     */
    Long zrank(String key, String members);

    /**
     * key的集合数量
     *
     * @param key
     * @return
     */
    Long zcard(String key);

    /**
     * 根据key和member增加分数score，若元素不存在则创建并返回新的分数
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    Double zincrby(String key, double score, String member);

    /**
     * 根据key和索引值查询成员集合
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Set<String> zrange(String key, long start, long end);

    /**
     * 根据key和索引值查询成员集合(包括分数)
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    List<TupleDto> zrangeWithScores(String key, long start, long end);

    /**
     * 删除指定成员，返回删除的数量
     *
     * @param key
     * @param members
     * @return
     */
    long zrem(String key, String... members);
}
