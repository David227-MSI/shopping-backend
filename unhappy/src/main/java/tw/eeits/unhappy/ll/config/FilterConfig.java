package tw.eeits.unhappy.ll.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import tw.eeits.unhappy.ll.security.JwtAuthenticationFilter;
// import tw.eeits.unhappy.ttpp.userMember.jwt.UserJwtFilter;

@Configuration
public class FilterConfig {

    // 註冊後台Admin JWT filter
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

    // 註冊前台User JWT filter
    // @Bean
    // public FilterRegistrationBean<UserJwtFilter> userFilterRegistration(UserJwtFilter userJwtFilter) {
    //     FilterRegistrationBean<UserJwtFilter> registration = new FilterRegistrationBean<>();
    //     registration.setFilter(userJwtFilter);
    //     registration.addUrlPatterns("/api/user/*"); // 只攔截前台用戶API
    //     registration.setOrder(2); // 記得設定與 admin filter 不同的順序
    //     return registration;
    // }




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

