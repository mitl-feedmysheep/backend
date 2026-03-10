package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.in.query.dto.AdminMeResponse;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @Mock
    private MemberPort memberPort;

    @Mock
    private ChurchPort churchPort;

    @InjectMocks
    private MemberQueryService memberQueryService;

    private Member createMember(MemberId memberId, String name, String email) {
        return Member.builder()
                .id(memberId)
                .name(name)
                .email(email)
                .password("encoded-password")
                .sex(Sex.M)
                .birthday(LocalDate.of(1990, 1, 1))
                .phone("010-1234-5678")
                .build();
    }

    @Nested
    @DisplayName("getMemberById")
    class GetMemberById {

        @Test
        @DisplayName("멤버 ID로 조회 성공")
        void shouldReturnMemberWhenFound() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            Member member = createMember(memberId, "홍길동", "hong@test.com");

            when(memberPort.findById(memberId.getValue())).thenReturn(Optional.of(member));

            Member result = memberQueryService.getMemberById(memberId);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getEmail()).isEqualTo("hong@test.com");
            verify(memberPort).findById(memberId.getValue());
        }

        @Test
        @DisplayName("존재하지 않는 멤버 조회 시 RuntimeException 발생")
        void shouldThrowExceptionWhenNotFound() {
            MemberId memberId = MemberId.from(UUID.randomUUID());

            when(memberPort.findById(memberId.getValue())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> memberQueryService.getMemberById(memberId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("getMembersByGroupId")
    class GetMembersByGroupId {

        @Test
        @DisplayName("그룹 ID로 멤버 목록 조회")
        void shouldReturnMembersForGroupId() {
            UUID groupId = UUID.randomUUID();
            List<Member> members = List.of(
                    createMember(MemberId.from(UUID.randomUUID()), "멤버1", "m1@test.com"),
                    createMember(MemberId.from(UUID.randomUUID()), "멤버2", "m2@test.com")
            );

            when(memberPort.findMembersByGroupId(groupId)).thenReturn(members);

            List<Member> result = memberQueryService.getMembersByGroupId(groupId);

            assertThat(result).hasSize(2);
            verify(memberPort).findMembersByGroupId(groupId);
        }
    }

    @Nested
    @DisplayName("getAdminMyInfo")
    class GetAdminMyInfo {

        @Test
        @DisplayName("관리자 정보 조회 - ADMIN 역할")
        void shouldReturnAdminMeResponseWithRole() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            ChurchMember churchMember = ChurchMember.builder()
                    .id(ChurchMemberId.from(UUID.randomUUID()))
                    .memberId(memberId)
                    .churchId(churchId)
                    .role(ChurchRole.ADMIN)
                    .build();

            when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                    .thenReturn(churchMember);

            AdminMeResponse result = memberQueryService.getAdminMyInfo(memberId, churchId);

            assertThat(result).isNotNull();
            assertThat(result.getRole()).isEqualTo(ChurchRole.ADMIN);
        }

        @Test
        @DisplayName("관리자 정보 조회 - MEMBER 역할")
        void shouldReturnMemberRoleForRegularMember() {
            MemberId memberId = MemberId.from(UUID.randomUUID());
            ChurchId churchId = ChurchId.from(UUID.randomUUID());

            ChurchMember churchMember = ChurchMember.builder()
                    .id(ChurchMemberId.from(UUID.randomUUID()))
                    .memberId(memberId)
                    .churchId(churchId)
                    .role(ChurchRole.MEMBER)
                    .build();

            when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                    .thenReturn(churchMember);

            AdminMeResponse result = memberQueryService.getAdminMyInfo(memberId, churchId);

            assertThat(result.getRole()).isEqualTo(ChurchRole.MEMBER);
        }
    }

    @Nested
    @DisplayName("isPhoneAvailable")
    class IsPhoneAvailable {

        @Test
        @DisplayName("사용 가능한 전화번호 - true 반환")
        void shouldReturnTrueWhenPhoneNotTaken() {
            String phone = "010-9999-8888";

            when(memberPort.findByPhone(phone)).thenReturn(Optional.empty());

            assertThat(memberQueryService.isPhoneAvailable(phone)).isTrue();
        }

        @Test
        @DisplayName("이미 사용 중인 전화번호 - false 반환")
        void shouldReturnFalseWhenPhoneTaken() {
            String phone = "010-1234-5678";
            Member member = createMember(MemberId.from(UUID.randomUUID()), "기존회원", "existing@test.com");

            when(memberPort.findByPhone(phone)).thenReturn(Optional.of(member));

            assertThat(memberQueryService.isPhoneAvailable(phone)).isFalse();
        }
    }

    @Nested
    @DisplayName("isEmailAvailable")
    class IsEmailAvailable {

        @Test
        @DisplayName("사용 가능한 이메일 - true 반환")
        void shouldReturnTrueWhenEmailNotTaken() {
            String email = "new@test.com";

            when(memberPort.findByEmail(email)).thenReturn(Optional.empty());

            assertThat(memberQueryService.isEmailAvailable(email)).isTrue();
        }

        @Test
        @DisplayName("이미 사용 중인 이메일 - false 반환")
        void shouldReturnFalseWhenEmailTaken() {
            String email = "existing@test.com";
            Member member = createMember(MemberId.from(UUID.randomUUID()), "기존회원", email);

            when(memberPort.findByEmail(email)).thenReturn(Optional.of(member));

            assertThat(memberQueryService.isEmailAvailable(email)).isFalse();
        }
    }

    @Nested
    @DisplayName("verifyMemberByEmailAndName")
    class VerifyMemberByEmailAndName {

        @Test
        @DisplayName("이메일과 이름이 일치하는 멤버 존재 - true 반환")
        void shouldReturnTrueWhenMemberFound() {
            String email = "hong@test.com";
            String name = "홍길동";
            Member member = createMember(MemberId.from(UUID.randomUUID()), name, email);

            when(memberPort.findByEmailAndName(email, name)).thenReturn(Optional.of(member));

            assertThat(memberQueryService.verifyMemberByEmailAndName(email, name)).isTrue();
        }

        @Test
        @DisplayName("이메일과 이름이 일치하는 멤버 없음 - false 반환")
        void shouldReturnFalseWhenMemberNotFound() {
            String email = "unknown@test.com";
            String name = "없는사람";

            when(memberPort.findByEmailAndName(email, name)).thenReturn(Optional.empty());

            assertThat(memberQueryService.verifyMemberByEmailAndName(email, name)).isFalse();
        }
    }
}
