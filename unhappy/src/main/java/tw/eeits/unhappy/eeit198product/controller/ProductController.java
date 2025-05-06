package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer brand,
            @RequestParam(required = false) String search
    ) {
        return productService.searchProducts(category, brand, search);
    }

@GetMapping("/{id}")
public ResponseEntity<ApiRes<ProductDTO>> getProductById(@PathVariable Integer id) {
    System.out.println("Received GET request for product ID: " + id); // 添加日誌
    try {
        ProductDTO productDTO = productService.getProductDetailsWithImages(id);
        if (productDTO == null) { // 檢查 service 是否返回 null 而不是拋異常
            System.out.println("Product with ID " + id + " not found by service."); // 添加日誌
            return ResponseEntity.notFound().build();
        }
        System.out.println("Successfully retrieved product ID: " + id); // 添加日誌
        return ResponseEntity.ok(ResponseFactory.success(productDTO));
    } catch (RuntimeException e) {
        System.err.println("Error fetching product ID " + id + ": " + e.getMessage()); // 添加日誌
        e.printStackTrace(); // 打印堆棧跟踪
        return ResponseEntity.notFound().build();
    } catch (Exception e) { // 捕獲其他可能的異常
         System.err.println("Unexpected error fetching product ID " + id + ": " + e.getMessage()); // 添加日誌
         e.printStackTrace(); // 打印堆棧跟踪
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 返回 500
    }
}

    @GetMapping("/{id}/recommended")
    public List<Product> getRecommendedProducts(@PathVariable Integer id) {
        return productService.getRecommendedProducts(id);
    }
}
