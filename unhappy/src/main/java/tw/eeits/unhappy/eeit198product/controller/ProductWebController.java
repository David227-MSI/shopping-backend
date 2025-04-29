package tw.eeits.unhappy.eeit198product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import tw.eeits.unhappy.eeit198product.entity.Brand;
import tw.eeits.unhappy.eeit198product.entity.Category;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.service.BrandService;
import tw.eeits.unhappy.eeit198product.service.CategoryService;
import tw.eeits.unhappy.eeit198product.service.ProductService;

@Controller
public class ProductWebController {

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String listProducts(Model model) {
        List<Category> categories = categoryService.findAll();
        List<Brand> brands = brandService.findAll();
        List<Product> products = productService.findAll();  // 或 searchProducts(...)
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("products", products);
        return "list";  // 回傳 src/main/resources/templates/list.html
    }
}
