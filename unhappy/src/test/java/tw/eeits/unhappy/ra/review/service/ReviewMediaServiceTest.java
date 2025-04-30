package tw.eeits.unhappy.ra.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import tw.eeits.unhappy.ra.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class ReviewMediaServiceTest {

    @Mock  StorageService storage;
    @InjectMocks ReviewMediaService mediaSvc;

    @Test @DisplayName("成功上傳 – 回傳 URL")
    void upload_ok() throws Exception {
        MockMultipartFile f = new MockMultipartFile(
                "file","x.jpg","image/jpeg","demo".getBytes());

        when(storage.upload(anyString(), any(InputStream.class),
                            eq(f.getSize()), eq(f.getContentType())))
            .thenReturn("https://blob/review/1001/uuid.jpg");

        String url = mediaSvc.upload(1001, f);

        assertThat(url).isEqualTo("https://blob/review/1001/uuid.jpg");
    }

    @Test @DisplayName("超過 3 MB 時丟 IllegalArgumentException")
    void oversize_throw() {
        byte[] big = new byte[4*1024*1024];          // 4 MB
        MockMultipartFile f =
            new MockMultipartFile("f","b.jpg","image/jpeg",big);

        assertThatThrownBy(() -> mediaSvc.upload(1001, f))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(storage);
    }
}

