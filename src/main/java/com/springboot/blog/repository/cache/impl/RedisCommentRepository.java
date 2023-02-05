package com.springboot.blog.repository.cache.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.repository.cache.RedisRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class RedisCommentRepository implements RedisRepository<String, Long, Comment> {

    private final RedisTemplate redisTemplate;

    private final ModelMapper mapper;

    @Override
    public Comment get(String key, Long hashKey) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        if( value != null ) {
            return mapper.map(value, Comment.class);
        }
        return null;
    }

    @Override
    public void delete(String key, Long hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    @Override
    public void put(String key, Long hashKey, Comment value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
}
