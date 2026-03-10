package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CompleteMediaUploadCommand;
import mitl.IntoTheHeaven.application.port.out.CloudPort;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaCommandServiceTest {

    @Mock
    private MediaPort mediaPort;

    @Mock
    private CloudPort cloudPort;

    @InjectMocks
    private MediaCommandService mediaCommandService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mediaCommandService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(mediaCommandService, "presignedUrlExpirationMinutes", 5);
        ReflectionTestUtils.setField(mediaCommandService, "maxThumbnailSize", 2097152L);
        ReflectionTestUtils.setField(mediaCommandService, "maxMediumSize", 5242880L);
    }

    @Nested
    @DisplayName("completeUpload - 업로드 완료 처리")
    class CompleteUploadTests {

        @Test
        @DisplayName("동일한 fileGroupId로 모든 미디어가 생성된다")
        void shouldCreateAllMediaWithSameFileGroupId() {
            UUID entityId = UUID.randomUUID();

            CompleteMediaUploadCommand command = CompleteMediaUploadCommand.builder()
                    .entityType(EntityType.GATHERING)
                    .entityId(entityId)
                    .uploads(List.of(
                            CompleteMediaUploadCommand.CompletedUploadInfo.builder()
                                    .mediaType(MediaType.THUMBNAIL)
                                    .publicUrl("https://cdn.example.com/thumb.jpg")
                                    .build(),
                            CompleteMediaUploadCommand.CompletedUploadInfo.builder()
                                    .mediaType(MediaType.MEDIUM)
                                    .publicUrl("https://cdn.example.com/medium.jpg")
                                    .build()))
                    .build();

            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            List<Media> result = mediaCommandService.completeUpload(command);

            assertThat(result).hasSize(2);

            ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
            verify(mediaPort, times(2)).save(captor.capture());
            List<Media> savedMedias = captor.getAllValues();

            String fileGroupId = savedMedias.get(0).getFileGroupId();
            assertThat(fileGroupId).isNotNull();
            assertThat(savedMedias.get(1).getFileGroupId()).isEqualTo(fileGroupId);

            assertThat(savedMedias.get(0).getMediaType()).isEqualTo(MediaType.THUMBNAIL);
            assertThat(savedMedias.get(1).getMediaType()).isEqualTo(MediaType.MEDIUM);
            assertThat(savedMedias.get(0).getEntityType()).isEqualTo(EntityType.GATHERING);
            assertThat(savedMedias.get(0).getEntityId()).isEqualTo(entityId);
        }

        @Test
        @DisplayName("각 미디어에 고유한 ID가 생성된다")
        void shouldGenerateUniqueIdsForEachMedia() {
            UUID entityId = UUID.randomUUID();

            CompleteMediaUploadCommand command = CompleteMediaUploadCommand.builder()
                    .entityType(EntityType.MEMBER)
                    .entityId(entityId)
                    .uploads(List.of(
                            CompleteMediaUploadCommand.CompletedUploadInfo.builder()
                                    .mediaType(MediaType.THUMBNAIL)
                                    .publicUrl("https://cdn.example.com/1.jpg")
                                    .build(),
                            CompleteMediaUploadCommand.CompletedUploadInfo.builder()
                                    .mediaType(MediaType.MEDIUM)
                                    .publicUrl("https://cdn.example.com/2.jpg")
                                    .build()))
                    .build();

            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            List<Media> result = mediaCommandService.completeUpload(command);

            assertThat(result.get(0).getId()).isNotEqualTo(result.get(1).getId());
        }

        @Test
        @DisplayName("제공된 URL이 그대로 저장된다")
        void shouldStoreProvidedUrlDirectly() {
            UUID entityId = UUID.randomUUID();
            String publicUrl = "https://cdn.example.com/specific-image.jpg";

            CompleteMediaUploadCommand command = CompleteMediaUploadCommand.builder()
                    .entityType(EntityType.GATHERING)
                    .entityId(entityId)
                    .uploads(List.of(
                            CompleteMediaUploadCommand.CompletedUploadInfo.builder()
                                    .mediaType(MediaType.MEDIUM)
                                    .publicUrl(publicUrl)
                                    .build()))
                    .build();

            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            List<Media> result = mediaCommandService.completeUpload(command);

            assertThat(result.get(0).getUrl()).isEqualTo(publicUrl);
        }
    }

    @Nested
    @DisplayName("deleteById - 미디어 ID로 삭제")
    class DeleteByIdTests {

        @Test
        @DisplayName("같은 fileGroup의 모든 미디어가 soft delete된다")
        void shouldSoftDeleteAllMediaInSameFileGroup() {
            MediaId mediaId = MediaId.from(UUID.randomUUID());
            String fileGroupId = UUID.randomUUID().toString();

            Media media1 = Media.builder()
                    .id(mediaId)
                    .mediaType(MediaType.THUMBNAIL)
                    .entityType(EntityType.GATHERING)
                    .entityId(UUID.randomUUID())
                    .fileGroupId(fileGroupId)
                    .url("url1")
                    .build();

            Media media2 = Media.builder()
                    .id(MediaId.from(UUID.randomUUID()))
                    .mediaType(MediaType.MEDIUM)
                    .entityType(EntityType.GATHERING)
                    .entityId(UUID.randomUUID())
                    .fileGroupId(fileGroupId)
                    .url("url2")
                    .build();

            when(mediaPort.findById(mediaId)).thenReturn(Optional.of(media1));
            when(mediaPort.findByFileGroupId(fileGroupId)).thenReturn(List.of(media1, media2));
            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            mediaCommandService.deleteById(mediaId);

            ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
            verify(mediaPort, times(2)).save(captor.capture());
            List<Media> deletedMedias = captor.getAllValues();

            assertThat(deletedMedias).allSatisfy(m ->
                    assertThat(m.getDeletedAt()).isNotNull());
        }

        @Test
        @DisplayName("미디어가 존재하지 않으면 RuntimeException이 발생한다")
        void shouldThrowWhenMediaNotFound() {
            MediaId mediaId = MediaId.from(UUID.randomUUID());
            when(mediaPort.findById(mediaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mediaCommandService.deleteById(mediaId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Media not found");

            verify(mediaPort, never()).save(any());
        }

        @Test
        @DisplayName("하나의 미디어만 있는 fileGroup도 삭제된다")
        void shouldDeleteSingleMediaInFileGroup() {
            MediaId mediaId = MediaId.from(UUID.randomUUID());
            String fileGroupId = UUID.randomUUID().toString();

            Media singleMedia = Media.builder()
                    .id(mediaId)
                    .mediaType(MediaType.THUMBNAIL)
                    .entityType(EntityType.MEMBER)
                    .entityId(UUID.randomUUID())
                    .fileGroupId(fileGroupId)
                    .url("url")
                    .build();

            when(mediaPort.findById(mediaId)).thenReturn(Optional.of(singleMedia));
            when(mediaPort.findByFileGroupId(fileGroupId)).thenReturn(List.of(singleMedia));
            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            mediaCommandService.deleteById(mediaId);

            verify(mediaPort, times(1)).save(any(Media.class));
        }
    }

    @Nested
    @DisplayName("deleteByEntity - 엔티티별 미디어 삭제")
    class DeleteByEntityTests {

        @Test
        @DisplayName("엔티티의 모든 미디어가 soft delete된다")
        void shouldSoftDeleteAllMediaForEntity() {
            UUID entityId = UUID.randomUUID();

            Media media1 = Media.builder()
                    .id(MediaId.from(UUID.randomUUID()))
                    .mediaType(MediaType.THUMBNAIL)
                    .entityType(EntityType.GATHERING)
                    .entityId(entityId)
                    .fileGroupId("fg1")
                    .url("url1")
                    .build();

            Media media2 = Media.builder()
                    .id(MediaId.from(UUID.randomUUID()))
                    .mediaType(MediaType.MEDIUM)
                    .entityType(EntityType.GATHERING)
                    .entityId(entityId)
                    .fileGroupId("fg2")
                    .url("url2")
                    .build();

            when(mediaPort.findByEntityId(entityId)).thenReturn(List.of(media1, media2));
            when(mediaPort.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));

            mediaCommandService.deleteByEntity(entityId);

            ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
            verify(mediaPort, times(2)).save(captor.capture());
            List<Media> deletedMedias = captor.getAllValues();

            assertThat(deletedMedias).allSatisfy(m ->
                    assertThat(m.getDeletedAt()).isNotNull());
        }

        @Test
        @DisplayName("해당 엔티티에 미디어가 없으면 아무 동작도 하지 않는다")
        void shouldNoOpWhenNoMediaForEntity() {
            UUID entityId = UUID.randomUUID();
            when(mediaPort.findByEntityId(entityId)).thenReturn(List.of());

            mediaCommandService.deleteByEntity(entityId);

            verify(mediaPort, never()).save(any());
        }
    }
}
