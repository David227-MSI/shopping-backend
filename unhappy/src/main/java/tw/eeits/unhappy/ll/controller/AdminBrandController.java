package tw.eeits.unhappy.ll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // 更新品牌
    @PutMapping("/{id}")
    public ResponseEntity<Brand> update(@PathVariable Integer id, @RequestBody @Valid Brand brand) {
        Brand updated = brandService.update(id, brand);
        return ResponseEntity.ok(updated);
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
}