package tw.eeits.unhappy.eeit198product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.eeits.unhappy.eeit198product.repository.CategoryBrandRepository;
import tw.eeits.unhappy.ll.model.Brand;

@Service
public class CategoryBrandService {

    @Autowired
    private CategoryBrandRepository categoryBrandRepository;

    public List<Brand> findBrandsByCategoryIds(List<Integer> categoryIds) {
        return categoryBrandRepository.findBrandsByCategoryIds(categoryIds);
    }
}

