package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.eeits.unhappy.eeit198product.service.BrandService;
import tw.eeits.unhappy.ll.model.Brand;

@RestController
@RequestMapping("/api")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/user/brands")
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/admin/brands")
    public List<Brand> getAllBrandsAdmin() {
        return brandService.getAllBrands();
    }

    @GetMapping("/user/brands/{id}")
    public Optional<Brand> getBrandById(@PathVariable Integer id) {
        return brandService.getBrandById(id);
    }

    @PostMapping("/admin/brands/{id}")
    public Brand createBrand(@RequestBody Brand brand) {
        return brandService.createBrand(brand);
    }

    @PutMapping("/admin/brands/{id}")
    public Brand updateBrand(@PathVariable Integer id, @RequestBody Brand brandDetails) {
        return brandService.updateBrand(id, brandDetails);
    }

    @DeleteMapping("/admin/brands/{id}")
    public boolean deleteBrand(@PathVariable Integer id) {
        return brandService.deleteBrand(id);
    }
}
