package tw.eeits.unhappy.ll.controller;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;
import tw.eeits.unhappy.ll.repository.BrandRepository;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.service.BrandMediaService;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandMediaController {

    private final BrandRepository brandRepo;
    private final BrandMediaService brandMediaService;

    @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiRes<String>> uploadLogo(@PathVariable Integer id,
                                                    @RequestPart MultipartFile file) throws Exception {
        Brand brand = brandRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到品牌"));

        String url = brandMediaService.uploadLogo(id, file);

        brand.setPhoto(url);
        brandRepo.save(brand);

        return ResponseEntity.ok(ResponseFactory.success(url));
    }

    // 取得品牌 logo URL（預覽用）
    @GetMapping("/{id}/logo")
    public ResponseEntity<ApiRes<String>> getLogo(@PathVariable Integer id) {
        Brand brand = brandRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到品牌"));
    
        return ResponseEntity.ok(ResponseFactory.success(brand.getPhoto()));
    }

    // 刪除品牌 logo（資料庫）
    @DeleteMapping("/{id}/logo")
    public ResponseEntity<ApiRes<Void>> deleteLogo(@PathVariable Integer id) {
        Brand brand = brandRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到品牌"));
    
        brand.setPhoto(null);  // 雲端先不管，直接清空資料庫欄位
        brandRepo.save(brand);
    
        return ResponseEntity.ok(ResponseFactory.success((Void) null));
    }
}
