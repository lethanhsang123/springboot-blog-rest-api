package com.springboot.blog.service.cache.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.repository.cache.RedisRepository;
import com.springboot.blog.service.cache.RedisService;
import com.springboot.blog.utils.AppConstants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisCommentServiceImpl implements RedisService<Long, Comment> {

    private final RedisRepository<String, Long, Comment> redisRepository;

    private final String key = AppConstants.COMMENT_KEY_PREFIX;

    @Override
    public Comment get(Long hashKey) {
        return (Comment) redisRepository.get(key, hashKey);
    }

    @Override
    public void put(Long hashKey, Comment value) {
        redisRepository.put(key, hashKey, value);
    }

    @Override
    public void delete(Long hashKey) {
        redisRepository.delete(key, hashKey);
    }
}
