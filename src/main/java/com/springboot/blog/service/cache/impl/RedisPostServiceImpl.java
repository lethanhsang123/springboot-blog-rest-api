package com.springboot.blog.service.cache.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.repository.cache.RedisRepository;
import com.springboot.blog.service.cache.RedisService;
import com.springboot.blog.utils.AppConstants;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisPostServiceImpl implements RedisService<Long, Post> {

    private final RedisRepository<String, Long, Post> redisRepository;

    private final ModelMapper mapper;

    private final String key = AppConstants.POST_KEY_PREFIX;

    @Override
    public Post get(Long hashKey) {
        return (Post) redisRepository.get(key, hashKey);
    }

    @Override
    public void put(Long hashKey, Post value) {
        redisRepository.put(key, hashKey, value);
    }

    @Override
    public void delete(Long hashKey) {
        redisRepository.delete(key, hashKey);
    }

}
