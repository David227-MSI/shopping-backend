//package tw.eeits.unhappy.ll.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 靜態資源路徑映射：讓 /uploads/** 指向實體的 uploads 資料夾
//        registry
//                .addResourceHandler("/uploads/**")
//                .addResourceLocations("file:uploads/");
//    }
//}
//
//// 舊的JWT攔截器設定，現在是用Filter攔截器，繼承也不同，不能直接用
//
//// package tw.eeits.unhappy.ll.config;
//
//// import org.springframework.beans.factory.annotation.Autowired;
//// import org.springframework.context.annotation.Configuration;
//// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//// import lombok.RequiredArgsConstructor;
//// import tw.eeits.unhappy.ll.security.JwtAuthenticationFilter;
//
//// @Configuration
//// @RequiredArgsConstructor
//// public class WebConfig implements WebMvcConfigurer {
//
//// private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//// @Override
//// public void addInterceptors(InterceptorRegistry registry) {
//// registry.addInterceptor(jwtAuthenticationFilter)
//// .addPathPatterns("/api/**") // 所有 API 都攔
//// .excludePathPatterns("/api/admin/login"); // 登入本身不用驗證
//// }
//// }
