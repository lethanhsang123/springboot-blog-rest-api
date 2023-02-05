package com.springboot.blog.repository.cache;

public interface RedisRepository<K, HK, V> {

    V get (K key, HK hashKey);

    void delete(K key, HK hashKey);

    void put(K key, HK hashKey, V value);

}
