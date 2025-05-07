package tw.eeits.unhappy.eeit198product.service;

import java.util.List;
import java.util.Optional;

import tw.eeits.unhappy.eeit198product.dto.ProductDTO;
import tw.eeits.unhappy.eeit198product.entity.Product;

public interface ProductService {

    /** 取得所有商品（無條件搜尋） */
    List<Product> findAll();

    /** 依條件搜尋商品（categoryId、brandId、keyword 可為 null） */
    List<Product> searchProducts(Integer categoryId, Integer brandId, String searchKeyword);

    /** 取得單一商品（Optional 包裝，可避免 null） */
    Optional<Product> getProductById(Integer id);

    /** 取得單一商品（包含圖片資訊，返回 DTO） */
    ProductDTO getProductDetailsWithImages(Integer id);

    /** 建立商品 - 接收 DTO 並返回 Entity */
    Product createProduct(ProductDTO productDto); // 接收 ProductDTO

    /** 刪除商品 */
    void deleteProduct(Integer id);

    /** 推薦商品（排除自己，取最新前5筆） */
    List<Product> getRecommendedProducts(Integer excludeProductId);

    /** 更新商品 - 接收 ID 和 DTO 並返回 Entity */
    Product updateProduct(Integer id, ProductDTO productDto); // 接收 ID 和 ProductDTO

    // ttpp
    public List<Product> findByIds(List<Integer> ids);

}
