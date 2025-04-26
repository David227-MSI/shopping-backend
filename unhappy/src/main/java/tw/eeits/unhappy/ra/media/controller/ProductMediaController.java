package tw.eeits.unhappy.ra.media.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.media.service.ProductMediaService;

@RestController
@RequestMapping("/app/media")
@RequiredArgsConstructor
public class ProductMediaController {

    private final ProductMediaService  mediaSvc;
    private final ProductMediaRepository mediaRepo;   // 取列表／主圖直接查 Repo 即可 (read-only)

    /* ---------- 1. 取某商品所有媒體 ---------- */
    @GetMapping("/product/{pid}")
    public ResponseEntity<ApiRes<List<ProductMedia>>> list(
            @PathVariable Integer pid) {

        List<ProductMedia> list = mediaRepo.findByProductId(pid)
                                            .stream()
                                            .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                                            .toList();
        return ResponseEntity.ok(ResponseFactory.success(list));
    }

    /* ---------- 2. 上傳檔案 ---------- */
    @PostMapping(
        value = "/product/{pid}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiRes<ProductMedia>> upload(
            @PathVariable Integer pid,
            @RequestPart      MultipartFile file,
            @RequestParam(defaultValue = "false") boolean main,
            @RequestParam(required = false)       Integer order) throws Exception {

        ProductMedia saved = mediaSvc.upload(pid, file, main, order);
        return ResponseEntity.ok(ResponseFactory.success(saved));
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
    @GetMapping("/product/{pid}/main")
    public ResponseEntity<ApiRes<ProductMedia>> getMain(@PathVariable Integer pid) {
        ProductMedia main = mediaRepo.findFirstByProductIdAndIsMainTrue(pid).orElse(null);
        return ResponseEntity.ok(ResponseFactory.success(main));
    }
}

