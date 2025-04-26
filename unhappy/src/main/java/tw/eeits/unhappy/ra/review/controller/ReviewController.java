package tw.eeits.unhappy.ra.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.service.ReviewService;

@RestController
@RequestMapping("/app/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* ---------- 前台：商品評論列表 ---------- */
    @GetMapping("/product/{pid}")
    public ResponseEntity<ApiRes<PageDto<ReviewResp>>> listByProduct(
            @PathVariable Integer pid,
            @RequestParam(defaultValue = "LATEST")  ReviewSortOption sort,
            @RequestParam(defaultValue = "false")   boolean onlyImages,
            @RequestParam(defaultValue = "0")       int page,
            @RequestParam(defaultValue = "10")      int size) {

        // 如果只看有圖，直接改用 WITH_IMAGES 排序（createdAt DESC）
        ReviewSortOption opt = onlyImages ? ReviewSortOption.WITH_IMAGES : sort;

        Page<ReviewResp> pageEntity = reviewService.listByProduct(pid, opt, page, size);
        return ResponseEntity.ok(ResponseFactory.success(PageDto.from(pageEntity)));
    }

    /* ---------- 前台：新增評論 ---------- */
    @PostMapping("/{orderItemId}")
    public ResponseEntity<ApiRes<ReviewResp>> addReview(
            @PathVariable Integer orderItemId,
            @RequestParam    Integer userId,          // demo 直接傳參數
            @RequestBody     @Valid ReviewCreateReq req) {

        ReviewResp resp = reviewService.addReview(userId, orderItemId, req);
        return ResponseEntity.ok(ResponseFactory.success(resp));
    }

    /* ---------- 前台：按/收回讚 ---------- */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiRes<Integer>> toggleLike(
            @PathVariable Integer id,
            @RequestParam    Integer userId) {      // demo: userId 直接帶參數，正式可改從登入上下文取得

        int newCount = reviewService.toggleLike(id, userId);
        return ResponseEntity.ok(ResponseFactory.success(newCount));
    }

    /* ---------- 後台：審核／隱藏評論 ---------- */
    @PatchMapping("/{id}/visible")
    public ResponseEntity<ApiRes<Void>> setVisible(
            @PathVariable Integer id,
            @RequestParam  boolean visible) {

        reviewService.adminSetVisible(id, visible);
        // 若 ResponseFactory 沒有無參 success()，可傳入 null
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }
}
