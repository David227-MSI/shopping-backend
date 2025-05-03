package tw.eeits.unhappy.ll.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import tw.eeits.unhappy.ll.security.JwtAuthenticationFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);

        // 僅攔後台管理 API
        // registrationBean.addUrlPatterns("/api/admin/*");
        registrationBean.addUrlPatterns("/api/admin/*", "/api/admin/*/*", "/api/admin/*/*/*");


        // 設定執行順序，數字越小越優先
        registrationBean.setOrder(1);

        return registrationBean;
    }
}


// package tw.eeits.unhappy.ll.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import lombok.RequiredArgsConstructor;
// import tw.eeits.unhappy.ll.security.JwtAuthenticationFilter;

// @Configuration
// @RequiredArgsConstructor
// public class WebConfig implements WebMvcConfigurer {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     @Override
//     public void addInterceptors(InterceptorRegistry registry) {
//         registry.addInterceptor(jwtAuthenticationFilter)
//                 .addPathPatterns("/api/**") // 所有 API 都攔
//                 .excludePathPatterns("/api/admin/login"); // 登入本身不用驗證
//     }
// }
