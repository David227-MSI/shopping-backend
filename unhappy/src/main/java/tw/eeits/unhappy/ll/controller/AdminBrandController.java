package tw.eeits.unhappy.ll.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.dto.BrandRequest;
import tw.eeits.unhappy.ll.dto.BrandResponse;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.service.BrandService;

@RestController
@RequestMapping("/api/admin/brands")
@Validated
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    // 新增品牌
    @PostMapping
    public ResponseEntity<Brand> create(@RequestBody @Valid Brand brand) {
        Brand created = brandService.create(brand);
        return ResponseEntity.ok(created);
    }

    // 查詢單一品牌
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> findById(@PathVariable Integer id) {
        BrandResponse found = brandService.findById(id);
        return ResponseEntity.ok(found);
    }

    // 查詢所有品牌
    @GetMapping
    public ResponseEntity<List<BrandResponse>> findAll() {
        List<BrandResponse> list = brandService.findAll();
        return ResponseEntity.ok(list);
    }

    // 更新品牌
    // @PutMapping("/{id}")
    // public ResponseEntity<Brand> update(@PathVariable Integer id, @RequestBody @Valid Brand brand) {
    //     Brand updated = brandService.update(id, brand);
    //     return ResponseEntity.ok(updated);
    // }
    

    // ✅ 純 JSON 更新
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Brand brand) {
        brandService.updateBrand(id, brand);
        return ResponseEntity.ok().build();
    }

    // ✅ JSON + 圖片 更新
    // @PutMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<?> updateWithPhoto(
    //         @PathVariable Integer id,
    //         @Valid @RequestPart("brand") BrandRequest dto,
    //         @RequestPart(value = "photo", required = false) MultipartFile photo) {

    //     brandService.updateBrandWithPhoto(id, dto, photo);
    //     return ResponseEntity.ok().build();
    // }

    @PutMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> updateWithPhoto(
        @PathVariable Integer id,
        @RequestPart("brand") String brandJson,
        @RequestPart(value = "photo", required = false) MultipartFile photo
) {
    // System.out.println("收到 JSON 原文：\n" + brandJson);
    try {
        ObjectMapper mapper = new ObjectMapper();
        BrandRequest dto = mapper.readValue(brandJson, BrandRequest.class);

        brandService.updateBrandWithPhoto(id, dto, photo);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("JSON 解析失敗：" + e.getMessage());
    }
}



}