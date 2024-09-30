package app.demo.service;

import app.demo.domain.entity.PostEntity;
import app.demo.domain.entity.UserEntity;
import app.demo.domain.post.Post;
import app.demo.domain.post.PostDraftRequest;
import app.demo.domain.post.PostPublishRequest;
import app.demo.domain.post.PostStatus;
import app.demo.exception.BaseException;
import app.demo.repository.PostEntityRepository;
import app.demo.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static app.demo.exception.response.BaseResponseStatus.POST_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postRepository;

    /**
     * 임시저장(draft) 게시물 생성
     */
    @Transactional
    public Post saveDraft(PostDraftRequest postDraftRequest, UserEntity currentUser) {
        return Post.from(postRepository.save(
                PostEntity.of(
                        postDraftRequest.title(),
                        postDraftRequest.content(),
                        postDraftRequest.description(),
                        true,
                        currentUser
                )
        ));
    }

    /**
     * 발행 중이던 게시글 수정 중 임시 저장처리
     */
    @Transactional
    public Post saveDraftFromPublish(Long postId, PostPublishRequest postPublishRequest, UserEntity currentUser) {
        PostEntity publishedPost = postRepository.findByIdAndAuthorAndStatus(postId, currentUser.getNickname(), PostStatus.PUBLISHED)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));

        return Post.from(
                postRepository.save(
                        publishedPost.saveRevisionAsDraft(
                                postPublishRequest.title(),
                                postPublishRequest.content(),
                                postPublishRequest.description()
                        )
                )
        );
    }

    /**
     * 임시 저장 중이던 게시글 발행
     */
    @Transactional
    public Post draftToPublishPost(Long postId, PostPublishRequest postPublishRequest, UserEntity currentUser) {
        PostEntity draftPost = postRepository.findByIdAndAuthorAndStatus(postId, currentUser.getNickname(), PostStatus.DRAFT)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));

        return Post.from(
                postRepository.save(
                        draftPost.updatePublish(
                                postPublishRequest.title(),
                                postPublishRequest.content(),
                                postPublishRequest.description()
                        )
                )
        );
    }

    /**
     * 임시 저장 게시글 삭제
     */
    @Transactional
    public void deleteRevisionFromPost(Long postId, UserEntity currentUser) {
        PostEntity revisionPost = postRepository.findByIdAndAuthorAndStatus(postId, currentUser.getNickname(), PostStatus.DRAFT)
                .orElseThrow(() -> new BaseException(POST_NOT_FOUND));

        if (revisionPost.getRevision() != null) {
            revisionPost.deleteRevision();
            postRepository.save(revisionPost);
        }
    }

    /**
     * 게시글 작성 후 바로 발행
     */
    @Transactional
    public Post publishPost(PostPublishRequest postPublishRequest, UserEntity currentUser) {
        return Post.from(
                postRepository.save(
                        PostEntity.of(
                                postPublishRequest.title(),
                                postPublishRequest.content(),
                                postPublishRequest.description(),
                                false,
                                currentUser
                        )
                )
        );
    }
}
