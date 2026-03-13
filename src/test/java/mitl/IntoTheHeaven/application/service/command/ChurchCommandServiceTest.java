package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequestId;
import mitl.IntoTheHeaven.domain.model.MemberId;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChurchCommandServiceTest {

    @Mock
    private ChurchPort churchPort;

    @InjectMocks
    private ChurchCommandService churchCommandService;

    private UUID memberUuid;
    private UUID churchUuid;
    private MemberId memberId;
    private ChurchId churchId;
    private Church church;

    @BeforeEach
    void setUp() {
        memberUuid = UUID.randomUUID();
        churchUuid = UUID.randomUUID();
        memberId = MemberId.from(memberUuid);
        churchId = ChurchId.from(churchUuid);
        church = Church.builder()
                .id(churchId)
                .name("은혜교회")
                .build();
    }

    @Nested
    @DisplayName("createJoinRequest - 교회 가입 신청")
    class CreateJoinRequestTests {

        @Test
        @DisplayName("정상적으로 가입 신청을 생성한다")
        void shouldCreateJoinRequestSuccessfully() {
            when(churchPort.findById(churchUuid)).thenReturn(church);
            when(churchPort.findPendingJoinRequest(memberUuid, churchUuid)).thenReturn(Optional.empty());
            when(churchPort.saveJoinRequest(any(ChurchMemberRequest.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ChurchMemberRequest result = churchCommandService.createJoinRequest(memberId, churchId, null);

            ArgumentCaptor<ChurchMemberRequest> captor = ArgumentCaptor.forClass(ChurchMemberRequest.class);
            verify(churchPort).saveJoinRequest(captor.capture());
            ChurchMemberRequest saved = captor.getValue();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getMemberId()).isEqualTo(memberId);
            assertThat(saved.getChurchId()).isEqualTo(churchId);
            assertThat(saved.getStatus()).isEqualTo(RequestStatus.PENDING);
            assertThat(saved.getChurchName()).isEqualTo("은혜교회");
        }

        @Test
        @DisplayName("교회가 존재하지 않으면 IllegalArgumentException이 발생한다")
        void shouldThrowWhenChurchNotFound() {
            when(churchPort.findById(churchUuid)).thenReturn(null);

            assertThatThrownBy(() -> churchCommandService.createJoinRequest(memberId, churchId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Church not found");

            verify(churchPort, never()).saveJoinRequest(any());
        }

        @Test
        @DisplayName("이미 대기 중인 가입 신청이 있으면 IllegalStateException이 발생한다")
        void shouldThrowWhenPendingRequestAlreadyExists() {
            ChurchMemberRequest existing = ChurchMemberRequest.builder()
                    .id(ChurchMemberRequestId.from(UUID.randomUUID()))
                    .memberId(memberId)
                    .churchId(churchId)
                    .status(RequestStatus.PENDING)
                    .churchName("은혜교회")
                    .build();

            when(churchPort.findById(churchUuid)).thenReturn(church);
            when(churchPort.findPendingJoinRequest(memberUuid, churchUuid)).thenReturn(Optional.of(existing));

            assertThatThrownBy(() -> churchCommandService.createJoinRequest(memberId, churchId, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("A pending join request already exists for this church");

            verify(churchPort, never()).saveJoinRequest(any());
        }
    }
}
