package tw.eeits.unhappy.ra.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.IOException;
import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ra.review.dto.AverageScoresDto;
import tw.eeits.unhappy.ra.review.dto.PageDto;
import tw.eeits.unhappy.ra.review.dto.ReviewCreateReq;
import tw.eeits.unhappy.ra.review.dto.ReviewResp;
import tw.eeits.unhappy.ra.review.model.ReviewSortOption;
import tw.eeits.unhappy.ra.review.service.ReviewMediaService;
import tw.eeits.unhappy.ra.review.service.ReviewService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMediaService reviewMediaService;

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

    /* ---------- 前台：新增評論 ---------- */
    @PostMapping("/reviews/{orderItemId}")
    public ResponseEntity<ApiRes<Void>> addReview(
            @PathVariable Integer orderItemId,
            @RequestBody     @Valid ReviewCreateReq req) {

        reviewService.createReview(req);
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }

    /* ---------- 前台：新增評論圖片 ---------- */
    @PostMapping(value = "/reviews/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiRes<String>> uploadImg(
            @RequestParam Integer userId,
            @RequestPart MultipartFile file) throws IOException {

        String url = reviewMediaService.upload(userId, file);
        return ResponseEntity.ok(ResponseFactory.success(url));
    }

    /* ---------- 前台：按/收回讚 ---------- */
    @PostMapping("/reviews/{id}/like")
    public ResponseEntity<ApiRes<Integer>> toggleLike(
            @PathVariable Integer id,
            @RequestParam Integer userId) {

        int newCount = reviewService.toggleLike(id, userId);
        return ResponseEntity.ok(ResponseFactory.success(newCount));
    }

    /* ---------- 後台：審核／隱藏評論 ---------- */
    @PutMapping("/reviews/{id}/visible")
    public ResponseEntity<ApiRes<Void>> hideReview(
            @PathVariable Integer id) {

        reviewService.hideReview(id);
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }
}