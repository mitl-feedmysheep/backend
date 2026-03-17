package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentCommandService")
class DepartmentCommandServiceTest {

    @Mock
    private DepartmentPort departmentPort;

    @InjectMocks
    private DepartmentCommandService departmentCommandService;

    private UUID churchUuid;
    private UUID departmentUuid;
    private UUID memberUuid;
    private ChurchId churchId;
    private DepartmentId departmentId;
    private MemberId memberId;

    @BeforeEach
    void setUp() {
        churchUuid = UUID.randomUUID();
        departmentUuid = UUID.randomUUID();
        memberUuid = UUID.randomUUID();
        churchId = ChurchId.from(churchUuid);
        departmentId = DepartmentId.from(departmentUuid);
        memberId = MemberId.from(memberUuid);
    }

    private Department createDepartment(String name) {
        return createDepartment(name, false);
    }

    private Department createDepartment(String name, boolean isDefault) {
        return Department.builder()
                .id(DepartmentId.from(departmentUuid))
                .name(name)
                .description(name + " 설명")
                .churchId(churchId)
                .isDefault(isDefault)
                .build();
    }

    private DepartmentMember createDepartmentMember(DepartmentRole role) {
        return DepartmentMember.builder()
                .id(DepartmentMemberId.from(UUID.randomUUID()))
                .departmentId(departmentId)
                .member(Member.builder()
                        .id(memberId)
                        .name("테스트 멤버")
                        .build())
                .role(role)
                .status(DepartmentMemberStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("createDepartment")
    class CreateDepartmentTests {

        @Test
        @DisplayName("부서를 생성한다")
        void createsDepartment() {
            Department expected = createDepartment("청년부");
            when(departmentPort.save(any(Department.class))).thenReturn(expected);

            Department result = departmentCommandService.createDepartment(churchId, "청년부", "청년부 설명");

            assertThat(result.getName()).isEqualTo("청년부");
            verify(departmentPort).save(any(Department.class));
        }
    }

    @Nested
    @DisplayName("updateDepartment")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("부서 이름과 설명을 수정한다")
        void updatesDepartment() {
            Department existing = createDepartment("청년부");
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.of(existing));
            when(departmentPort.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

            Department result = departmentCommandService.updateDepartment(departmentId, "청년부 수정", "새 설명");

            assertThat(result.getName()).isEqualTo("청년부 수정");
            assertThat(result.getDescription()).isEqualTo("새 설명");
        }

        @Test
        @DisplayName("존재하지 않는 부서를 수정하면 예외를 던진다")
        void throwsWhenNotFound() {
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentCommandService.updateDepartment(departmentId, "이름", "설명"))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("deleteDepartment")
    class DeleteDepartmentTests {

        @Test
        @DisplayName("멤버/그룹 0이면 삭제한다")
        void deletesWhenEmpty() {
            Department dept = createDepartment("청년부");
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.of(dept));
            when(departmentPort.countActiveMembersByDepartmentId(departmentUuid)).thenReturn(0L);
            when(departmentPort.countGroupsByDepartmentId(departmentUuid)).thenReturn(0L);

            departmentCommandService.deleteDepartment(departmentId);

            verify(departmentPort).deleteById(departmentUuid);
        }

        @Test
        @DisplayName("기본 부서는 삭제할 수 없다")
        void cannotDeleteDefaultDepartment() {
            Department defaultDept = createDepartment("전체", true);
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.of(defaultDept));

            assertThatThrownBy(() -> departmentCommandService.deleteDepartment(departmentId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("기본 부서는 삭제할 수 없습니다.");
            verify(departmentPort, never()).deleteById(any());
        }

        @Test
        @DisplayName("활동 멤버가 있으면 삭제할 수 없다")
        void cannotDeleteWithActiveMembers() {
            Department dept = createDepartment("청년부");
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.of(dept));
            when(departmentPort.countActiveMembersByDepartmentId(departmentUuid)).thenReturn(5L);

            assertThatThrownBy(() -> departmentCommandService.deleteDepartment(departmentId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("부서에 활동 중인 멤버가 있어 삭제할 수 없습니다.");
            verify(departmentPort, never()).deleteById(any());
        }

        @Test
        @DisplayName("소속 그룹이 있으면 삭제할 수 없다")
        void cannotDeleteWithGroups() {
            Department dept = createDepartment("청년부");
            when(departmentPort.findById(departmentUuid)).thenReturn(Optional.of(dept));
            when(departmentPort.countActiveMembersByDepartmentId(departmentUuid)).thenReturn(0L);
            when(departmentPort.countGroupsByDepartmentId(departmentUuid)).thenReturn(3L);

            assertThatThrownBy(() -> departmentCommandService.deleteDepartment(departmentId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("부서에 소속된 그룹이 있어 삭제할 수 없습니다.");
            verify(departmentPort, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("addMember")
    class AddMemberTests {

        @Test
        @DisplayName("부서에 멤버를 추가한다")
        void addsMember() {
            DepartmentMember expected = createDepartmentMember(DepartmentRole.MEMBER);
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.empty());
            when(departmentPort.saveDepartmentMember(any(), eq(departmentUuid), eq(memberUuid)))
                    .thenReturn(expected);

            DepartmentMember result = departmentCommandService.addMember(departmentId, memberId, DepartmentRole.MEMBER);

            assertThat(result.getRole()).isEqualTo(DepartmentRole.MEMBER);
        }

        @Test
        @DisplayName("이미 소속된 멤버를 추가하면 예외를 던진다")
        void throwsWhenAlreadyMember() {
            DepartmentMember existing = createDepartmentMember(DepartmentRole.MEMBER);
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> departmentCommandService.addMember(departmentId, memberId, DepartmentRole.MEMBER))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 부서에 소속된 멤버입니다.");
        }
    }

    @Nested
    @DisplayName("removeMember")
    class RemoveMemberTests {

        @Test
        @DisplayName("부서에서 멤버를 제거한다")
        void removesMember() {
            DepartmentMember dm = createDepartmentMember(DepartmentRole.MEMBER);
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.of(dm));

            departmentCommandService.removeMember(departmentId, memberId);

            verify(departmentPort).deleteDepartmentMember(dm.getId().getValue());
        }

        @Test
        @DisplayName("소속되지 않은 멤버를 제거하면 예외를 던진다")
        void throwsWhenNotMember() {
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentCommandService.removeMember(departmentId, memberId))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("changeMemberRole")
    class ChangeMemberRoleTests {

        @Test
        @DisplayName("멤버 역할을 변경한다")
        void changesRole() {
            DepartmentMember dm = createDepartmentMember(DepartmentRole.MEMBER);
            DepartmentMember updated = createDepartmentMember(DepartmentRole.LEADER);
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.of(dm));
            when(departmentPort.updateDepartmentMemberRole(dm.getId().getValue(), DepartmentRole.LEADER))
                    .thenReturn(updated);

            DepartmentMember result = departmentCommandService.changeMemberRole(departmentId, memberId, DepartmentRole.LEADER);

            assertThat(result.getRole()).isEqualTo(DepartmentRole.LEADER);
        }
    }

    @Nested
    @DisplayName("changeMemberStatus")
    class ChangeMemberStatusTests {

        @Test
        @DisplayName("멤버 상태를 졸업으로 변경한다")
        void graduatesMember() {
            DepartmentMember dm = createDepartmentMember(DepartmentRole.MEMBER);
            DepartmentMember graduated = DepartmentMember.builder()
                    .id(dm.getId())
                    .departmentId(departmentId)
                    .member(dm.getMember())
                    .role(DepartmentRole.MEMBER)
                    .status(DepartmentMemberStatus.GRADUATED)
                    .build();
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(departmentUuid, memberUuid))
                    .thenReturn(Optional.of(dm));
            when(departmentPort.updateDepartmentMemberStatus(dm.getId().getValue(), DepartmentMemberStatus.GRADUATED))
                    .thenReturn(graduated);

            DepartmentMember result = departmentCommandService.changeMemberStatus(
                    departmentId, memberId, DepartmentMemberStatus.GRADUATED);

            assertThat(result.getStatus()).isEqualTo(DepartmentMemberStatus.GRADUATED);
        }
    }
}
