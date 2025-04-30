package tw.eeits.unhappy.ll.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import tw.eeits.unhappy.ll.security.JwtAuthenticationFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtAuthenticationFilter);

        // 這是測試用的白名單
        // TODO 改成登入後才能用
        registration.addUrlPatterns("/api/**"); // 只攔截 /api/ 開頭的請求
        registration.setOrder(1); // 優先順序（數字越小越優先）
        return registration;
    }
}




// package tw.eeits.unhappy.ll.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import lombok.RequiredArgsConstructor;
// import tw.eeits.unhappy.ll.security.JwtAuthInterceptor;

// @Configuration
// @RequiredArgsConstructor
// public class WebConfig implements WebMvcConfigurer {

//     private final JwtAuthInterceptor jwtAuthInterceptor;

//     @Override
//     public void addInterceptors(InterceptorRegistry registry) {
//         registry.addInterceptor(jwtAuthInterceptor)
//                 .addPathPatterns("/api/**")               // 所有 API 都攔
//                 .excludePathPatterns("/api/admin/login"); // 登入本身不用驗證
//     }
// }

