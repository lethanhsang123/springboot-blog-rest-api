package com.springboot.blog.service.cache;

public interface RedisService<HK, V> {
    V get(HK hashKey);

    void put(HK hashKey, V value);

    void delete(HK hashKey);
}
