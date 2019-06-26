package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.RedisDao;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.LoginUserCachePhp;
import cn.onlyhi.client.dto.TupleDto;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/10.
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisDao redisDao;

    @Override
    public String setLoginUserCache(String token, LoginUserCache loginUserCache) {
        String status = set(token, JSON.toJSONString(loginUserCache));
        expire(token);
        return status;
    }

    @Override
    public LoginUserCachePhp getLoginUserCachePhp(String token) {
        String json = get(token);
        return JSON.parseObject(json, LoginUserCachePhp.class);
    }

    @Override
    public LoginUserCache getLoginUserCache(String token) {
        String json = get(token);
        return JSON.parseObject(json, LoginUserCache.class);
    }

    @Override
    public long incr(String key) {
        return redisDao.incr(key);
    }

    @Override
    public long decr(String key) {
        return redisDao.decr(key);
    }

    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * 默认过期时间7天
     *
     * @param key
     * @param value
     * @return 状态码
     */
    @Override
    public String set(String key, String value) {
        String status =null;
        if(key!=null){
            status = redisDao.set(key, value);
        }
        return status;
    }

    @Override
    public String set(String key, String value, int second) {
        return redisDao.set(key, value, second);
    }

    @Override
    public String get(String key) {
        return redisDao.get(key);
    }

    @Override
    public boolean exists(String key) {
        return redisDao.exists(key);
    }

    @Override
    public String rename(String oldKey, String newKey) {
        return redisDao.rename(oldKey, newKey);
    }

    @Override
    public void expire(String key) {
        redisDao.expire(key);
    }

    @Override
    public void expire(String key, int seconds) {
        redisDao.expire(key, seconds);
    }

    @Override
    public long del(String... keys) {
        return redisDao.del(keys);
    }

    @Override
    public long zadd(String key, Map<String, Double> scoreMembers) {
        return redisDao.zadd(key, scoreMembers);
    }

    @Override
    public long zadd(String key, double score, String members) {
        return redisDao.zadd(key, score, members);
    }

    @Override
    public Long zrank(String key, String members) {
        return redisDao.zrank(key, members);
    }

    @Override
    public Long zcard(String key) {
        return redisDao.zcard(key);
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        return redisDao.zincrby(key, score, member);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return redisDao.zrange(key, start, end);
    }

    @Override
    public List<TupleDto> zrangeWithScores(String key, long start, long end) {
        Set<Tuple> tupleSet = redisDao.zrangeWithScores(key, start, end);
        List<TupleDto> tupleDtoList = new ArrayList<>();
        TupleDto tupleDto = new TupleDto();
        for (Tuple tuple : tupleSet) {
            tupleDto = new TupleDto();
            String element = tuple.getElement();
            double score = tuple.getScore();
            tupleDto.setElement(element);
            tupleDto.setScore(score);
            tupleDtoList.add(tupleDto);
        }
        return tupleDtoList;
    }

    @Override
    public long zrem(String key, String... members) {
        return redisDao.zrem(key, members);
    }
}
