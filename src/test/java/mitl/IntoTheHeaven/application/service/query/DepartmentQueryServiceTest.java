package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentQueryService")
class DepartmentQueryServiceTest {

    @Mock
    private DepartmentPort departmentPort;

    @InjectMocks
    private DepartmentQueryService departmentQueryService;

    private Department createDepartment(String name) {
        return Department.builder()
                .id(DepartmentId.from(UUID.randomUUID()))
                .name(name)
                .description(name + " 설명")
                .churchId(ChurchId.from(UUID.randomUUID()))
                .build();
    }

    private DepartmentMember createDepartmentMember(DepartmentRole role) {
        return DepartmentMember.builder()
                .id(DepartmentMemberId.from(UUID.randomUUID()))
                .departmentId(DepartmentId.from(UUID.randomUUID()))
                .member(Member.builder()
                        .id(MemberId.from(UUID.randomUUID()))
                        .name("테스트 멤버")
                        .build())
                .role(role)
                .status(DepartmentMemberStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("getDepartmentsByChurchId")
    class GetDepartmentsByChurchIdTests {

        @Test
        @DisplayName("교회의 부서 목록을 반환한다")
        void returnsDepartmentsForChurch() {
            UUID churchUuid = UUID.randomUUID();
            List<Department> departments = List.of(
                    createDepartment("청년부"),
                    createDepartment("장년부"));
            when(departmentPort.findByChurchId(churchUuid)).thenReturn(departments);

            List<Department> result = departmentQueryService.getDepartmentsByChurchId(ChurchId.from(churchUuid));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("청년부");
            assertThat(result.get(1).getName()).isEqualTo("장년부");
        }

        @Test
        @DisplayName("부서가 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoDepartments() {
            UUID churchUuid = UUID.randomUUID();
            when(departmentPort.findByChurchId(churchUuid)).thenReturn(List.of());

            List<Department> result = departmentQueryService.getDepartmentsByChurchId(ChurchId.from(churchUuid));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getDepartmentById")
    class GetDepartmentByIdTests {

        @Test
        @DisplayName("부서를 찾으면 반환한다")
        void returnsDepartment() {
            UUID deptUuid = UUID.randomUUID();
            Department department = createDepartment("청년부");
            when(departmentPort.findById(deptUuid)).thenReturn(Optional.of(department));

            Department result = departmentQueryService.getDepartmentById(DepartmentId.from(deptUuid));

            assertThat(result.getName()).isEqualTo("청년부");
        }

        @Test
        @DisplayName("부서를 찾지 못하면 예외를 던진다")
        void throwsWhenNotFound() {
            UUID deptUuid = UUID.randomUUID();
            when(departmentPort.findById(deptUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentQueryService.getDepartmentById(DepartmentId.from(deptUuid)))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("getActiveDepartmentMembers")
    class GetActiveDepartmentMembersTests {

        @Test
        @DisplayName("활동 중인 부서 멤버 목록을 반환한다")
        void returnsActiveMembers() {
            UUID deptUuid = UUID.randomUUID();
            List<DepartmentMember> members = List.of(
                    createDepartmentMember(DepartmentRole.ADMIN),
                    createDepartmentMember(DepartmentRole.MEMBER));
            when(departmentPort.findActiveDepartmentMembersByDepartmentId(deptUuid)).thenReturn(members);

            List<DepartmentMember> result = departmentQueryService.getActiveDepartmentMembers(DepartmentId.from(deptUuid));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getRole()).isEqualTo(DepartmentRole.ADMIN);
        }
    }

    @Nested
    @DisplayName("getCurrentRole")
    class GetCurrentRoleTests {

        @Test
        @DisplayName("멤버의 부서 역할을 반환한다")
        void returnsRole() {
            UUID membUuid = UUID.randomUUID();
            UUID deptUuid = UUID.randomUUID();
            DepartmentMember dm = createDepartmentMember(DepartmentRole.LEADER);
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(deptUuid, membUuid))
                    .thenReturn(Optional.of(dm));

            DepartmentRole result = departmentQueryService.getCurrentRole(
                    MemberId.from(membUuid), DepartmentId.from(deptUuid));

            assertThat(result).isEqualTo(DepartmentRole.LEADER);
        }

        @Test
        @DisplayName("부서 멤버가 아니면 null을 반환한다")
        void returnsNullWhenNotMember() {
            UUID membUuid = UUID.randomUUID();
            UUID deptUuid = UUID.randomUUID();
            when(departmentPort.findDepartmentMemberByDepartmentIdAndMemberId(deptUuid, membUuid))
                    .thenReturn(Optional.empty());

            DepartmentRole result = departmentQueryService.getCurrentRole(
                    MemberId.from(membUuid), DepartmentId.from(deptUuid));

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getMyDepartments")
    class GetMyDepartmentsTests {

        @Test
        @DisplayName("멤버의 교회 내 부서 목록을 반환한다")
        void returnsMyDepartments() {
            UUID membUuid = UUID.randomUUID();
            UUID churchUuid = UUID.randomUUID();
            List<DepartmentMember> dms = List.of(
                    createDepartmentMember(DepartmentRole.ADMIN),
                    createDepartmentMember(DepartmentRole.MEMBER));
            when(departmentPort.findDepartmentMembersByMemberIdAndChurchId(membUuid, churchUuid))
                    .thenReturn(dms);

            List<DepartmentMember> result = departmentQueryService.getMyDepartments(
                    MemberId.from(membUuid), ChurchId.from(churchUuid));

            assertThat(result).hasSize(2);
        }
    }
}
