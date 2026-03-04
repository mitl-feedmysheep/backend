package mitl.IntoTheHeaven.application.service.query;

import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChurchQueryServiceSearchTest {

    @Mock
    private ChurchPort churchPort;

    @InjectMocks
    private ChurchQueryService churchQueryService;

    private final UUID memberUuid = UUID.randomUUID();
    private final UUID churchUuid = UUID.randomUUID();
    private final MemberId memberId = MemberId.from(memberUuid);
    private final ChurchId churchId = ChurchId.from(churchUuid);

    private ChurchMember buildChurchMember(ChurchRole role) {
        return ChurchMember.builder()
                .id(ChurchMemberId.from(UUID.randomUUID()))
                .memberId(memberId)
                .churchId(churchId)
                .role(role)
                .build();
    }

    @Test
    @DisplayName("church_member.role이 LEADER 이상이면 elevated access true")
    void hasElevatedSearchAccess_churchLeader() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.LEADER));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isTrue();
    }

    @Test
    @DisplayName("church_member.role이 ADMIN이면 elevated access true")
    void hasElevatedSearchAccess_churchAdmin() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.ADMIN));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isTrue();
    }

    @Test
    @DisplayName("church_member.role이 SUPER_ADMIN이면 elevated access true")
    void hasElevatedSearchAccess_churchSuperAdmin() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.SUPER_ADMIN));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isTrue();
    }

    @Test
    @DisplayName("church_member.role이 MEMBER지만 group에서 LEADER면 elevated access true")
    void hasElevatedSearchAccess_groupLeader() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.MEMBER));
        when(churchPort.findGroupMemberRolesByMemberIdAndChurchId(memberUuid, churchUuid))
                .thenReturn(List.of(GroupMemberRole.LEADER));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isTrue();
    }

    @Test
    @DisplayName("church_member.role이 MEMBER지만 group에서 SUB_LEADER면 elevated access true")
    void hasElevatedSearchAccess_groupSubLeader() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.MEMBER));
        when(churchPort.findGroupMemberRolesByMemberIdAndChurchId(memberUuid, churchUuid))
                .thenReturn(List.of(GroupMemberRole.SUB_LEADER));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isTrue();
    }

    @Test
    @DisplayName("church_member.role이 MEMBER이고 group에서도 MEMBER면 elevated access false")
    void hasElevatedSearchAccess_regularMember() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.MEMBER));
        when(churchPort.findGroupMemberRolesByMemberIdAndChurchId(memberUuid, churchUuid))
                .thenReturn(List.of(GroupMemberRole.MEMBER));

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isFalse();
    }

    @Test
    @DisplayName("church_member.role이 MEMBER이고 group이 없으면 elevated access false")
    void hasElevatedSearchAccess_noGroups() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(buildChurchMember(ChurchRole.MEMBER));
        when(churchPort.findGroupMemberRolesByMemberIdAndChurchId(memberUuid, churchUuid))
                .thenReturn(List.of());

        assertThat(churchQueryService.hasElevatedSearchAccess(memberId, churchId)).isFalse();
    }

    @Test
    @DisplayName("교회 멤버가 아니면 예외 발생")
    void hasElevatedSearchAccess_notMember() {
        when(churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId))
                .thenReturn(null);

        assertThatThrownBy(() -> churchQueryService.hasElevatedSearchAccess(memberId, churchId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
