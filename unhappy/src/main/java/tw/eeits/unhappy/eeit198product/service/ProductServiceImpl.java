package tw.eeits.unhappy.eeit198product.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.eeits.unhappy.eeit198product.dto.ProductDTO;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.ra.media.dto.ProductMediaDto;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMediaRepository productMediaRepository;

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

    /** 取得單一商品（包含圖片資訊，返回 DTO） */
    @Override
    @Transactional
    public ProductDTO getProductDetailsWithImages(Integer productId) {
        Product product = productRepository.findById(productId)
                                            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        String brandName = product.getBrand() != null ? product.getBrand().getName() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        List<ProductMedia> mediaList = productMediaRepository.findByProductId(productId);

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setDescription(product.getDescription());
        dto.setBrandName(brandName);
        dto.setCategoryName(categoryName);

        List<ProductMediaDto> mediaDtos = mediaList.stream()
                                                    .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                                                    .map(ProductMediaDto::from)
                                                    .collect(Collectors.toList());

       dto.setImages(mediaDtos); // *** 設定圖片列表 ***

       return dto; // *** 返回包含圖片的 DTO ***
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
