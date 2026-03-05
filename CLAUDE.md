# Backend (IntoTheHeaven)

Java 17 / Spring Boot 3.3.5 / Gradle / MySQL

## 도메인 용어

| 용어 | 설명 |
|------|------|
| Member | 앱 사용자 (회원) |
| Church | 교회 |
| ChurchMember | 교회-회원 관계 (역할: MEMBER/LEADER/ADMIN/SUPER_ADMIN) |
| ChurchMemberRequest | 교회 가입 요청 (PENDING/ACCEPTED/DECLINED) |
| Group | 소그룹 (셀, 목장 등) |
| GroupMember | 소그룹 소속 멤버 |
| Gathering | 소그룹 모임 (출석, 기도제목 공유) |
| GatheringMember | 모임 참석 기록 |
| Prayer | 기도제목 (모임/심방/개인 3가지 컨텍스트) |
| Visit | 심방 (목회자 방문) |
| VisitMember | 심방 대상자 |
| Message | 회원 간 메시지 |
| Notification | 시스템 알림 |
| Event | 캘린더 이벤트 (다형성: entity_type + entity_id) |
| Media | 이미지/파일 (다형성: entity_type + entity_id, Cloudflare R2 저장) |
| EducationProgram | 교육 커리큘럼 (그룹당 1개) |
| EducationProgress | 교육 이수 기록 (유일하게 hard delete) |
| Verification | 이메일/SMS 인증 코드 |

## 아키텍처 (Hexagonal / DDD)

```
mitl.IntoTheHeaven/
├── adapter/in/web/          # controller, dto (Request/Response)
├── adapter/out/persistence/ # JpaEntity, Repository, Mapper
├── adapter/out/cloud/       # Cloudflare R2 스토리지
├── adapter/out/email/       # 이메일 발송
├── application/port/in/     # UseCase 인터페이스
├── application/port/out/    # Port 인터페이스
├── application/service/     # command/ (쓰기), query/ (읽기)
├── domain/model/            # 도메인 모델, 값 객체
├── domain/enums/            # 도메인 열거형
└── global/                  # config, security, aop, util
```

## 네이밍 컨벤션

| 대상 | 패턴 | 예시 |
|------|------|------|
| UseCase (In-Port) | `[Domain]QueryUseCase`, `[Domain]CommandUseCase` | `MemberQueryUseCase` |
| Service | `[Domain]QueryService`, `[Domain]CommandService` | `GroupCommandService` |
| Out-Port | `[Domain]Port` | `MemberPort` |
| Adapter | `[Domain]PersistenceAdapter` | `ChurchPersistenceAdapter` |
| Controller | `[Domain]Controller` | `GatheringController` |
| DTO | `[Action][Domain]Request/Response` | `CreateGatheringRequest` |
| JPA Entity | `[Domain]JpaEntity` | `MemberJpaEntity` |
| Mapper | `[Domain]PersistenceMapper` | `GroupPersistenceMapper` |

## DB 공통 패턴

- 모든 엔티티: UUID (CHAR(36)), soft delete (`deleted_at`), `@SQLRestriction("deleted_at is null")`
- BaseEntity: id, createdAt, updatedAt, deletedAt
- 예외: EducationProgress만 hard delete
- N+1 방지: `@BatchSize`, QueryDSL 사용
- FetchType.LAZY 기본

## API 규칙

- RESTful: 복수 명사 (`/members`, `/churches`, `/groups`)
- 계층 중첩: `/churches/{churchId}/groups`, `/groups/{groupId}/gatherings`
- 인증: JWT Bearer + `@AuthenticationPrincipal String memberId`
- 권한: `@RequireChurchRole` 어노테이션
- 검증: Controller에서 `@Valid`
- 응답: 200/201/204 성공, 4xx/5xx 에러

## 주요 의존성

- Spring Data JPA, Spring Security
- QueryDSL 5.0.0
- SpringDoc OpenAPI 2.5.0 (Swagger)
- jjwt 0.12.5
- AWS SDK (S3/SES)
- Lombok

## 환경변수 (Doppler)

- 프로덕션 환경변수는 Doppler로 관리, CI/CD에서 Doppler Service Token으로 주입
- 로컬 실행: `application-local.properties` 사용 (gitignored)

## 작업 시 필수 확인

- 쿼리 추가/변경 시 반드시 기존 인덱스를 확인하고, 필요하면 새 인덱스를 추가할 것
- 환경변수를 추가/삭제/수정하면 반드시 사용자에게 "Doppler에서 해당 환경변수를 변경해주세요"라고 알려줄 것

## 커밋 컨벤션

Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`
