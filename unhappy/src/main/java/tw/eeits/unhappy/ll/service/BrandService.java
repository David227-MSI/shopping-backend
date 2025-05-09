package tw.eeits.unhappy.ll.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import tw.eeits.unhappy.ll.dto.BrandRequest;
import tw.eeits.unhappy.ll.dto.BrandResponse;
import tw.eeits.unhappy.ll.model.Brand;

public interface BrandService {

    Brand create(Brand brand);

    void createBrandWithPhoto(BrandRequest dto, MultipartFile photo);

    // Brand update(Integer id, Brand brand);
    void updateBrand(Integer id, Brand brand); // 純 JSON 更新

    void updateBrandWithPhoto(Integer id, BrandRequest dto, MultipartFile photo); // multipart 更新

    BrandResponse findById(Integer id);

    List<BrandResponse> findAll();

    Brand findBrandById(Integer id);

    // 【新增】根據名稱查找品牌的方法宣告
    Optional<Brand> findBrandByName(String brandName);
}
