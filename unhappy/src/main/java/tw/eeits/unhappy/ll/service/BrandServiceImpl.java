package tw.eeits.unhappy.ll.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tw.eeits.unhappy.ll.dto.BrandRequest;
import tw.eeits.unhappy.ll.dto.BrandResponse;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.model.BrandStatus;
import tw.eeits.unhappy.ll.repository.BrandRepository;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "INACTIVE", "WARNING");

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Brand create(Brand brand) {
        if (brand.getValidReportCount() == null) {
            brand.setValidReportCount(0); // 自己補 0
        }
        Brand saved = brandRepository.save(brand);
        return brandRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("儲存後找不到品牌資料"));
    }

    @Override
    public void createBrandWithPhoto(BrandRequest dto, MultipartFile photo) {
        Brand brand = new Brand();

        brand.setName(dto.getName());
        brand.setType(dto.getType());
        brand.setTaxId(dto.getTaxId());
        brand.setAddress(dto.getAddress());
        brand.setEmail(dto.getEmail());
        brand.setPhone(dto.getPhone());
        brand.setFax(dto.getFax());
        brand.setContactName(dto.getContactName());
        brand.setContactEmail(dto.getContactEmail());
        brand.setContactPhone(dto.getContactPhone());
        brand.setStatus(BrandStatus.ACTIVE); // 預設為 ACTIVE
        brand.setValidReportCount(0);

        if (photo != null && !photo.isEmpty()) {
            String savedPath = savePhotoFile(photo);
            brand.setPhoto(savedPath);
        }

        brandRepository.save(brand);
    }

    // @Override
    // public Brand update(Integer id, Brand brand) {
    // Brand existing = brandRepository.findById(id)
    // .orElseThrow(() -> new RuntimeException("找不到指定的品牌資料"));

    // if (brand.getStatus() == null) {
    // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "品牌狀態不可為空");
    // }
    // String normalizedStatus = brand.getStatus().name().toUpperCase();
    // if (!VALID_STATUSES.contains(normalizedStatus)) {
    // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "修改狀態不合法");
    // }

    // existing.setName(brand.getName());
    // existing.setType(brand.getType());
    // existing.setTaxId(brand.getTaxId());
    // existing.setAddress(brand.getAddress());
    // existing.setPhoto(brand.getPhoto()); // 寫入 photo
    // existing.setEmail(brand.getEmail());
    // existing.setPhone(brand.getPhone());
    // existing.setFax(brand.getFax());
    // existing.setContactName(brand.getContactName());
    // existing.setContactEmail(brand.getContactEmail());
    // existing.setContactPhone(brand.getContactPhone());
    // existing.setStatus(brand.getStatus());

    // return brandRepository.save(existing);
    // }

    @Override
    public void updateBrand(Integer id, Brand brand) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到品牌"));

        existing.setName(brand.getName());
        existing.setType(brand.getType());
        existing.setTaxId(brand.getTaxId());
        existing.setAddress(brand.getAddress());
        existing.setEmail(brand.getEmail());
        existing.setPhone(brand.getPhone());
        existing.setFax(brand.getFax());
        existing.setContactName(brand.getContactName());
        existing.setContactEmail(brand.getContactEmail());
        existing.setContactPhone(brand.getContactPhone());
        existing.setStatus(brand.getStatus());
        existing.setPhoto(brand.getPhoto()); // 若使用者沒改圖片，就維持原本

        brandRepository.save(existing);
    }

    // @Override
    // public void updateBrandWithPhoto(Integer id, BrandRequest dto, MultipartFile
    // photo) {
    // Brand brand = brandRepository.findById(id)
    // .orElseThrow(() -> new RuntimeException("找不到品牌"));

    // // 設定欄位
    // brand.setName(dto.getName());
    // brand.setType(dto.getType());
    // brand.setTaxId(dto.getTaxId());
    // brand.setAddress(dto.getAddress());
    // brand.setEmail(dto.getEmail());
    // brand.setPhone(dto.getPhone());
    // brand.setFax(dto.getFax());
    // brand.setContactName(dto.getContactName());
    // brand.setContactEmail(dto.getContactEmail());
    // brand.setContactPhone(dto.getContactPhone());

    // // 狀態預設為 ACTIVE（如果需要）
    // if (brand.getStatus() == null) {
    // brand.setStatus(BrandStatus.ACTIVE);
    // }

    // // 圖片處理
    // if (photo != null && !photo.isEmpty()) {
    // try {
    // String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
    // Path path = Paths.get("uploads/brands");
    // Files.createDirectories(path);
    // Path filepath = path.resolve(filename);
    // Files.copy(photo.getInputStream(), filepath,
    // StandardCopyOption.REPLACE_EXISTING);

    // brand.setPhoto("/uploads/brands/" + filename);
    // } catch (IOException e) {
    // throw new RuntimeException("圖片上傳失敗", e);
    // }
    // }

    // brandRepository.save(brand);
    // }

    @Override
    public void updateBrandWithPhoto(Integer id, BrandRequest dto, MultipartFile photo) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到品牌"));

        brand.setName(dto.getName());
        brand.setType(dto.getType());
        brand.setTaxId(dto.getTaxId());
        brand.setAddress(dto.getAddress());
        brand.setEmail(dto.getEmail());
        brand.setPhone(dto.getPhone());
        brand.setFax(dto.getFax());
        brand.setContactName(dto.getContactName());
        brand.setContactEmail(dto.getContactEmail());
        brand.setContactPhone(dto.getContactPhone());
        brand.setStatus(dto.getStatus()); // 狀態從 DTO 來

        if (brand.getStatus() == null) {
            brand.setStatus(BrandStatus.ACTIVE);
        }

        if (photo != null && !photo.isEmpty()) {
            String savedPath = savePhotoFile(photo);
            brand.setPhoto(savedPath);
        }

        brandRepository.save(brand);
    }

    private String savePhotoFile(MultipartFile photo) {
        try {
            String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            Path path = Paths.get("uploads/brands");
            Files.createDirectories(path);
            Path filepath = path.resolve(filename);
            Files.copy(photo.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/brands/" + filename; // 儲存的是相對路徑
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗", e);
        }
    }

    @Override
    public BrandResponse findById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到指定的品牌資料"));
        return convertToResponse(brand);
    }

    @Override
    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private BrandResponse convertToResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setType(brand.getType());
        response.setTaxId(brand.getTaxId());
        response.setAddress(brand.getAddress());
        response.setEmail(brand.getEmail());
        response.setPhone(brand.getPhone());
        response.setFax(brand.getFax());
        response.setPhoto(brand.getPhoto());
        response.setContactName(brand.getContactName());
        response.setContactEmail(brand.getContactEmail());
        response.setContactPhone(brand.getContactPhone());
        response.setStatus(brand.getStatus());
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());
        return response;
    }
}
