package tw.eeits.unhappy.ra.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ra.review.dto.AverageScoresDto;
import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.model.ReviewTag;
import tw.eeits.unhappy.ra.review.service.ReviewMediaService;
import tw.eeits.unhappy.ra.review.service.ReviewService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMediaService reviewMediaService;
    private final ObjectMapper objectMapper;

    /* ---------- 前台：商品評論列表 ---------- */
    @GetMapping("/products/{pid}/reviews")
    public ApiRes<PageDto<ReviewResp>> listByProduct(
            @PathVariable Integer pid,
            @RequestParam(defaultValue = "LATEST") ReviewSortOption sort,
            @RequestParam(defaultValue = "false") boolean onlyImages,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "10")    int size) {

        ReviewSortOption opt = onlyImages ? ReviewSortOption.WITH_IMAGES : sort;
        PageDto<ReviewResp> dto = reviewService.listByProduct(pid, opt, page, size);
        return ResponseFactory.success(dto);
    }

    /* ---------- 前台：商品評論平均分數 ---------- */
    @GetMapping("/products/{pid}/avg")
    public ApiRes<AverageScoresDto> getAverageScores(@PathVariable Integer pid) {
        AverageScoresDto dto = reviewService.getAverageScoresByProduct(pid);
        return ResponseFactory.success(dto);
    }

    /* ---------- 前台：檢查評論是否存在 ---------- */
    @GetMapping("/reviews/{orderItemId}/exists")
    public ApiRes<ExistsResponse> checkReviewExists(
            @PathVariable Integer orderItemId,
            @RequestParam Integer userId) {
        log.info("檢查評論是否存在: orderItemId={}, userId={}", orderItemId, userId);
        ReviewResp review = reviewService.findByOrderItemIdAndUserId(orderItemId, userId);
        return ResponseFactory.success(new ExistsResponse(review != null, review != null ? review.id() : null));
    }

    /* ---------- 前台：獲取評論詳情 ---------- */
    @GetMapping("/reviews/{id}")
    public ApiRes<ReviewResp> getReview(@PathVariable Integer id) {
        log.info("獲取評論詳情: reviewId={}", id);
        try {
            ReviewResp review = reviewService.findById(id);
            return ResponseFactory.success(review);
        } catch (RuntimeException e) {
            log.error("獲取評論失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseFactory.fail("評論不存在");
        }
    }

    /* ---------- 前台：新增評論 ---------- */
     @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 路徑改為 /reviews
    public ResponseEntity<ApiRes<Void>> addReview(
            // 從請求參數中獲取 orderItemId
            @RequestParam("orderItemId") Integer orderItemId,
            @RequestParam("userId") Integer userId,
            @RequestParam("reviewText") String reviewText,
            @RequestParam("scoreQuality") Integer scoreQuality,
            @RequestParam("scoreDescription") Integer scoreDescription,
            @RequestParam("scoreDelivery") Integer scoreDelivery,
            @RequestParam("tags") String tagsJson,
            @RequestPart(name = "images", required = false) MultipartFile[] images) throws IOException {

        log.info("接收到新增評論請求: orderItemId={}, userId={}, images={}",
                orderItemId, userId, images != null ? images.length : 0);

        List<String> tagStrings;
        try {
            tagStrings = Arrays.asList(objectMapper.readValue(tagsJson, String[].class));
            log.info("解析 tagsJson: {}", tagStrings);
        } catch (Exception e) {
            log.error("解析 tagsJson 失敗: {}", tagsJson, e);
            return ResponseEntity.badRequest().body(ResponseFactory.fail("無效的標籤格式"));
        }

        Set<ReviewTag> tags;
        try {
            tags = tagStrings.stream()
                    .map(ReviewTag::fromLabel)
                    .collect(Collectors.toSet());
            log.info("轉換標籤: {}", tags);
        } catch (Exception e) {
            log.error("轉換標籤失敗: {}", tagStrings, e);
            return ResponseEntity.badRequest().body(ResponseFactory.fail("無效的標籤值: " + e.getMessage()));
        }

        List<String> imageUrls = images != null ? reviewMediaService.uploadMultiple(userId, images) : List.of();
        log.info("圖片上傳結果: imageUrls={}", imageUrls);

        try {
            ReviewCreateReq req = new ReviewCreateReq(
                    userId, orderItemId, reviewText, imageUrls,
                    scoreQuality, scoreDescription, scoreDelivery, tags);
            reviewService.createReview(req);
            log.info("評論儲存成功: orderItemId={}", orderItemId);
            return ResponseEntity.ok(ResponseFactory.success((Void) null));
        } catch (IllegalArgumentException e) {
            log.error("新增評論失敗: orderItemId={}, error={}", orderItemId, e.getMessage());
            return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
        }
    }

    /* ---------- 前台：更新評論文字和標籤 ---------- */
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ApiRes<Void>> updateReview(
            @PathVariable Integer id,
            @RequestParam("userId") Integer userId,
            @RequestBody UpdateReviewReq req) {
        log.info("更新評論: reviewId={}, userId={}, tags={}", id, userId, req.tags());
        try {
            ReviewResp review = reviewService.findById(id);
            if (!review.userId().equals(userId)) {
                log.warn("無權更新評論: reviewId={}, userId={}", id, userId);
                return ResponseEntity.status(403).body(ResponseFactory.fail("無權更新此評論"));
            }
            reviewService.updateReview(id, req.reviewText(), req.tags());
            log.info("評論更新成功: reviewId={}", id);
            return ResponseEntity.ok(ResponseFactory.success((Void) null));
        } catch (IllegalArgumentException e) {
            log.error("更新評論失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
        } catch (RuntimeException e) {
            log.error("更新評論失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseEntity.status(404).body(ResponseFactory.fail("評論不存在"));
        }
    }

    /* ---------- 前台：更新評論文字（舊端點，保留相容性） ---------- */
    @PutMapping("/reviews/{id}/text")
    public ResponseEntity<ApiRes<Void>> updateReviewText(
            @PathVariable Integer id,
            @RequestBody UpdateReviewTextReq req) {
        log.info("更新評論文字: reviewId={}", id);
        try {
            reviewService.updateReviewText(id, req.reviewText());
            return ResponseEntity.ok(ResponseFactory.success((Void) null));
        } catch (RuntimeException e) {
            log.error("更新評論文字失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseEntity.status(404).body(ResponseFactory.fail("評論不存在"));
        }
    }

    /* ---------- 前台：按/收回讚 ---------- */
    @PostMapping("/reviews/{id}/like")
    public ResponseEntity<ApiRes<Integer>> toggleLike(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        try {
            int newCount = reviewService.toggleLike(id, userId);
            return ResponseEntity.ok(ResponseFactory.success(newCount));
        } catch (RuntimeException e) {
            log.error("按讚失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseEntity.status(404).body(ResponseFactory.fail("評論不存在"));
        }
    }

    /* ---------- 後台：審核／隱藏評論 ---------- */
    @PutMapping("/reviews/{id}/visible")
    public ResponseEntity<ApiRes<Void>> hideReview(@PathVariable Integer id) {
        try {
            reviewService.hideReview(id);
            return ResponseEntity.ok(ResponseFactory.success((Void) null));
        } catch (RuntimeException e) {
            log.error("隱藏評論失敗: reviewId={}, error={}", id, e.getMessage());
            return ResponseEntity.status(404).body(ResponseFactory.fail("評論不存在"));
        }
    }

    /* ----- 內部 DTO ----- */
    private record ExistsResponse(boolean exists, Integer reviewId) {}
    private record UpdateReviewTextReq(String reviewText) {}
    private record UpdateReviewReq(String reviewText, List<String> tags) {}
}