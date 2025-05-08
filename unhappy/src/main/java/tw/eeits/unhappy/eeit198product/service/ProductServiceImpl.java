package tw.eeits.unhappy.eeit198product.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDateTime;

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
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository; // 【新增】引入 ProductMediaRepository

// 注意：這裡的 BrandService 應該是從 ll 模組引入
import tw.eeits.unhappy.ll.service.BrandService;
import tw.eeits.unhappy.eeit198product.service.CategoryService;
import tw.eeits.unhappy.ra.media.service.ProductMediaService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMediaRepository productMediaRepository; // 【新增】注入 ProductMediaRepository

    @Autowired
    private BrandService llBrandService; // ll 模組的 BrandService

    @Autowired
    private CategoryService eeit198productCategoryService; // eeit198product 模組的 CategoryService

    @Autowired
    private ProductMediaService productMediaService;

    /** 取得所有商品（無條件搜尋） */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        log.info("Fetching all products.");
        List<Product> products = productRepository.findAll();
        log.info("Found {} total products.", products.size());
        return products.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }

    /** 依條件搜尋商品（categoryId、brandId、keyword 可為 null） */
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(Integer categoryId, Integer brandId, String searchKeyword) {
        log.info("Searching products with categoryId={}, brandId={}, searchKeyword={}", categoryId, brandId, searchKeyword);
        List<Product> products = productRepository.searchByCondition(categoryId, brandId, searchKeyword);
        log.info("Found {} products matching search criteria.", products.size());
        return products.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
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

        // 獲取相關媒體（圖片）
        List<ProductMedia> mediaList = productMediaRepository.findByProductId(id);
        log.debug("Found {} media records for product ID {}", mediaList.size(), id);

        // 將媒體 Entity 轉換為媒體 DTO，並按 mediaOrder 排序
        List<ProductMediaDto> mediaDtos = mediaList.stream()
                                                    .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                                                    .map(ProductMediaDto::from)
                                                    .collect(Collectors.toList());

        ProductDTO dto = convertToDTO(product); // 使用轉換方法填充基本屬性
        dto.setImages(mediaDtos); // 設定圖片列表到 DTO

        log.info("Finished fetching product details with images for product ID: {}", id);
        return dto; // 返回包含圖片的 DTO
    }

    /** 建立新商品 - 接收 DTO 並返回 Entity */
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDto) {
         log.info("Creating new product from DTO: {}", productDto.getName());

         // 將 ProductDTO 轉換為 Product Entity
         Product product = new Product();
         product.setName(productDto.getName());
         product.setUnitPrice(productDto.getUnitPrice());
         product.setDescription(productDto.getDescription());

         product.setIsActive(productDto.getIsActive() != null ? productDto.getIsActive() : false);
         product.setStock(productDto.getStock() != null ? productDto.getStock() : 0);
         // TODO: 如果有 startTime 和 endTime，需要從 DTO 設定到 Entity
         // product.setStartTime(productDto.getStartTime());
         // product.setEndTime(productDto.getEndTime());


         // 根據 DTO 中的品牌 ID 找到 Brand Entity 並設定
         if (productDto.getBrandId() == null) {
             log.warn("Brand ID is null in ProductDTO for new product: {}", productDto.getName());
             throw new IllegalArgumentException("品牌 ID 不可為空");
         }
         log.debug("Finding Brand with ID: {}", productDto.getBrandId());
         Brand brand = llBrandService.findBrandById(productDto.getBrandId());
         if (brand == null) {
             log.warn("Brand not found for ID: {}", productDto.getBrandId());
             throw new IllegalArgumentException("無效的品牌 ID: " + productDto.getBrandId());
         }
         product.setBrand(brand);
         log.debug("Set Brand {} on product {}", brand.getName(), productDto.getName());


         // 根據 DTO 中的分類 ID 找到 Category Entity 並設定
         if (productDto.getCategoryId() == null) {
             log.warn("Category ID is null in ProductDTO for new product: {}", productDto.getName());
             throw new IllegalArgumentException("分類 ID 不可為空");
         }
         log.debug("Finding Category with ID: {}", productDto.getCategoryId());
         Category category = eeit198productCategoryService.getCategoryById(productDto.getCategoryId())
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
    @Transactional
    public Product updateProduct(Integer id, ProductDTO productDto) {
        log.info("Updating product with ID {} from DTO: {}", id, productDto.getName());

        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found for update. ID: {}", id);
                return new IllegalArgumentException("商品不存在，無法更新。ID: " + id);
            });
        log.info("Found product for update: ID={}, Name={}", id, existingProduct.getName());


        // 根據 DTO 中的品牌 ID 找到 Brand Entity 並設定
         if (productDto.getBrandId() == null) {
             log.warn("Brand ID is null in ProductDTO for updating product: {}", productDto.getName());
             throw new IllegalArgumentException("品牌 ID 不可為空");
         }
         log.debug("Finding Brand with ID: {}", productDto.getBrandId());
         Brand brand = llBrandService.findBrandById(productDto.getBrandId());
         if (brand == null) {
             log.warn("Brand not found for ID: {}", productDto.getBrandId());
             throw new IllegalArgumentException("無效的品牌 ID: " + productDto.getBrandId());
         }
         existingProduct.setBrand(brand);
         log.debug("Set Brand {} on product {}", brand.getName(), existingProduct.getName());


        // 根據 DTO 中的分類 ID 找到 Category Entity 並設定
         if (productDto.getCategoryId() == null) {
             log.warn("Category ID is null in ProductDTO for updating product: {}", productDto.getName());
             throw new IllegalArgumentException("分類 ID 不可為空");
         }
         log.debug("Finding Category with ID: {}", productDto.getCategoryId());
         Category category = eeit198productCategoryService.getCategoryById(productDto.getCategoryId())
                                                           .orElseThrow(() -> {
                                                                log.warn("Category not found for ID: {}", productDto.getCategoryId());
                                                                return new IllegalArgumentException("無效的分類 ID: " + productDto.getCategoryId());
                                                           });
         existingProduct.setCategory(category);
         log.debug("Set Category {} on product {}", category.getName(), existingProduct.getName());


        existingProduct.setName(productDto.getName());
        existingProduct.setUnitPrice(productDto.getUnitPrice());
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
        //      existingProduct.setStartTime(productDto.getStartTime());
        // }
        // if (productDto.getEndTime() != null) {
        //      existingProduct.setEndTime(productDto.getEndTime());
        // }


        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return updatedProduct;
    }


    /** 刪除商品 */
    @Override
    @Transactional
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
            productMediaService.deleteAllByProductId(id);
            log.info("Successfully called ProductMediaService.deleteAllByProductId for product ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting media for product ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete media for product ID " + id, e);
        }

        // 然後再刪除商品 Entity
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
         Set<Integer> idSet = new java.util.HashSet<>(ids);
         List<Product> products = productRepository.findByIdIn(idSet);
         log.info("Found {} products for provided IDs.", products.size());
         return products;
    }


    /**
     * 將 Product Entity 轉換為 ProductDTO
     * @param product 要轉換的 Product Entity
     * @return 轉換後的 ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setDescription(product.getDescription());

        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getName());
        } else {
             dto.setBrandId(null);
             dto.setBrandName("未知品牌");
        }

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        } else {
             dto.setCategoryId(null);
             dto.setCategoryName("未知分類");
        }

        dto.setIsActive(product.getIsActive());
        dto.setStock(product.getStock());
        // TODO: 如果 Product Entity 有 startTime 和 endTime，也需要在這裡設定到 DTO
        // dto.setStartTime(product.getStartTime());
        // dto.setEndTime(product.getEndTime());

        // 【新增】獲取商品的主圖 URL 並設定到 DTO
        // 這裡只取第一張標記為主圖的圖片 URL
        Optional<ProductMedia> mainMedia = productMediaRepository.findFirstByProductIdAndIsMainTrue(product.getId());
        if (mainMedia.isPresent()) {
            dto.setMainImageUrl(mainMedia.get().getMediaUrl());
            log.debug("Set main image URL for product ID {}: {}", product.getId(), dto.getMainImageUrl());
        } else {
            // 如果沒有主圖，可以設定一個預設圖片的 URL
            dto.setMainImageUrl("https://via.placeholder.com/100x60?text=No+Image");
            log.debug("No main image found for product ID {}, using placeholder.", product.getId());
        }


        // 注意：這裡不設定 images 列表，因為 findAll/searchProducts 通常不需要圖片列表
        // 只有 getProductDetailsWithImages 方法會額外獲取並設定 images

        return dto;
    }

    // 你之前在 ProductServiceImpl.java 中有這個方法，但 BrandService 在 ll 模組，需要確認引入
    // Optional<Brand> findBrandByName(String brandName) {
    //      return llBrandService.findByName(brandName);
    // }

    // 你可能還需要 CategoryService 中根據名稱找分類的方法，如果Product creation/update 需要的話
    // Optional<Category> getCategoryByName(...) {
    //      return eeit198productCategoryService.findByName(...);
    // }


}
