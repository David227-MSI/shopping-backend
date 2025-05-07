package tw.eeits.unhappy.eeit198product.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDateTime; // 引入 LocalDateTime

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.eeits.unhappy.eeit198product.dto.ProductDTO;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.entity.Category;
// 注意：這裡的 Brand 應該是從 ll 模組引入
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.ra.media.dto.ProductMediaDto;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;

// 注意：這裡的 BrandService 應該是從 ll 模組引入
import tw.eeits.unhappy.ll.service.BrandService;
import tw.eeits.unhappy.eeit198product.service.CategoryService;
import tw.eeits.unhappy.ra.media.service.ProductMediaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j // 添加 Slf4j 日誌註解
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMediaRepository productMediaRepository;

    // 注意：這裡的 BrandService 應該注入 ll 模組的 BrandService
    @Autowired
    private BrandService llBrandService; // 確保這個是 ll 模組的 BrandService

    @Autowired
    private CategoryService eeit198productCategoryService; // 確保這個是 eeit198product 模組的 CategoryService

    @Autowired
    private ProductMediaService productMediaService;

    /** 取得所有商品（無條件搜尋） */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.info("Fetching all products.");
        List<Product> products = productRepository.findAll();
        log.info("Found {} total products.", products.size());
        return products;
    }

    /** 依條件搜尋商品（categoryId、brandId、keyword 可為 null） */
    @Override
    @Transactional(readOnly = true) // 搜尋操作通常是只讀的
    public List<Product> searchProducts(Integer categoryId, Integer brandId, String searchKeyword) {
        log.info("Searching products with categoryId={}, brandId={}, searchKeyword={}", categoryId, brandId, searchKeyword);
        List<Product> products = productRepository.searchByCondition(categoryId, brandId, searchKeyword);
        log.info("Found {} products matching search criteria.", products.size());
        return products;
    }

    /** 取得單一商品（Optional 包裝，可避免 null） */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Integer id) {
        log.info("Fetching product by ID: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            log.info("Product found for ID: {}", id);
        } else {
            log.warn("Product not found for ID: {}", id);
        }
        return product;
    }

    /** 取得單一商品（包含圖片資訊，返回 DTO） */
    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductDetailsWithImages(Integer id) {
        log.info("Fetching product details with images for product ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for details fetch. ID: {}", id);
                return new IllegalArgumentException("商品不存在。ID: " + id);
            });
        log.info("Found product for details: ID={}, Name={}", id, product.getName());

        // 獲取品牌和分類名稱（假設 Product Entity 有 ManyToOne 到 Brand 和 Category）
        // 這裡直接訪問 Entity 的關聯屬性來獲取名稱
        String brandName = (product.getBrand() != null) ? product.getBrand().getName() : "未知品牌";
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "未知分類";
        log.debug("Product ID {} details - Brand: {}, Category: {}", id, brandName, categoryName);


        // 獲取相關媒體（圖片）
        List<ProductMedia> mediaList = productMediaRepository.findByProductId(id);
        log.debug("Found {} media records for product ID {}", mediaList.size(), id);


        // 轉換為 DTO
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setDescription(product.getDescription());
        // 設定品牌和分類名稱到 DTO
        dto.setBrandName(brandName);
        dto.setCategoryName(categoryName);
        // 設定其他屬性到 DTO
        dto.setIsActive(product.getIsActive());
        dto.setStock(product.getStock());
        // 如果 Product Entity 有 startTime 和 endTime，也需要設定到 DTO
        // dto.setStartTime(product.getStartTime());
        // dto.setEndTime(product.getEndTime());


        // 將媒體 Entity 轉換為媒體 DTO，並按 mediaOrder 排序
        List<ProductMediaDto> mediaDtos = mediaList.stream()
                                                    .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                                                    .map(ProductMediaDto::from)
                                                    .collect(Collectors.toList());

        dto.setImages(mediaDtos); // 設定圖片列表到 DTO

        log.info("Finished fetching product details with images for product ID: {}", id);
        return dto; // 返回包含圖片的 DTO
    }

    /** 建立新商品 - 接收 DTO 並返回 Entity */
    @Override
    @Transactional // 確保事務支援
    public Product createProduct(ProductDTO productDto) { // 接收 ProductDTO
         log.info("Creating new product from DTO: {}", productDto.getName());

         // 將 ProductDTO 轉換為 Product Entity
         Product product = new Product();
         product.setName(productDto.getName());
         product.setUnitPrice(productDto.getUnitPrice());
         product.setDescription(productDto.getDescription());

         // 【修正】從 DTO 獲取並設定其他屬性
         product.setIsActive(productDto.getIsActive() != null ? productDto.getIsActive() : false); // 預設為 false 或根據需求設定
         product.setStock(productDto.getStock() != null ? productDto.getStock() : 0); // 預設為 0 或根據需求設定
         // TODO: 如果有 startTime 和 endTime，需要從 DTO 設定到 Entity
         // product.setStartTime(productDto.getStartTime());
         // product.setEndTime(productDto.getEndTime());


         // 【修正】根據 DTO 中的品牌 ID 找到 Brand Entity 並設定
         if (productDto.getBrandId() == null) {
             log.warn("Brand ID is null in ProductDTO for new product: {}", productDto.getName());
             throw new IllegalArgumentException("品牌 ID 不可為空");
         }
         log.debug("Finding Brand with ID: {}", productDto.getBrandId());
         Brand brand = llBrandService.findBrandById(productDto.getBrandId()); // 確保 BrandService 有 findBrandById 方法且返回 Brand
         if (brand == null) {
             log.warn("Brand not found for ID: {}", productDto.getBrandId());
             throw new IllegalArgumentException("無效的品牌 ID: " + productDto.getBrandId());
         }
         product.setBrand(brand);
         log.debug("Set Brand {} on product {}", brand.getName(), productDto.getName());


         // 【修正】根據 DTO 中的分類 ID 找到 Category Entity 並設定
         if (productDto.getCategoryId() == null) {
             log.warn("Category ID is null in ProductDTO for new product: {}", productDto.getName());
             throw new IllegalArgumentException("分類 ID 不可為空");
         }
         log.debug("Finding Category with ID: {}", productDto.getCategoryId());
         Category category = eeit198productCategoryService.getCategoryById(productDto.getCategoryId()) // 確保 CategoryService 有 getCategoryById 方法且返回 Optional<Category>
                                                           .orElseThrow(() -> {
                                                                log.warn("Category not found for ID: {}", productDto.getCategoryId());
                                                                return new IllegalArgumentException("無效的分類 ID: " + productDto.getCategoryId());
                                                           });
         product.setCategory(category);
         log.debug("Set Category {} on product {}", category.getName(), productDto.getName());


         Product savedProduct = productRepository.save(product);
         log.info("Product created successfully with ID: {}", savedProduct.getId());
         return savedProduct;
    }

    /** 更新商品 - 接收 ID 和 DTO 並返回 Entity */
    @Override
    @Transactional // 確保事務支援
    public Product updateProduct(Integer id, ProductDTO productDto) { // 更新方法
        log.info("Updating product with ID {} from DTO: {}", id, productDto.getName());

        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for update. ID: {}", id);
                return new IllegalArgumentException("商品不存在，無法更新。ID: " + id);
            });
        log.info("Found product for update: ID={}, Name={}", id, existingProduct.getName());


        // 【修正】根據 DTO 中的品牌 ID 找到 Brand Entity 並設定
         if (productDto.getBrandId() == null) {
             log.warn("Brand ID is null in ProductDTO for updating product: {}", productDto.getName());
             throw new IllegalArgumentException("品牌 ID 不可為空");
         }
         log.debug("Finding Brand with ID: {}", productDto.getBrandId());
         Brand brand = llBrandService.findBrandById(productDto.getBrandId()); // 確保 BrandService 有 findBrandById 方法且返回 Brand
         if (brand == null) {
             log.warn("Brand not found for ID: {}", productDto.getBrandId());
             throw new IllegalArgumentException("無效的品牌 ID: " + productDto.getBrandId());
         }
         existingProduct.setBrand(brand);
         log.debug("Set Brand {} on product {}", brand.getName(), existingProduct.getName());


        // 【修正】根據 DTO 中的分類 ID 找到 Category Entity 並設定
         if (productDto.getCategoryId() == null) {
             log.warn("Category ID is null in ProductDTO for updating product: {}", productDto.getName());
             throw new IllegalArgumentException("分類 ID 不可為空");
         }
         log.debug("Finding Category with ID: {}", productDto.getCategoryId());
         Category category = eeit198productCategoryService.getCategoryById(productDto.getCategoryId()) // 確保 CategoryService 有 getCategoryById 方法且返回 Optional<Category>
                                                           .orElseThrow(() -> {
                                                                log.warn("Category not found for ID: {}", productDto.getCategoryId());
                                                                return new IllegalArgumentException("無效的分類 ID: " + productDto.getCategoryId());
                                                           });
         existingProduct.setCategory(category);
         log.debug("Set Category {} on product {}", category.getName(), existingProduct.getName());


        existingProduct.setName(productDto.getName());
        existingProduct.setUnitPrice(productDto.getUnitPrice());
        // 【修正】更新其他屬性
        if (productDto.getStock() != null) {
             existingProduct.setStock(productDto.getStock());
        }
        if (productDto.getDescription() != null) {
             existingProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getIsActive() != null) {
             existingProduct.setIsActive(productDto.getIsActive());
        }
        // TODO: 如果 ProductDTO 有 startTime 和 endTime，需要進行解析並設定到 Entity
        // if (productDto.getStartTime() != null) {
        //      existingProduct.setStartTime(productDto.getStartTime()); // 假設 DTO 中是 LocalDateTime
        // }
        // if (productDto.getEndTime() != null) {
        //      existingProduct.setEndTime(productDto.getEndTime()); // 假設 DTO 中是 LocalDateTime
        // }


        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return updatedProduct;
    }


    /** 刪除商品 */
    @Override
    @Transactional // 刪除操作通常需要事務支援
    public void deleteProduct(Integer id) {
         log.info("Attempting to delete product with ID: {}", id);
         Product productToDelete = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for deletion. ID: {}", id);
                return new IllegalArgumentException("商品不存在，無法刪除。ID: " + id);
            });
         log.info("Found product for deletion: ID={}, Name={}", id, productToDelete.getName());

        // 先刪除該商品相關的所有媒體
        try {
            log.info("Calling ProductMediaService to delete media for product ID: {}", id);
            productMediaService.deleteAllByProductId(id); // 呼叫 ProductMediaService 刪除媒體
            log.info("Successfully called ProductMediaService.deleteAllByProductId for product ID: {}", id);
        } catch (Exception e) {
            // 如果媒體刪除失敗，記錄錯誤並重新拋出 RuntimeException，中斷事務
            log.error("Error deleting media for product ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete media for product ID " + id, e); // 拋出異常
        }

        // 然後再刪除商品 Entity
        // 只有當上面的媒體刪除沒有拋出異常時，這裡才會執行
        log.info("Deleting product entity with ID: {}", id);
        productRepository.delete(productToDelete);
        log.info("Successfully deleted product entity with ID: {}", id);
    }

    /** 推薦商品（排除自己，取最新前5筆） */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getRecommendedProducts(Integer excludeProductId) {
         log.info("Fetching recommended products excluding product ID: {}", excludeProductId);
         List<Product> recommended = productRepository.findTop5ByIdNotOrderByCreatedAtDesc(excludeProductId);
         log.info("Found {} recommended products.", recommended.size());
         return recommended;
    }


    // ttpp
    @Override
    @Transactional(readOnly = true)
    public List<Product> findByIds(List<Integer> ids) {
         log.info("Finding products by IDs: {}", ids);
        if (ids == null || ids.isEmpty()) {
             log.info("Input ID list is null or empty, returning empty list.");
            return List.of();
        }
         // 將 List<Integer> 轉換為 Set<Integer> 以用於 findByIdIn， Set 查詢效率通常更高
         Set<Integer> idSet = new java.util.HashSet<>(ids);
         List<Product> products = productRepository.findByIdIn(idSet);
         log.info("Found {} products for provided IDs.", products.size());
         return products;
    }

    // 你可能還需要 CategoryService 中根據名稱找分類的方法，如果Product creation/update 需要的話
    // 假設 CategoryService 有 findByName 方法
    // private Optional<Category> getCategoryByName(String categoryName) {
    //      return eeit198productCategoryService.findByName(categoryName);
    // }
     // 假設 BrandService 有 findBrandByName 方法
    // private Optional<Brand> findBrandByName(String brandName) {
    //      return llBrandService.findBrandByName(brandName); // 確保 llBrandService 有這個方法
    // }


}
