package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; // 引入 RequestParam
import org.springframework.web.bind.annotation.PathVariable; // 【新增】引入 PathVariable

import tw.eeits.unhappy.eeit198product.dto.ProductDTO; // 引入 ProductDTO
import tw.eeits.unhappy.eeit198product.entity.Category;
import tw.eeits.unhappy.eeit198product.entity.Product; // 雖然返回 DTO，但 Entity 可能在其他地方用到
// import tw.eeits.unhappy.ll.model.Brand; // 不再直接使用 Brand Entity
import tw.eeits.unhappy.ll.dto.BrandResponse; // 【新增】引入 BrandResponse
// import tw.eeits.unhappy.eeit198product.service.BrandService; // 這個 BrandService 可能是 eeit198product 模組的，我們需要 ll 模組的
import tw.eeits.unhappy.ll.service.BrandService; // 【修正】確保引入的是 ll 模組的 BrandService
import tw.eeits.unhappy.eeit198product.service.CategoryService;
import tw.eeits.unhappy.eeit198product.service.ProductService;

@Controller
public class ProductWebController {

    @Autowired
    private ProductService productService;

    // 注意：這裡注入的是 ll 模組的 BrandService
    @Autowired
    private tw.eeits.unhappy.ll.service.BrandService llBrandService; // 確保注入的是 ll 模組的 BrandService

    @Autowired
    private CategoryService eeit198productCategoryService; // 確保注入的是 eeit198product 模組的 CategoryService


    /** 顯示商品列表頁面 */
    @GetMapping("/products")
    public String listProducts(
        Model model,
        @RequestParam(required = false) Integer category, // 接收分類 ID 參數
        @RequestParam(required = false) Integer brand,    // 接收品牌 ID 參數
        @RequestParam(required = false) String search     // 接收搜尋關鍵字參數
    ) {
        // 獲取所有分類和品牌，用於前端下拉選單
        List<Category> categories = eeit198productCategoryService.getAllCategories(); // 假設 CategoryService 有 getAllCategories 方法
        // 【修正】接收 List<BrandResponse>
        List<BrandResponse> brands = llBrandService.findAll(); // 呼叫 ll 模組的 BrandService.findAll()

        // 根據參數呼叫 service 獲取 ProductDTO 列表
        // 使用 searchProducts 方法，它可以處理 null 參數
        List<ProductDTO> products = productService.searchProducts(category, brand, search);

        // 將數據添加到 Model 中，傳遞給視圖
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands); // 【修正】現在傳遞的是 List<BrandResponse>
        model.addAttribute("products", products); // 現在傳遞的是 List<ProductDTO>

        // 返回對應的視圖名稱 (例如 Thymeleaf 模板名稱)
        return "product/list"; // 假設你的模板檔案在 src/main/resources/templates/product/list.html
    }

    // 示例：顯示新增商品頁面
    @GetMapping("/products/new")
    public String showAddProductForm(Model model) {
        // 獲取所有分類和品牌，用於前端下拉選單
        List<Category> categories = eeit198productCategoryService.getAllCategories();
        // 【修正】接收 List<BrandResponse>
        List<BrandResponse> brands = llBrandService.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands); // 【修正】現在傳遞的是 List<BrandResponse>
        model.addAttribute("product", new ProductDTO()); // 傳遞一個空的 DTO 給表單綁定

        return "product/add"; // 假設你的模板檔案在 src/main/resources/templates/product/add.html
    }

    // 示例：顯示編輯商品頁面
    @GetMapping("/products/edit/{id}")
    // 【新增】引入 PathVariable
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        try {
            // 獲取商品詳細資訊 (包含圖片，返回 DTO)
            ProductDTO productDTO = productService.getProductDetailsWithImages(id);

            // 獲取所有分類和品牌，用於前端下拉選單
            List<Category> categories = eeit198productCategoryService.getAllCategories();
            // 【修正】接收 List<BrandResponse>
            List<BrandResponse> brands = llBrandService.findAll();

            model.addAttribute("categories", categories);
            model.addAttribute("brands", brands); // 【修正】現在傳遞的是 List<BrandResponse>
            model.addAttribute("product", productDTO); // 傳遞包含現有數據的 DTO 給表單綁定

            return "product/edit"; // 假設你的模板檔案在 src/main/resources/templates/product/edit.html

        } catch (IllegalArgumentException e) {
            // 處理商品不存在的情況，例如導向錯誤頁面或列表頁
            // 這裡簡單地導向列表頁並顯示錯誤訊息
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/products"; // 重定向到商品列表頁
        }
    }

    // TODO: 如果你的 Web 頁面需要處理表單提交，還需要對應的 @PostMapping 和 @PutMapping 方法
    // 這些方法會接收表單數據，呼叫 productService 的 createProduct 或 updateProduct 方法，
    // 然後重定向到列表頁或其他頁面。

}
