package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateReportCommand;
import mitl.IntoTheHeaven.application.port.out.NotificationPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.ReportCommentPort;
import mitl.IntoTheHeaven.application.port.out.ReportPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Notification;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportId;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportCommandService")
class ReportCommandServiceTest {

    @Mock
    private ReportPort reportPort;

    @Mock
    private ReportCommentPort reportCommentPort;

    @Mock
    private NotificationPort notificationPort;

    @Mock
    private PushSubscriptionPort pushSubscriptionPort;

    @Mock
    private WebPushPort webPushPort;

    @InjectMocks
    private ReportCommandService service;

    private UUID adminMemberIdValue;
    private MemberId adminMemberId;
    private MemberId reporterId;

    @BeforeEach
    void setUp() {
        adminMemberIdValue = UUID.randomUUID();
        adminMemberId = MemberId.from(adminMemberIdValue);
        reporterId = MemberId.from(UUID.randomUUID());
        ReflectionTestUtils.setField(service, "systemAdminMemberId", adminMemberIdValue.toString());

        lenient().when(reportPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(pushSubscriptionPort.findByMemberId(any())).thenReturn(List.of());
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("일반 유저가 제출하면 관리자에게 알림과 푸시가 간다")
        void create_byRegularUser_notifiesAdmin() {
            CreateReportCommand command = new CreateReportCommand(reporterId, ReportType.BUG, "화면이 멈춰요");
            PushSubscription subscription = PushSubscription.builder()
                    .memberId(adminMemberId)
                    .endpoint("https://push.example.com/admin")
                    .build();
            when(pushSubscriptionPort.findByMemberId(adminMemberId)).thenReturn(List.of(subscription));
            when(webPushPort.send(any(), any())).thenReturn(WebPushPort.SendResult.SUCCESS);

            ReportId reportId = service.create(command);

            assertThat(reportId).isNotNull();

            ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
            verify(reportPort).save(reportCaptor.capture());
            assertThat(reportCaptor.getValue().getStatus()).isEqualTo(ReportStatus.RECEIVED);
            assertThat(reportCaptor.getValue().getReporterId()).isEqualTo(reporterId);

            ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationPort).save(notificationCaptor.capture());
            assertThat(notificationCaptor.getValue().getReceiverId()).isEqualTo(adminMemberId);

            verify(webPushPort).send(eq(subscription), any());
        }

        @Test
        @DisplayName("관리자 본인이 제출해도 관리자에게 알림과 푸시가 간다")
        void create_bySystemAdmin_stillNotifiesAdmin() {
            CreateReportCommand command = new CreateReportCommand(adminMemberId, ReportType.QUESTION, "테스트");
            PushSubscription subscription = PushSubscription.builder()
                    .memberId(adminMemberId)
                    .endpoint("https://push.example.com/admin")
                    .build();
            when(pushSubscriptionPort.findByMemberId(adminMemberId)).thenReturn(List.of(subscription));
            when(webPushPort.send(any(), any())).thenReturn(WebPushPort.SendResult.SUCCESS);

            service.create(command);

            ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationPort).save(notificationCaptor.capture());
            assertThat(notificationCaptor.getValue().getReceiverId()).isEqualTo(adminMemberId);
            verify(webPushPort).send(eq(subscription), any());
        }
    }

    @Nested
    @DisplayName("addComment")
    class AddComment {

        private Report report;

        @BeforeEach
        void setUpReport() {
            report = Report.builder()
                    .id(ReportId.from(UUID.randomUUID()))
                    .reporterId(reporterId)
                    .type(ReportType.BUG)
                    .content("내용")
                    .status(ReportStatus.RECEIVED)
                    .build();
            lenient().when(reportPort.findById(report.getId().getValue())).thenReturn(java.util.Optional.of(report));
        }

        @Test
        @DisplayName("관리자가 댓글을 달면 리포터에게만 알림이 간다")
        void addComment_byAdmin_notifiesReporter() {
            service.addComment(adminMemberId, report.getId(), "확인했어요");

            verify(reportCommentPort).save(any());
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationPort).save(captor.capture());
            assertThat(captor.getValue().getReceiverId()).isEqualTo(reporterId);
        }

        @Test
        @DisplayName("리포터가 댓글을 달면 관리자에게만 알림이 간다")
        void addComment_byReporter_notifiesAdmin() {
            service.addComment(reporterId, report.getId(), "추가 정보요");

            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationPort).save(captor.capture());
            assertThat(captor.getValue().getReceiverId()).isEqualTo(adminMemberId);
        }

        @Test
        @DisplayName("리포터도 관리자도 아니면 403을 던진다")
        void addComment_byStranger_throwsForbidden() {
            MemberId stranger = MemberId.from(UUID.randomUUID());

            assertThatThrownBy(() -> service.addComment(stranger, report.getId(), "몰래 댓글"))
                    .isInstanceOf(AccessDeniedException.class);

            verify(reportCommentPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        private Report report;

        @BeforeEach
        void setUpReport() {
            report = Report.builder()
                    .id(ReportId.from(UUID.randomUUID()))
                    .reporterId(reporterId)
                    .type(ReportType.BUG)
                    .content("내용")
                    .status(ReportStatus.RECEIVED)
                    .build();
        }

        @Test
        @DisplayName("관리자가 상태를 바꾸면 알림 없이 상태만 갱신된다")
        void updateStatus_byAdmin_updatesWithoutNotification() {
            when(reportPort.findById(report.getId().getValue())).thenReturn(java.util.Optional.of(report));

            service.updateStatus(adminMemberId, report.getId(), ReportStatus.IN_PROGRESS);

            verify(reportPort).updateStatus(report.getId().getValue(), ReportStatus.IN_PROGRESS);
            verifyNoInteractions(notificationPort);
            verifyNoInteractions(webPushPort);
        }

        @Test
        @DisplayName("관리자가 아니면 403을 던진다")
        void updateStatus_byNonAdmin_throwsForbidden() {
            assertThatThrownBy(() -> service.updateStatus(reporterId, report.getId(), ReportStatus.RESOLVED))
                    .isInstanceOf(AccessDeniedException.class);

            verify(reportPort, never()).updateStatus(any(), any());
        }
    }
}
