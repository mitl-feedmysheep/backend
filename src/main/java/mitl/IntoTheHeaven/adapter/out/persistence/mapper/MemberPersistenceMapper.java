package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceMapper {

    public Member toDomain(MemberJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Member.builder()
                .id(new MemberId(entity.getId()))
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .sex(entity.getSex())
                .birthday(entity.getBirthday())
                .phone(entity.getPhone())
                .profileUrl(entity.getProfileUrl())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public MemberJpaEntity toEntity(Member domain) {
        if (domain == null) {
            return null;
        }
        return MemberJpaEntity.builder()
                .id(domain.getId().getValue())
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .sex(domain.getSex())
                .birthday(domain.getBirthday())
                .phone(domain.getPhone())
                .profileUrl(domain.getProfileUrl())
                .address(domain.getAddress())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
} 