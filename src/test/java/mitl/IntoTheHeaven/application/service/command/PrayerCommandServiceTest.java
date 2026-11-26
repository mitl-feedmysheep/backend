package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrayerCommandService")
class PrayerCommandServiceTest {

    @Mock
    private PrayerPort prayerPort;

    @InjectMocks
    private PrayerCommandService prayerCommandService;

    private UUID prayerUuid;
    private PrayerId prayerId;
    private MemberId ownerId;
    private MemberId otherId;
    private Prayer myPrayer;
    private Prayer gatheringPrayer;

    @BeforeEach
    void setUp() {
        prayerUuid = UUID.randomUUID();
        prayerId = PrayerId.from(prayerUuid);
        ownerId = MemberId.from(UUID.randomUUID());
        otherId = MemberId.from(UUID.randomUUID());
        myPrayer = Prayer.builder()
                .id(prayerId)
                .memberId(ownerId)
                .prayerRequest("기도제목")
                .isAnswered(false)
                .build();
        gatheringPrayer = myPrayer.toBuilder()
                .gatheringMemberId(GatheringMemberId.from(UUID.randomUUID()))
                .build();
    }

    @Nested
    @DisplayName("createPersonal - 나의 기도 작성")
    class CreatePersonal {

        @Test
        @DisplayName("모임/심방 연결 없이 본인 소유의 미응답 기도제목을 만든다 (앞뒤 공백 제거)")
        void createsPersonalPrayer() {
            when(prayerPort.save(any(Prayer.class))).thenAnswer(inv -> inv.getArgument(0));

            PrayerId id = prayerCommandService.createPersonal(ownerId, "  부모님 건강  ");

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            Prayer saved = captor.getValue();
            assertThat(id).isEqualTo(saved.getId());
            assertThat(saved.getMemberId()).isEqualTo(ownerId);
            assertThat(saved.getPrayerRequest()).isEqualTo("부모님 건강");
            assertThat(saved.isPersonal()).isTrue();
            assertThat(saved.isAnswered()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateContent - 나의 기도 수정")
    class UpdateContent {

        @Test
        @DisplayName("본인의 나의 기도 내용을 수정한다")
        void ownerCanEdit() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(myPrayer));

            prayerCommandService.updateContent(ownerId, prayerId, "새 내용");

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().getPrayerRequest()).isEqualTo("새 내용");
        }

        @Test
        @DisplayName("다른 사람의 나의 기도는 수정할 수 없다")
        void otherCannotEdit() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(myPrayer));

            assertThatThrownBy(() -> prayerCommandService.updateContent(otherId, prayerId, "x"))
                    .isInstanceOf(AccessDeniedException.class);
            verify(prayerPort, never()).save(any());
        }

        @Test
        @DisplayName("모임 기도제목은 이 API로 수정할 수 없다")
        void gatheringPrayerCannotBeEdited() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(gatheringPrayer));

            assertThatThrownBy(() -> prayerCommandService.updateContent(ownerId, prayerId, "x"))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(prayerPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete - 기도제목 삭제")
    class DeleteTests {

        @Test
        @DisplayName("본인의 나의 기도를 soft delete한다")
        void ownerDeletesPersonal() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(myPrayer));

            prayerCommandService.delete(ownerId, prayerId);

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("다른 사람의 나의 기도는 삭제할 수 없다")
        void otherCannotDeletePersonal() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(myPrayer));

            assertThatThrownBy(() -> prayerCommandService.delete(otherId, prayerId))
                    .isInstanceOf(AccessDeniedException.class);
            verify(prayerPort, never()).save(any());
        }

        @Test
        @DisplayName("모임 기도제목은 리더가 모임 화면에서 지울 수 있도록 기존처럼 삭제된다")
        void gatheringPrayerKeepsExistingBehavior() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(gatheringPrayer));

            prayerCommandService.delete(otherId, prayerId);

            verify(prayerPort).save(any(Prayer.class));
        }

        @Test
        @DisplayName("기도제목이 없으면 예외가 발생한다")
        void notFound() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> prayerCommandService.delete(ownerId, prayerId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Prayer not found");
            verify(prayerPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateAnswered - 기도 응답 상태 변경")
    class UpdateAnsweredTests {

        @Test
        @DisplayName("본인이면 응답 상태를 바꾼다")
        void ownerToggles() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(gatheringPrayer));

            prayerCommandService.updateAnswered(ownerId, prayerId, true);

            ArgumentCaptor<Prayer> captor = ArgumentCaptor.forClass(Prayer.class);
            verify(prayerPort).save(captor.capture());
            assertThat(captor.getValue().isAnswered()).isTrue();
        }

        @Test
        @DisplayName("본인이 아니면 바꿀 수 없다")
        void otherCannotToggle() {
            when(prayerPort.findById(prayerUuid)).thenReturn(Optional.of(gatheringPrayer));

            assertThatThrownBy(() -> prayerCommandService.updateAnswered(otherId, prayerId, true))
                    .isInstanceOf(AccessDeniedException.class);
            verify(prayerPort, never()).save(any());
        }
    }
}
