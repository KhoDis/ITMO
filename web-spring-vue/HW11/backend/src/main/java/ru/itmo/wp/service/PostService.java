package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public void writeComment(Comment comment, long postId, BindingResult result) {
        Post post = postRepository.findById(postId).orElse(null);
        comment.setPost(post);
        if (post == null) {
            result.rejectValue("post", "Post can't be null");
            throw new ValidationException(result);
        }
        post.addComment(comment);
        postRepository.save(post);
    }

    public Optional<Post> findById(long postId) {
        return postRepository.findById(postId);
    }
}
