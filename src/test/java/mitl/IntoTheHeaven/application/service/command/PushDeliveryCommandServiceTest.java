package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushDeliveryCommandService")
class PushDeliveryCommandServiceTest {

    @Mock
    private PushDeliveryLogPort pushDeliveryLogPort;

    @InjectMocks
    private PushDeliveryCommandService service;

    @Nested
    @DisplayName("recordClick")
    class RecordClick {

        @Test
        @DisplayName("클릭 시각과 함께 markClicked를 호출한다")
        void marksClicked() {
            UUID id = UUID.randomUUID();
            when(pushDeliveryLogPort.markClicked(eq(id), any())).thenReturn(true);

            service.recordClick(id);

            verify(pushDeliveryLogPort).markClicked(eq(id), any());
        }

        @Test
        @DisplayName("이미 클릭됐거나 없는 ID여도 예외 없이 끝난다")
        void alreadyClickedOrMissing_noError() {
            UUID id = UUID.randomUUID();
            when(pushDeliveryLogPort.markClicked(eq(id), any())).thenReturn(false);

            assertThatCode(() -> service.recordClick(id)).doesNotThrowAnyException();
        }
    }
}
