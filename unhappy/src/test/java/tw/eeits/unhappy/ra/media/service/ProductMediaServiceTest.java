package tw.eeits.unhappy.ra.media.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import tw.eeits.unhappy.ra.media.model.MediaType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.storage.StorageException;
import tw.eeits.unhappy.ra.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class ProductMediaServiceTest {

    @Mock
    StorageService storageService;

    @Mock
    ProductMediaRepository mediaRepo;

    @InjectMocks
    ProductMediaService mediaService;

    /* ---------- upload() ---------- */

    @Nested
    @DisplayName("upload()")
    class Upload {

        @Test
        @DisplayName("成功上傳 JPG，回傳 ProductMedia")
        void givenJpg_whenUpload_thenReturnEntity() throws Exception {

            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                    "file", "demo.jpg", "image/jpeg",
                    "dummy".getBytes(StandardCharsets.UTF_8));

            String expectedUrl =
                    "https://blob/account/42/uuid-demo.jpg";

            when(storageService.upload(
                    anyString(),                       // path
                    any(InputStream.class),            // inputStream
                    eq(file.getSize()),                // size
                    eq(file.getContentType())))        // contentType
                .thenReturn(expectedUrl);

            when(mediaRepo.save(any(ProductMedia.class)))
                .thenAnswer(inv -> inv.getArgument(0));

            // Act
            ProductMedia saved = mediaService.upload(
                    42, file, true, null);             // main=true, order=null

            // Assert
            assertThat(saved.getProductId()).isEqualTo(42);
            assertThat(saved.getMediaUrl()).isEqualTo(expectedUrl);
            assertThat(saved.getIsMain()).isTrue();
            verify(storageService).upload(anyString(),
                                        any(InputStream.class),
                                        eq(file.getSize()),
                                        eq(file.getContentType()));
            verify(mediaRepo).save(any(ProductMedia.class));
        }

         // oversize 檔案丟 IllegalArgumentException
        @Test
        @DisplayName("檔案超過 5 MB 時丟 IllegalArgumentException")
        void givenOversizeFile_whenUpload_thenThrow() {
            byte[] big = new byte[6 * 1024 * 1024];            // 6 MB
            MockMultipartFile file = new MockMultipartFile(
                    "file", "big.jpg", "image/jpeg", big);

            assertThatThrownBy(() ->
                    mediaService.upload(42, file, false, null))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(storageService, mediaRepo);
        }

        @Test
        @DisplayName("StorageService 失敗 – 包裝 StorageException")
        void givenStorageFail_whenUpload_thenThrow() throws Exception {

            MockMultipartFile file = new MockMultipartFile(
                    "file", "demo.jpg", "image/jpeg",
                    "x".getBytes());

            when(storageService.upload(
                    anyString(), any(InputStream.class),
                    anyLong(), anyString()))
                .thenThrow(new StorageException("upload failed"));

            assertThatThrownBy(() ->
                    mediaService.upload(42, file, false, null))
                .isInstanceOf(StorageException.class);

            verify(mediaRepo, never()).save(any());
        }
    }

    /* ---------- delete() ---------- */

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("成功刪除 – 呼叫 StorageService 並刪除 DB")
        void givenValidId_whenDelete_thenRemove() {

            ProductMedia m = new ProductMedia();
            m.setId(100);
            m.setProductId(42);
            m.setMediaType(MediaType.IMAGE);
            m.setMediaUrl("https://blob/account/42/demo.jpg");
            m.setMediaOrder(1);

            when(mediaRepo.findById(100)).thenReturn(Optional.of(m));

            mediaService.delete(100);

            verify(storageService)
                    .delete("account/42/demo.jpg"); // 注意：ProductMediaService 截掉前導斜線
            verify(mediaRepo).delete(m);
        }

        // delete(id) 查不到丟 IllegalArgumentException
        @Test
        @DisplayName("找不到 id 時丟 IllegalArgumentException")
        void givenUnknownId_whenDelete_thenThrow() {

            when(mediaRepo.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mediaService.delete(999))
                .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(storageService);
        }
    }

    /* ---------- reorder() ---------- */
    @Nested
    @DisplayName("reorder()")
    class Reorder {

        /** ③ 排序後 mediaOrder 應為 1…n */
        @Test
        @DisplayName("重新排序後 mediaOrder 連號遞增")
        void givenNewOrderMap_whenReorder_thenMediaOrderUpdated() {

            // Arrange：三筆 media
            ProductMedia m1 = new ProductMedia(); m1.setId(1); m1.setMediaOrder(1);
            ProductMedia m2 = new ProductMedia(); m2.setId(2); m2.setMediaOrder(2);
            ProductMedia m3 = new ProductMedia(); m3.setId(3); m3.setMediaOrder(3);

            when(mediaRepo.findByProductId(42))
                    .thenReturn(List.of(m1, m2, m3));

            Map<Integer,Integer> newOrders = Map.of(
                    1, 3,    // 把 id=1 放到 3
                    2, 1,    // id=2 放到 1
                    3, 2);   // id=3 放到 2

            // Act
            mediaService.reorder(42, newOrders);

            // Assert
            assertThat(m1.getMediaOrder()).isEqualTo(3);
            assertThat(m2.getMediaOrder()).isEqualTo(1);
            assertThat(m3.getMediaOrder()).isEqualTo(2);
            verify(mediaRepo).findByProductId(42);
        }
    }
}