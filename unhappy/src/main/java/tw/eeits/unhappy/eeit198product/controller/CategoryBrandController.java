package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.eeits.unhappy.eeit198product.service.CategoryBrandService;
import tw.eeits.unhappy.eeit198product.service.CategoryService;
import tw.eeits.unhappy.ll.model.Brand;

@RestController
@RequestMapping("/api/category-brands")
public class CategoryBrandController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBrandService categoryBrandService;

    @GetMapping("/brands")
    public List<Brand> getBrandsByCategoryId(@RequestParam Integer categoryId) {
        List<Integer> ids = categoryService.getSubCategoryIdsIncludingSelf(categoryId);
        return categoryBrandService.findBrandsByCategoryIds(ids);
    }
}
