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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.service.ReviewService;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
class ReviewControllerTest {

        @Autowired MockMvc mvc;
        @Autowired ObjectMapper mapper;

        @MockBean ReviewService reviewService;   // Service 層打 stub


    /* ---------- GET /product/{productId} ---------- */
        @Test @DisplayName("查詢評論 (LATEST) 成功")
        void list_ok() throws Exception {

                ReviewResp r1 = new ReviewResp(1, 1, 9, "A", null,
                        5,5,5,true, Set.of(), 2, null);

                given(reviewService.listByProduct(9, ReviewSortOption.LATEST, 0, 5))
                        .willReturn(new PageImpl<>(List.of(r1)));

                mvc.perform(get("/app/reviews/product/9")
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))   // ApiRes 的 success 欄位
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].reviewText").value("A"));
                }


        /* ---------- POST /{orderItemId} ---------- */
        @Test @DisplayName("新增評論成功，回 200 + JSON")
        void create_ok() throws Exception {

                ReviewCreateReq req = new ReviewCreateReq(
                        "讚啦", null, 5, 5, 5,
                        Set.of(ReviewTag.FAST, ReviewTag.QUALITY));

                ReviewResp stubResp = new ReviewResp(
                        1, 1001, 9, req.reviewText(), null,
                        5, 5, 5, true, req.tags(),
                        0, null);

                given(reviewService.addReview(eq(1001), eq(9), any()))
                        .willReturn(stubResp);

                mvc.perform(post("/app/reviews/9")
                        .param("userId", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1001))
                .andExpect(jsonPath("$.data.scoreQuality").value(5));
        }


        /* ---------- POST /{reviewId}/like ---------- */
        @Test @DisplayName("按 / 取消讚成功，回新總數")
        void toggleLike_ok() throws Exception {

        given(reviewService.toggleLike(1, 1001)).willReturn(3);

        mvc.perform(post("/app/reviews/1/like")
                .param("userId","1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(3));
        }
}
