package mitl.IntoTheHeaven.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ChurchRoleTest {

    @Nested
    @DisplayName("SUPER_ADMIN 권한 검증")
    class SuperAdminPermission {

        @ParameterizedTest
        @EnumSource(ChurchRole.class)
        @DisplayName("SUPER_ADMIN은 모든 역할에 대해 권한을 가진다")
        void hasPermissionOverAllRoles(ChurchRole role) {
            assertThat(ChurchRole.SUPER_ADMIN.hasPermissionOver(role)).isTrue();
        }
    }

    @Nested
    @DisplayName("ADMIN 권한 검증")
    class AdminPermission {

        @Test
        @DisplayName("ADMIN은 ADMIN에 대해 권한을 가진다")
        void hasPermissionOverAdmin() {
            assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.ADMIN)).isTrue();
        }

        @Test
        @DisplayName("ADMIN은 LEADER에 대해 권한을 가진다")
        void hasPermissionOverLeader() {
            assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.LEADER)).isTrue();
        }

        @Test
        @DisplayName("ADMIN은 MEMBER에 대해 권한을 가진다")
        void hasPermissionOverMember() {
            assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.MEMBER)).isTrue();
        }

        @Test
        @DisplayName("ADMIN은 SUPER_ADMIN에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverSuperAdmin() {
            assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.SUPER_ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("LEADER 권한 검증")
    class LeaderPermission {

        @Test
        @DisplayName("LEADER는 LEADER에 대해 권한을 가진다")
        void hasPermissionOverLeader() {
            assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.LEADER)).isTrue();
        }

        @Test
        @DisplayName("LEADER는 MEMBER에 대해 권한을 가진다")
        void hasPermissionOverMember() {
            assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.MEMBER)).isTrue();
        }

        @Test
        @DisplayName("LEADER는 ADMIN에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverAdmin() {
            assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.ADMIN)).isFalse();
        }

        @Test
        @DisplayName("LEADER는 SUPER_ADMIN에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverSuperAdmin() {
            assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.SUPER_ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("MEMBER 권한 검증")
    class MemberPermission {

        @Test
        @DisplayName("MEMBER는 MEMBER에 대해서만 권한을 가진다")
        void hasPermissionOverMember() {
            assertThat(ChurchRole.MEMBER.hasPermissionOver(ChurchRole.MEMBER)).isTrue();
        }

        @Test
        @DisplayName("MEMBER는 LEADER에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverLeader() {
            assertThat(ChurchRole.MEMBER.hasPermissionOver(ChurchRole.LEADER)).isFalse();
        }

        @Test
        @DisplayName("MEMBER는 ADMIN에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverAdmin() {
            assertThat(ChurchRole.MEMBER.hasPermissionOver(ChurchRole.ADMIN)).isFalse();
        }

        @Test
        @DisplayName("MEMBER는 SUPER_ADMIN에 대해 권한을 가지지 못한다")
        void doesNotHavePermissionOverSuperAdmin() {
            assertThat(ChurchRole.MEMBER.hasPermissionOver(ChurchRole.SUPER_ADMIN)).isFalse();
        }
    }

    @ParameterizedTest
    @EnumSource(ChurchRole.class)
    @DisplayName("모든 역할은 자기 자신에 대해 권한을 가진다")
    void sameRoleHasPermissionOverItself(ChurchRole role) {
        assertThat(role.hasPermissionOver(role)).isTrue();
    }

    @Test
    @DisplayName("역할 레벨 순서가 MEMBER < LEADER < ADMIN < SUPER_ADMIN이다")
    void roleLevelOrderIsCorrect() {
        assertThat(ChurchRole.MEMBER.hasPermissionOver(ChurchRole.LEADER)).isFalse();
        assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.ADMIN)).isFalse();
        assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.SUPER_ADMIN)).isFalse();

        assertThat(ChurchRole.SUPER_ADMIN.hasPermissionOver(ChurchRole.ADMIN)).isTrue();
        assertThat(ChurchRole.ADMIN.hasPermissionOver(ChurchRole.LEADER)).isTrue();
        assertThat(ChurchRole.LEADER.hasPermissionOver(ChurchRole.MEMBER)).isTrue();
    }
}
