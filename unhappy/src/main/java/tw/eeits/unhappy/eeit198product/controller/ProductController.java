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
    public ResponseEntity<ApiRes<List<Product>>> getProducts(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer brand,
            @RequestParam(required = false) String search
    ) {
        List<Product> products = productService.searchProducts(category, brand, search);
        return ResponseEntity.ok(ResponseFactory.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiRes<ProductDTO>> getProductById(@PathVariable Integer id) {
        System.out.println("Received GET request for product ID: " + id);
        try {
            ProductDTO productDTO = productService.getProductDetailsWithImages(id);
            if (productDTO == null) {
                System.out.println("Product with ID " + id + " not found by service.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail("商品不存在"));
            }
            System.out.println("Successfully retrieved product ID: " + id);
            return ResponseEntity.ok(ResponseFactory.success(productDTO));
        } catch (Exception e) {
            System.err.println("Error fetching product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("載入商品失敗，請稍後再試"));
        }
    }

    @GetMapping("/{id}/recommended")
    public ResponseEntity<ApiRes<List<Product>>> getRecommendedProducts(@PathVariable Integer id) {
        List<Product> recommended = productService.getRecommendedProducts(id);
        return ResponseEntity.ok(ResponseFactory.success(recommended));
    }

    /* ---------- 【新增】商品建立 Endpoint ---------- */
    @PostMapping
    public ResponseEntity<ApiRes<ProductDTO>> createProduct(@RequestBody ProductDTO productDto) {
        try {
            Product createdProductEntity = productService.createProduct(productDto);
            ProductDTO createdProductDTO = productService.getProductDetailsWithImages(createdProductEntity.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseFactory.success(createdProductDTO));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("商品建立失敗，請稍後再試"));
        }
    }

    /* ---------- 【新增】商品修改 Endpoint ---------- */
    @PutMapping("/{id}")
    public ResponseEntity<ApiRes<ProductDTO>> updateProduct(@PathVariable Integer id, @RequestBody ProductDTO productDto) {
        try {
            Product updatedProductEntity = productService.updateProduct(id, productDto);
            ProductDTO updatedProductDTO = productService.getProductDetailsWithImages(updatedProductEntity.getId());
            return ResponseEntity.ok(ResponseFactory.success(updatedProductDTO));
        
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseFactory.fail(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("商品更新失敗，請稍後再試"));
        }
    }

    /* ---------- 【新增】商品刪除 Endpoint ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiRes<Void>> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ResponseFactory.success(null));
        
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseFactory.fail(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error deleting product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("商品刪除失敗，請稍後再試"));
        }
    }

    @GetMapping("/byIds")
    public ResponseEntity<ApiRes<List<Product>>> getProductsByIds(@RequestParam List<Integer> ids) {
        try {
            List<Product> products = productService.findByIds(ids);
            return ResponseEntity.ok(ResponseFactory.success(products));
        
        } catch (Exception e) {
            System.err.println("Error fetching products by IDs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseFactory.fail("依 ID 載入商品失敗，請稍後再試"));
        }
    }
}
