package tw.eeits.unhappy.ll.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tw.eeits.unhappy.ll.dto.BrandResponse;
import tw.eeits.unhappy.ll.model.Brand;
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
    public Brand update(Integer id, Brand brand) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到指定的品牌資料"));

        if (brand.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "品牌狀態不可為空");
        }
        String normalizedStatus = brand.getStatus().name().toUpperCase();
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "修改狀態不合法");
        }

        existing.setName(brand.getName());
        existing.setType(brand.getType());
        existing.setTaxId(brand.getTaxId());
        existing.setAddress(brand.getAddress());
        existing.setPhoto(brand.getPhoto()); // 寫入 photo
        existing.setEmail(brand.getEmail());
        existing.setPhone(brand.getPhone());
        existing.setFax(brand.getFax());
        existing.setContactName(brand.getContactName());
        existing.setContactEmail(brand.getContactEmail());
        existing.setContactPhone(brand.getContactPhone());
        existing.setStatus(brand.getStatus());

        return brandRepository.save(existing);
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
        response.setContactName(brand.getContactName());
        response.setContactEmail(brand.getContactEmail());
        response.setContactPhone(brand.getContactPhone());
        response.setStatus(brand.getStatus());
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());
        return response;
    }


    @Override
    public Brand findBrandById(Integer id) {
        return brandRepository.findById(id).orElse(null);
    }

}
