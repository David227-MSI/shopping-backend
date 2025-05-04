// package tw.eeits.unhappy.eeit198product.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import tw.eeits.unhappy.eeit198product.dto.ProductReviewDTO;
// import tw.eeits.unhappy.eeit198product.service.ProductReviewService;

// import java.util.List;

// @RestController
// @RequestMapping("/api/products")
// @CrossOrigin(origins = "http://localhost:5173")
// public class ProductReviewController {

//     @Autowired
//     private ProductReviewService productReviewService;

//     @GetMapping("/{id}/reviews")
//     public List<ProductReviewDTO> getReviewsByProductId(@PathVariable Integer id) {
//         return productReviewService.findByProductId(id);
//     }
// }
