package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "sermon_note")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class SermonNoteJpaEntity extends BaseEntity {

    /** Sermon note title */
    @Column(nullable = false, length = 100)
    private String title;

    /** Date of the sermon */
    @Column(name = "sermon_date", nullable = false)
    private LocalDate sermonDate;

    /** Name of the preacher */
    @Column(length = 50)
    private String preacher;

    /** Type of worship service (free text for flexibility across churches) */
    @Column(name = "service_type", length = 50)
    private String serviceType;

    /** Bible scripture reference */
    @Column(length = 200)
    private String scripture;

    /** Personal note content */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberJpaEntity member;
}
