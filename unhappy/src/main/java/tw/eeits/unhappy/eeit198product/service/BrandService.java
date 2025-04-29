package tw.eeits.unhappy.eeit198product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.eeits.unhappy.eeit198product.entity.Brand;
import tw.eeits.unhappy.eeit198product.repository.BrandRepository;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getBrandsByCategoryId(Integer categoryId) {
        return brandRepository.findBrandsByCategoryId(categoryId);
    }

    public Optional<Brand> getBrandById(Integer id) {
        return brandRepository.findById(id);
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Integer id, Brand brandDetails) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isPresent()) {
            Brand brand = optionalBrand.get();
            brand.setName(brandDetails.getName());
            brand.setType(brandDetails.getType());
            brand.setTaxId(brandDetails.getTaxId());
            brand.setAddress(brandDetails.getAddress());
            brand.setPhotoUrl(brandDetails.getPhotoUrl());
            brand.setEmail(brandDetails.getEmail());
            brand.setPhone(brandDetails.getPhone());
            brand.setFax(brandDetails.getFax());
            brand.setContactName(brandDetails.getContactName());
            brand.setContactEmail(brandDetails.getContactEmail());
            brand.setContactPhone(brandDetails.getContactPhone());
            brand.setStatus(brandDetails.getStatus());

            brand.setValidReportCount(brandDetails.getValidReportCount());
            return brandRepository.save(brand);
        }
        return null;
    }

    public boolean deleteBrand(Integer id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Brand> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}
