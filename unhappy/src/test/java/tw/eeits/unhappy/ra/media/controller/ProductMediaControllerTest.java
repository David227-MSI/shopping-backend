package tw.eeits.unhappy.ra.media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.service.ProductMediaService;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductMediaController.class)
@AutoConfigureMockMvc
class ProductMediaControllerTest {

        @Autowired MockMvc mvc;
        @Autowired ObjectMapper mapper; 
        @MockBean ProductMediaService mediaService;
        @MockBean ProductMediaRepository mediaRepo;     
        @Test
        @DisplayName("列出商品媒體成功")
        void list_ok() throws Exception {
                Product p = new Product(); p.setId(101);
                ProductMedia m1 = new ProductMedia(1, p, tw.eeits.unhappy.ra.media.model.MediaType.IMAGE,
                        "http://cdn/img1.jpg", "alt", 1, true,
                        LocalDateTime.now(), null);

                given(mediaRepo.findByProductId(101)).willReturn(List.of(m1));

                mvc.perform(get("/api/media/product/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
        }

        @Test
        @DisplayName("取得主圖成功")
        void getMain_ok() throws Exception {
                Product p = new Product(); p.setId(101);
                ProductMedia main = new ProductMedia(2, p, tw.eeits.unhappy.ra.media.model.MediaType.IMAGE,
                        "http://cdn/main.jpg", "main", 1, true,
                        LocalDateTime.now(), null);

                given(mediaRepo.findFirstByProductIdAndIsMainTrue(101))
                        .willReturn(Optional.of(main));

                mvc.perform(get("/api/media/product/101/main"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(2));
        }

        @Test
        @DisplayName("上傳媒體成功")
        void upload_ok() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                        "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

                Product p = new Product(); p.setId(101);
                ProductMedia saved = new ProductMedia(3, p, tw.eeits.unhappy.ra.media.model.MediaType.IMAGE,
                        "http://cdn/photo.jpg", "photo", 2, false,
                        LocalDateTime.now(), null);

                given(mediaService.upload(eq(101), any(), eq(false), isNull()))
                        .willReturn(saved);

                mvc.perform(multipart("/api/media/product/101")
                        .file(file)
                        .param("main", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(3));
        }

        @Test
        @DisplayName("重新排序成功")
        void reorder_ok() throws Exception {
                Map<String, Integer> body = Map.of("3", 1, "2", 2);
                willDoNothing().given(mediaService).reorder(eq(101), any());

                mvc.perform(put("/api/media/product/101/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("刪除媒體成功")
        void delete_ok() throws Exception {
                willDoNothing().given(mediaService).delete(3);

                mvc.perform(delete("/api/media/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }
}
