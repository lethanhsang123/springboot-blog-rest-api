package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import com.springboot.blog.service.cache.RedisService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final ModelMapper mapper;

    private final RedisService<Long, Post> redisService;

//    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, RedisPostServiceImpl redisRepository) {
//        this.postRepository = postRepository;
//        this.mapper = mapper;
//        this.redisPostService = redisRepository;
//    }

    @Override
    public PostDto createPost(PostDto postDto) {

        // convert DTO to Entity
        Post post = mapToEntity(postDto);


        Post newPost = postRepository.save(post);

        // convert entity to DTO
        PostDto postResponse = mapToDto(newPost);

        return postResponse;
    }

    //Convert Entity into DTO
    private PostDto mapToDto(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
//        postDto.setTitle(post.getTitle());
        return postDto;
    }

    // Convert DTO into Entity
    private Post mapToEntity(PostDto postDto) {
        Post post = mapper.map(postDto, Post.class);

//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getDescription());
        return post;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> postList = posts.getContent();

        List<PostDto> content = postList.stream()
                .map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        // Check Post in Redis cache
        System.out.println("Getting Post in Cache");
        Post post = redisService.get(id);

        // Get data in Database
        if(post == null) {
           post = postRepository.findById(id).
                   orElseThrow(()->
                           new ResourceNotFoundException("Post", "id",id));
            System.out.println("Doesn't exist Post in Cache");
            redisService.put(post.getId(), post);
            System.out.println("Save Post into Cache");
        }
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        // Get post by id from the database
        Post post = postRepository.findById(id).
                orElseThrow(()->
                        new ResourceNotFoundException("Post", "id",id));
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        // Update Post into Database
        Post updatePost = postRepository.save(post);

        // Check Post in Cache
        Post cachePost = redisService.get(id);
        if (cachePost != null) {
            redisService.put(id, post);
        }

        return mapToDto(updatePost);
    }

    @Override
    public void deletePostById(long id) {
        // Get post by id from the database
        Post post = postRepository.findById(id).
                orElseThrow(()->
                        new ResourceNotFoundException("Post", "id",id));
        postRepository.delete(post);

        // Delete Post in Cache if exist
        if( redisService.get(id) != null ) {
            redisService.delete(id);
        }
    }
}
