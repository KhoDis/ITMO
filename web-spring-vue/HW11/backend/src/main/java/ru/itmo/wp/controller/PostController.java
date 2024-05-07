package ru.itmo.wp.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.exception.ValidationException;
import ru.itmo.wp.form.CommentForm;
import ru.itmo.wp.form.PostForm;
import ru.itmo.wp.service.JwtService;
import ru.itmo.wp.service.PostService;
import ru.itmo.wp.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/1")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final JwtService jwtService;

    public PostController(PostService postService, UserService userService, JwtService jwtService) {
        this.postService = postService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("posts")
    public List<Post> findPosts() {
        return postService.findAll();
    }

    @GetMapping("comments/{id}")
    public List<Comment> getComments(@PathVariable String id) {
        long postId;
        try {
            postId = Long.parseLong(id);
        } catch (NumberFormatException ignored) {
            throw new ValidationException("Id is not a number");
        }

        Post post = postService.findById(postId).orElseThrow(() -> new ValidationException("Post not found"));

        return post.getComments();
    }

    @PostMapping("posts")
    public void createPost(@Valid @RequestBody PostForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        String jwt = form.getJwt();
        if (jwt == null) {
            throw new ValidationException("You are not logged");
        }

        User user = jwtService.find(jwt);
        if (user == null) {
            throw new ValidationException("Invalid user");
        }

        Post post = new Post();
        post.setText(form.getText());
        post.setTitle(form.getTitle());

        user.addPost(post);
        userService.update(user);
    }

    @PostMapping("comments")
    public void createComment(@Valid @RequestBody CommentForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        User user = jwtService.find(form.getJwt());
        if (user == null) {
            bindingResult.rejectValue(form.getJwt(), "Can't parse JWT");
            throw new ValidationException(bindingResult);
        }

        Comment comment = new Comment();
        comment.setText(form.getText());
        comment.setUser(user);

        postService.writeComment(comment, form.getPostId(), bindingResult);
    }
}
