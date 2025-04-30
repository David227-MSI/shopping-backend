package tw.eeits.unhappy.ra.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.service.ReviewMediaService;
import tw.eeits.unhappy.ra.review.service.ReviewService;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
class ReviewControllerTest {

        @Autowired MockMvc mvc;
        @Autowired ObjectMapper mapper; 
        @MockBean ReviewService reviewService;
        @MockBean ReviewMediaService reviewMediaService;        
        @Test @DisplayName("查詢評論 (LATEST) 成功")
        void list_ok() throws Exception {
                ReviewResp r1 = new ReviewResp(
                        1, 1001, 9, "很好用！", List.of("http://x/img.jpg"),
                        5, 5, 5, true, List.of("FAST","QUALITY"), 2, LocalDateTime.now()
                );

                PageImpl<ReviewResp> page = new PageImpl<>(List.of(r1));
                PageDto<ReviewResp> pageDto = PageDto.from(page);

                given(reviewService.listByProduct(eq(9), eq(ReviewSortOption.LATEST), eq(0), eq(5)))
                        .willReturn(pageDto);

                mvc.perform(get("/api/reviews/product/9")
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].reviewText").value("很好用！"))
                .andExpect(jsonPath("$.data.content[0].reviewImages[0]").value("http://x/img.jpg"));
        }

        @Test
        @DisplayName("新增評論成功，回 200 + JSON")
        void create_ok() throws Exception {
                ReviewCreateReq req = new ReviewCreateReq(
                1,          // userId
                1001,       // orderItemId
                "很好用的商品！",  // reviewText
                List.of("https://cdn/img.jpg"),
                5, 5, 5,
                Set.of(ReviewTag.QUALITY)
                );
        
                willDoNothing().given(reviewService).createReview(any());
        
                mvc.perform(post("/api/reviews/1001")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        }

        @Test @DisplayName("上傳評論圖片成功")
        void uploadImg_ok() throws Exception {
                MockMultipartFile f = new MockMultipartFile(
                "file","p.png",MediaType.IMAGE_PNG_VALUE,"hi".getBytes());

                given(reviewMediaService.upload(eq(1001), any()))
                        .willReturn("https://cdn/reviews/p.png");

                mvc.perform(multipart("/api/reviews/upload")
                        .file(f)
                        .param("userId","1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("https://cdn/reviews/p.png"));
        }

        @Test @DisplayName("按 / 取消讚成功，回新總數")
        void toggleLike_ok() throws Exception {
                given(reviewService.toggleLike(1, 1001)).willReturn(3);

                mvc.perform(post("/api/reviews/1/like")
                        .param("userId","1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(3));
        }
}