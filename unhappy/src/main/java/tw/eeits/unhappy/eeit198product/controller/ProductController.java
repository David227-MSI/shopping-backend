package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.eeits.unhappy.eeit198product.dto.ProductDTO;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.service.ProductService;
import tw.eeits.unhappy.ra._response.ApiRes;
import tw.eeits.unhappy.ra._response.ResponseFactory;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

      // 🔍 全欄位模糊搜尋（名稱、分類、品牌、屬性值）
    @GetMapping("/search")
    public List<Product> searchAllFields(@RequestParam("keyword") String keyword) {
        return productService.searchAllFields(keyword);
    }

    /** 取得所有商品或依條件搜尋商品（返回 DTO 列表） */
    @GetMapping
    // 【修正】返回類型改為 ResponseEntity<ApiRes<List<ProductDTO>>>
    public ResponseEntity<ApiRes<List<ProductDTO>>> getProducts(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer brand,
            @RequestParam(required = false) String search) {
        // 呼叫 service 獲取 ProductDTO 列表
        List<ProductDTO> productDTOs = productService.searchProducts(category, brand, search);
        // 將 DTO 列表包裝在 ApiRes 中並返回
        return ResponseEntity.ok(ResponseFactory.success(productDTOs));
    }
/** 全欄位關鍵字搜尋（名稱、品牌、分類、父分類、屬性） */
@GetMapping("/fullsearch")
public ResponseEntity<ApiRes<List<ProductDTO>>> fullTextSearch(@RequestParam String keyword) {
    List<ProductDTO> results = productService.searchByKeywordFullText(keyword);
    return ResponseEntity.ok(ResponseFactory.success(results));
}
    /** 取得單一商品詳細資訊（包含圖片，返回 DTO） */
    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<ProductDTO>> getProductById(@PathVariable Integer id) {
        System.out.println("Received GET request for product ID: " + id); // 添加日誌
        try {
            // 呼叫 service 獲取包含詳細資訊和圖片的 ProductDTO
            ProductDTO productDTO = productService.getProductDetailsWithImages(id);
            // service 已經處理找不到商品拋出異常的情況，這裡只需要處理 service 返回的 DTO
            System.out.println("Successfully retrieved product ID: " + id); // 添加日誌
            return ResponseEntity.ok(ResponseFactory.success(productDTO));
        } catch (IllegalArgumentException e) {
            // 捕獲 service 拋出的 IllegalArgumentException (例如商品不存在)
            System.err.println("Error fetching product ID " + id + ": " + e.getMessage()); // 添加日誌
            // 返回 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail(e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error fetching product ID " + id + ": " + e.getMessage()); // 添加日誌
            e.printStackTrace(); // 打印堆棧跟踪
            // 返回 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("獲取商品詳細資訊失敗"));
        } catch (Exception e) { // 捕獲其他可能的異常
            System.err.println("Unexpected error fetching product ID " + id + ": " + e.getMessage()); // 添加日誌
            e.printStackTrace(); // 打印堆棧跟踪
            // 返回 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseFactory.fail("獲取商品詳細資訊時發生未知錯誤"));
        }
    }

    /** 建立新商品 */
    @PostMapping
    public ResponseEntity<ApiRes<Product>> createProduct(@RequestBody ProductDTO productDto) {
        try {
            Product createdProduct = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseFactory.success(createdProduct));
        } catch (IllegalArgumentException e) {
            // 捕獲 service 拋出的 IllegalArgumentException (例如品牌或分類無效)
            System.err.println("Error creating product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseFactory.fail(e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("建立商品失敗"));
        }
    }

    /** 更新商品 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<Product>> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDto) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(ResponseFactory.success(updatedProduct));
        } catch (IllegalArgumentException e) {
            // 捕獲 service 拋出的 IllegalArgumentException (例如商品不存在、品牌或分類無效)
            System.err.println("Error updating product ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseFactory.fail(e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error updating product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("更新商品失敗"));
        }
    }

    /** 刪除商品 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ResponseFactory.success(null)); // 刪除成功返回 success
        } catch (IllegalArgumentException e) {
            // 捕獲 service 拋出的 IllegalArgumentException (例如商品不存在)
            System.err.println("Error deleting product ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail(e.getMessage()));
        } catch (RuntimeException e) {
            System.err.println("Error deleting product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("刪除商品失敗"));
        }
    }

    /** 推薦商品（排除自己，取最新前5筆） */
    @GetMapping("/{id}/recommended")
    public List<Product> getRecommendedProducts(@PathVariable Integer id) {
        return productService.getRecommendedProducts(id);
    }

}
