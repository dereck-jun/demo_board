package app.demo.repository;

import app.demo.domain.entity.PostEntity;
import app.demo.domain.post.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    @Query("select p from PostEntity p where p.user.id = :userId and p.id = :postId and p.status = 'DRAFT'")
    Optional<PostEntity> findDraftByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("select p from PostEntity p where p.id = :postId and p.author = :author and p.status = :status")
    Optional<PostEntity> findByIdAndAuthorAndStatus(@Param("postId") Long postId, @Param("author") String author, @Param("status") PostStatus status);
}
