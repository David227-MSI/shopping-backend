// package tw.eeits.unhappy.ra._config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class SpringBootConfig implements WebMvcConfigurer {

// 	// 如果有CORS問題在下面添加路徑白名單
//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         String[] allowedPaths = {
//             "/api/media/**",
//             "/api/reviews/**"
//             // 允許路徑貼在這
//         };

//         for (String path : allowedPaths) {
//             registry.addMapping(path)
//                     .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
//                     .allowedOrigins("*")
//                     .allowedHeaders("*")
//                     .allowCredentials(false);
//         }
//     }
// }

