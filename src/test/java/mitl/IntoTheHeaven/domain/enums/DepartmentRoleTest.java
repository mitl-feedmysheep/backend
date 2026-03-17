package mitl.IntoTheHeaven.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DepartmentRole")
class DepartmentRoleTest {

    @Nested
    @DisplayName("ADMIN 권한")
    class AdminPermission {

        @ParameterizedTest
        @EnumSource(DepartmentRole.class)
        @DisplayName("ADMIN은 모든 역할 이상의 권한을 가진다")
        void adminHasPermissionOverAll(DepartmentRole role) {
            assertThat(DepartmentRole.ADMIN.hasPermissionOver(role)).isTrue();
        }
    }

    @Nested
    @DisplayName("LEADER 권한")
    class LeaderPermission {

        @Test
        @DisplayName("LEADER는 MEMBER 이상 권한을 가진다")
        void leaderHasPermissionOverMember() {
            assertThat(DepartmentRole.LEADER.hasPermissionOver(DepartmentRole.MEMBER)).isTrue();
        }

        @Test
        @DisplayName("LEADER는 자기 자신 이상 권한을 가진다")
        void leaderHasPermissionOverSelf() {
            assertThat(DepartmentRole.LEADER.hasPermissionOver(DepartmentRole.LEADER)).isTrue();
        }

        @Test
        @DisplayName("LEADER는 ADMIN 권한이 없다")
        void leaderCannotManageAdmin() {
            assertThat(DepartmentRole.LEADER.hasPermissionOver(DepartmentRole.ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("MEMBER 권한")
    class MemberPermission {

        @Test
        @DisplayName("MEMBER는 자기 자신 이상 권한만 가진다")
        void memberHasPermissionOverSelf() {
            assertThat(DepartmentRole.MEMBER.hasPermissionOver(DepartmentRole.MEMBER)).isTrue();
        }

        @Test
        @DisplayName("MEMBER는 LEADER 권한이 없다")
        void memberCannotManageLeader() {
            assertThat(DepartmentRole.MEMBER.hasPermissionOver(DepartmentRole.LEADER)).isFalse();
        }

        @Test
        @DisplayName("MEMBER는 ADMIN 권한이 없다")
        void memberCannotManageAdmin() {
            assertThat(DepartmentRole.MEMBER.hasPermissionOver(DepartmentRole.ADMIN)).isFalse();
        }
    }
}
