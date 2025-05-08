package tw.eeits.unhappy.ra.media.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 引入 Slf4j
import org.springframework.http.HttpStatus; // 引入 HttpStatus
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
import java.util.stream.Collectors; // 引入 Collectors

@Slf4j // 添加 Slf4j 日誌註解
@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ProductMediaController {

     private final ProductMediaService mediaSvc;
     private final ProductMediaRepository mediaRepo;

     /* ---------- 1. 取某商品所有媒體 ---------- */
     @Transactional(readOnly = true)
     @GetMapping("/product/{pid}")
     public ResponseEntity<ApiRes<List<ProductMediaDto>>> list(@PathVariable Integer pid) {
          log.info("Received GET request for media list for product ID: {}", pid);
          List<ProductMedia> mediaList = mediaRepo.findByProductId(pid);
          // 將媒體 Entity 轉換為 DTO，並按 mediaOrder 排序
          List<ProductMediaDto> list = mediaList.stream()
                    .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                    .map(ProductMediaDto::from)
                    .collect(Collectors.toList());
          log.info("Returning {} media items for product ID: {}", list.size(), pid);
          return ResponseEntity.ok(ResponseFactory.success(list));
     }

     /* ---------- 【新增】2.1 批量上傳媒體 ---------- */
     @PostMapping(value = "/product/{pid}/upload-batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ApiRes<List<ProductMediaDto>>> uploadBatch(
               @PathVariable Integer pid,
               @RequestPart("files") List<MultipartFile> files) { // 接收多個檔案
          log.info("Received POST request to upload batch media for product ID: {}. File count: {}", pid, files.size());
          if (files == null || files.isEmpty()) {
               log.warn("No files received for batch upload for product ID: {}", pid);
               return ResponseEntity.badRequest().body(ResponseFactory.fail("沒有收到檔案"));
          }
          try {
               List<ProductMedia> savedMediaList = mediaSvc.uploadBatch(pid, files);
               // 將保存成功的媒體 Entity 列表轉換為 DTO 列表
               List<ProductMediaDto> savedMediaDtos = savedMediaList.stream()
                         .map(ProductMediaDto::from)
                         .collect(Collectors.toList());
               log.info("Batch media uploaded successfully. Saved {} records.", savedMediaDtos.size());
               return ResponseEntity.status(HttpStatus.CREATED).body(ResponseFactory.success(savedMediaDtos));
          } catch (IllegalArgumentException e) {
               log.error("Error during batch media upload for product ID {}: {}", pid, e.getMessage());
               return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
          } catch (RuntimeException e) {
               log.error("Runtime error during batch media upload for product ID {}: {}", pid, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("批量上傳媒體失敗"));
          } catch (Exception e) { // 捕獲其他未知異常
               log.error("Unexpected error during batch media upload for product ID {}: {}", pid, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(ResponseFactory.fail("批量上傳媒體時發生未知錯誤"));
          }
     }

     /* ---------- 3. 拖曳後重新排序 ---------- */
     @PutMapping("/product/{pid}/reorder")
     public ResponseEntity<ApiRes<Void>> reorder(
               @PathVariable Integer pid,
               @RequestBody Map<Integer, Integer> newOrders) {
          log.info("Received PUT request to reorder media for product ID: {}", pid);
          try {
               mediaSvc.reorder(pid, newOrders);
               log.info("Media reordered successfully for product ID: {}", pid);
               return ResponseEntity.ok(ResponseFactory.success((Void) null));
          } catch (IllegalArgumentException e) {
               log.error("Error during media reorder for product ID {}: {}", pid, e.getMessage());
               return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
          } catch (RuntimeException e) {
               log.error("Runtime error during media reorder for product ID {}: {}", pid, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("媒體排序失敗"));
          } catch (Exception e) {
               log.error("Unexpected error during media reorder for product ID {}: {}", pid, e.getMessage(), e);
               // 【修正】INTERNAL_SERVER_SERVER_ERROR -> INTERNAL_SERVER_ERROR
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("媒體排序時發生未知錯誤"));
          }
     }

     /* ---------- 4. 刪除媒體 ---------- */
     @DeleteMapping("/{id}")
     public ResponseEntity<ApiRes<Void>> delete(@PathVariable Integer id) {
          log.info("Received DELETE request for media ID: {}", id);
          try {
               mediaSvc.delete(id);
               log.info("Media deleted successfully. ID: {}", id);
               return ResponseEntity.ok(ResponseFactory.success((Void) null));
          } catch (IllegalArgumentException e) {
               log.error("Error during media deletion for ID {}: {}", id, e.getMessage());
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail(e.getMessage()));
          } catch (RuntimeException e) {
               log.error("Runtime error during media deletion for ID {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("刪除媒體失敗"));
          } catch (Exception e) {
               log.error("Unexpected error during media deletion for ID {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("刪除媒體時發生未知錯誤"));
          }
     }

     /* ---------- 5. 取主圖 (前台商品卡片可用) ---------- */
     @Transactional(readOnly = true)
     @GetMapping("/product/{pid}/main")
     public ResponseEntity<ApiRes<ProductMediaDto>> getMain(@PathVariable Integer pid) {
          log.info("Received GET request for main media for product ID: {}", pid);
          ProductMedia main = mediaRepo.findFirstByProductIdAndIsMainTrue(pid).orElse(null);
          ProductMediaDto mainDto = (main == null ? null : ProductMediaDto.from(main));
          // 【修正】使用 DTO 的 accessor id()
          log.info("Returning main media for product ID {}: {}", pid, mainDto != null ? mainDto.id() : "none");
          return ResponseEntity.ok(ResponseFactory.success(mainDto));
     }

     /* ---------- 6. 設定主圖 ---------- */
     @PutMapping("/{id}/main")
     public ResponseEntity<ApiRes<Void>> setMain(@PathVariable Integer id) {
          log.info("Received PUT request to set media ID {} as main.", id);
          try {
               mediaSvc.setMain(id);
               log.info("Media ID {} set as main successfully.", id);
               return ResponseEntity.ok(ResponseFactory.success((Void) null));
          } catch (IllegalArgumentException e) {
               log.error("Error during setting media as main for ID {}: {}", id, e.getMessage());
               return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
          } catch (RuntimeException e) {
               log.error("Runtime error during setting media as main for ID {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("設定主圖失敗"));
          } catch (Exception e) {
               log.error("Unexpected error during setting media as main for ID {}: {}", id, e.getMessage(), e);
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("設定主圖時發生未知錯誤"));
          }
     }
}
