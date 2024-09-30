package app.demo.domain.entity;

import app.demo.domain.post.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "post")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update post set deleted_date_time = current_timestamp where post_id = ?")
@SQLRestriction("deleted_date_time is null")
public class PostEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Setter(AccessLevel.PROTECTED)
    @Column(nullable = false)
    private String title;

    @Lob
    @Setter(AccessLevel.PROTECTED)
    @Column(nullable = false)
    private String content;

    @Setter(AccessLevel.PROTECTED)
    @Column
    private String description;

    @Setter(AccessLevel.PROTECTED)
    @Column(nullable = false, updatable = false)
    private String author;

    private boolean isDraft;

    @Setter(AccessLevel.PROTECTED)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private PostEntity revision;

    public static PostEntity of(String title, String content, String description, boolean isDraft, UserEntity currentUser) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(title);
        postEntity.setContent(content);
        postEntity.setDescription(description);
        postEntity.setAuthor(currentUser.getNickname());
        if (isDraft) {
            postEntity.setStatus(PostStatus.DRAFT);
        } else {
            postEntity.setStatus(PostStatus.PUBLISHED);
        }
        return postEntity;
    }

    public PostEntity saveRevisionAsDraft(String title, String content, String description) {
        PostEntity revisionEntity = new PostEntity();
        revisionEntity.setTitle(title);
        revisionEntity.setContent(content);
        revisionEntity.setDescription(description);
        revisionEntity.setAuthor(this.author);
        revisionEntity.setStatus(PostStatus.DRAFT);
        revisionEntity.user = this.user;
        return revisionEntity;
    }

    public void deleteRevision() {
        this.revision = null;
    }

    public PostEntity updatePublish(String title, String content, String description) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.status = PostStatus.PUBLISHED;
        return this;
    }

    public boolean getIsDraft() {
        return isDraft;
    }
}
