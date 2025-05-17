package tw.eeits.unhappy.eeit198product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.eeits.unhappy.eeit198product.entity.Category;
import tw.eeits.unhappy.eeit198product.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category categoryDetails) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(categoryDetails.getName());
            category.setParentCategory(categoryDetails.getParentCategory());
            return categoryRepository.save(category);
        }
        return null;
    }

    public boolean deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Integer> getSubCategoryIdsIncludingSelf(Integer categoryId) {
        List<Integer> ids = new ArrayList<>();
        ids.add(categoryId);
    
        List<Category> subs = categoryRepository.findByParentCategory_Id(categoryId);
        for (Category sub : subs) {
            ids.add(sub.getId());
        }
        return ids;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 【新增】根據名稱查找分類的 Service 方法
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
}
