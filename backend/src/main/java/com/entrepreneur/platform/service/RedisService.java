package com.entrepreneur.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存储数据到Redis
     * @param key 键
     * @param value 值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 从Redis获取数据
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除Redis中的数据
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 检查Redis中是否存在指定的键
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置Redis键的过期时间
     * @param key 键
     * @param timeout 过期时间（秒）
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }
}
