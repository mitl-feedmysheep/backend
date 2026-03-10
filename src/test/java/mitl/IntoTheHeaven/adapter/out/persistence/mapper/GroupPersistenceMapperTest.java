package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.GroupType;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupPersistenceMapperTest {

    @Mock
    private MemberPersistenceMapper memberPersistenceMapper;

    @Mock
    private MediaPersistenceMapper mediaPersistenceMapper;

    @InjectMocks
    private GroupPersistenceMapper mapper;

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 churchId가 중첩 엔티티에서 추출되고 mediaPersistenceMapper가 호출된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();

        ChurchJpaEntity church = ChurchJpaEntity.builder().id(churchId).build();

        GroupJpaEntity entity = GroupJpaEntity.builder()
                .id(id)
                .name("청년 1셀")
                .description("청년부 1셀입니다")
                .church(church)
                .type(GroupType.NORMAL)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .build();

        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Group domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("청년 1셀");
        assertThat(domain.getDescription()).isEqualTo("청년부 1셀입니다");
        assertThat(domain.getChurchId().getValue()).isEqualTo(churchId);
        assertThat(domain.getType()).isEqualTo(GroupType.NORMAL);
        assertThat(domain.getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(domain.getEndDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        verify(mediaPersistenceMapper).toDomainList(any());
    }

    @Test
    @DisplayName("toDomain: medias가 있는 경우 mediaPersistenceMapper를 통해 변환된다")
    void toDomain_withMedias() {
        UUID mediaEntityId = UUID.randomUUID();
        List<Media> mockMedias = List.of(
                Media.builder()
                        .id(mitl.IntoTheHeaven.domain.model.MediaId.from(UUID.randomUUID()))
                        .url("https://example.com/img.jpg")
                        .build());

        GroupJpaEntity entity = GroupJpaEntity.builder()
                .id(UUID.randomUUID())
                .church(ChurchJpaEntity.builder().id(UUID.randomUUID()).build())
                .type(GroupType.NEWCOMER)
                .build();

        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(mockMedias);

        Group domain = mapper.toDomain(entity);

        assertThat(domain.getMedias()).hasSize(1);
        assertThat(domain.getMedias().get(0).getUrl()).isEqualTo("https://example.com/img.jpg");
    }

    @Test
    @DisplayName("toEntity: Domain -> JPA Entity 변환 시 stub ChurchJpaEntity가 생성되고 medias는 포함하지 않는다")
    void toEntity() {
        UUID id = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();

        Group domain = Group.builder()
                .id(GroupId.from(id))
                .name("새가족부")
                .description("새가족 소그룹")
                .churchId(ChurchId.from(churchId))
                .type(GroupType.NEWCOMER)
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 9, 30))
                .build();

        GroupJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("새가족부");
        assertThat(entity.getDescription()).isEqualTo("새가족 소그룹");
        assertThat(entity.getChurch().getId()).isEqualTo(churchId);
        assertThat(entity.getType()).isEqualTo(GroupType.NEWCOMER);
        assertThat(entity.getStartDate()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(entity.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 30));
    }

    @Test
    @DisplayName("toGroupMemberDomain: 엔티티에서 groupId를 추출하고 memberPersistenceMapper로 Member를 변환한다")
    void toGroupMemberDomain_fromEntity() {
        UUID gmId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        Member mockMember = Member.builder()
                .id(MemberId.from(memberId))
                .name("테스트멤버")
                .build();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder().id(memberId).build();
        GroupJpaEntity groupEntity = GroupJpaEntity.builder().id(groupId).build();

        GroupMemberJpaEntity entity = GroupMemberJpaEntity.builder()
                .id(gmId)
                .group(groupEntity)
                .member(memberEntity)
                .role(GroupMemberRole.SUB_LEADER)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        when(memberPersistenceMapper.toDomain(memberEntity)).thenReturn(mockMember);

        GroupMember domain = mapper.toGroupMemberDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(gmId);
        assertThat(domain.getGroupId().getValue()).isEqualTo(groupId);
        assertThat(domain.getMember().getName()).isEqualTo("테스트멤버");
        assertThat(domain.getRole()).isEqualTo(GroupMemberRole.SUB_LEADER);
        assertThat(domain.getStatus()).isEqualTo(GroupMemberStatus.ACTIVE);
        verify(memberPersistenceMapper).toDomain(memberEntity);
    }

    @Test
    @DisplayName("toGroupMemberDomain(entity, groupId): 외부 groupId를 사용하여 GroupMember를 생성한다")
    void toGroupMemberDomain_withExternalGroupId() {
        UUID gmId = UUID.randomUUID();
        UUID entityGroupId = UUID.randomUUID();
        UUID externalGroupId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        Member mockMember = Member.builder()
                .id(MemberId.from(memberId))
                .name("외부ID멤버")
                .build();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder().id(memberId).build();

        GroupMemberJpaEntity entity = GroupMemberJpaEntity.builder()
                .id(gmId)
                .group(GroupJpaEntity.builder().id(entityGroupId).build())
                .member(memberEntity)
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.GRADUATED)
                .build();

        when(memberPersistenceMapper.toDomain(memberEntity)).thenReturn(mockMember);

        GroupMember domain = mapper.toGroupMemberDomain(entity, externalGroupId);

        assertThat(domain.getGroupId().getValue()).isEqualTo(externalGroupId);
        assertThat(domain.getGroupId().getValue()).isNotEqualTo(entityGroupId);
        assertThat(domain.getMember().getName()).isEqualTo("외부ID멤버");
        assertThat(domain.getStatus()).isEqualTo(GroupMemberStatus.GRADUATED);
    }
}
