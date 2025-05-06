package tw.eeits.unhappy.ra.media.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ra.media.dto.ProductMediaDto;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.media.service.ProductMediaService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ProductMediaController {

    private final ProductMediaService  mediaSvc;
    private final ProductMediaRepository mediaRepo;   // 取列表／主圖直接查 Repo 即可 (read-only)

    /* ---------- 1. 取某商品所有媒體 ---------- */
    @Transactional(readOnly = true)
    @GetMapping("/product/{pid}")
    public ResponseEntity<ApiRes<List<ProductMediaDto>>> list(@PathVariable Integer pid) {
        List<ProductMediaDto> list = mediaRepo.findByProductId(pid).stream()
                                                .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                                                .map(ProductMediaDto::from)
                                                .toList();
        return ResponseEntity.ok(ResponseFactory.success(list));
    }

    /* ---------- 2. 上傳檔案 ---------- */
    @PostMapping(
        value = "/product/{pid}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiRes<ProductMediaDto>> upload(
            @PathVariable Integer pid,
            @RequestPart MultipartFile file,
            @RequestParam(defaultValue = "false") boolean main,
            @RequestParam(required = false) Integer order) throws Exception {
    
        ProductMedia saved = mediaSvc.upload(pid, file, main, order);
        return ResponseEntity.ok(ResponseFactory.success(ProductMediaDto.from(saved)));
    }

    /* ---------- 3. 拖曳後重新排序 ---------- */
    @PutMapping("/product/{pid}/reorder")
    public ResponseEntity<ApiRes<Void>> reorder(
            @PathVariable Integer pid,
            @RequestBody Map<Integer, Integer> newOrders) {

        mediaSvc.reorder(pid, newOrders);
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }

    /* ---------- 4. 刪除媒體 ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> delete(@PathVariable Integer id) {
        mediaSvc.delete(id);
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }

    /* ---------- 5. 取主圖 (前台商品卡片可用) ---------- */
    @Transactional(readOnly = true)
    @GetMapping("/product/{pid}/main")
    public ResponseEntity<ApiRes<ProductMediaDto>> getMain(@PathVariable Integer pid) {
        ProductMedia main = mediaRepo.findFirstByProductIdAndIsMainTrue(pid).orElse(null);
        return ResponseEntity.ok(ResponseFactory.success(
            main == null ? null : ProductMediaDto.from(main)
        ));
    }

    /* ---------- 6. 設定主圖 ---------- */
    @PutMapping("/{id}/main")
    public ResponseEntity<ApiRes<Void>> setMain(@PathVariable Integer id) {
        mediaSvc.setMain(id);
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }
}

