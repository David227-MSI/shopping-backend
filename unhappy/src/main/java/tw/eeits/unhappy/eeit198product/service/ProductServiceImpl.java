package tw.eeits.unhappy.eeit198product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    /** 無條件回傳所有商品 */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /** 依條件搜尋商品 */
    @Override
    public List<Product> searchProducts(Integer categoryId, Integer brandId, String searchKeyword) {
        if (categoryId == null && brandId == null && (searchKeyword == null || searchKeyword.isBlank())) {
            return findAll();
        }
        return productRepository.searchByCondition(categoryId, brandId, searchKeyword);
    }

    /** 取得單一商品 */
    @Override
    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }

    /** 建立新商品 */
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /** 刪除商品 */
    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    /** 推薦商品（排除自己，取最新前5筆） */
    @Override
    public List<Product> getRecommendedProducts(Integer excludeProductId) {
        return productRepository.findTop5ByIdNotOrderByCreatedAtDesc(excludeProductId);
    }



    // ttpp
    @Override
    public List<Product> findByIds(List<Integer> ids) {
        return productRepository.findAllById(ids);
    }
}
