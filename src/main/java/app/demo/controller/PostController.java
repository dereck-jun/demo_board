package app.demo.controller;

import app.demo.domain.entity.UserEntity;
import app.demo.domain.post.Post;
import app.demo.domain.post.PostDraftRequest;
import app.demo.domain.post.PostPublishRequest;
import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponse;
import app.demo.exception.response.BaseResponseStatus;
import app.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static app.demo.exception.response.BaseResponseStatus.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private static <T> ResponseEntity<BaseResponse<T>> responseErrorStatusToJson(BaseException e) {
        BaseResponseStatus status = e.getStatus();
        return switch (status) {
            case USER_NOT_FOUND -> new ResponseEntity<>(new BaseResponse<>(USER_NOT_FOUND), status.getStatus());
            case PRINCIPAL_MISMATCH_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(PRINCIPAL_MISMATCH_ERROR), status.getStatus());
            case USER_ALREADY_DELETED_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(USER_ALREADY_DELETED_ERROR), status.getStatus());
            case INVALID_AUTH_HEADER ->
                    new ResponseEntity<>(new BaseResponse<>(INVALID_AUTH_HEADER), status.getStatus());
            case INVALID_USER_ROLE -> new ResponseEntity<>(new BaseResponse<>(INVALID_USER_ROLE), status.getStatus());
            case EXPIRED_JWT_TOKEN -> new ResponseEntity<>(new BaseResponse<>(EXPIRED_JWT_TOKEN), status.getStatus());
            case INVALID_JWT_TOKEN -> new ResponseEntity<>(new BaseResponse<>(INVALID_JWT_TOKEN), status.getStatus());
            case EMAIL_DUPLICATION_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(EMAIL_DUPLICATION_ERROR), status.getStatus());
            case LOGIN_FAILED_ERROR -> new ResponseEntity<>(new BaseResponse<>(LOGIN_FAILED_ERROR), status.getStatus());

            default -> throw e;
        };
    }

    @PostMapping("/draft")
    public ResponseEntity<BaseResponse<Post>> createDraftPost(@RequestBody PostDraftRequest postDraftRequest, Authentication authentication) {
        try {
            Post post = postService.saveDraft(postDraftRequest, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(post), CREATED);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    @PostMapping("/draft/{postId}")
    public ResponseEntity<BaseResponse<Post>> updateDraftToPublish(@PathVariable Long postId, @RequestBody PostPublishRequest postPublishRequest, Authentication authentication) {
        try {
            Post post = postService.draftToPublishPost(postId, postPublishRequest, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(post), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    @PostMapping("/publish")
    public ResponseEntity<BaseResponse<Post>> createPublishPost(@RequestBody PostPublishRequest postPublishRequest, Authentication authentication) {
        try {
            Post post = postService.publishPost(postPublishRequest, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(post), CREATED);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    @PostMapping("/publish/{postId}")
    public ResponseEntity<BaseResponse<Post>> savePublishToDraft(@PathVariable Long postId, @RequestBody PostPublishRequest postPublishRequest, Authentication authentication) {
        try {
            Post post = postService.saveDraftFromPublish(postId, postPublishRequest, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(post), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }
}
