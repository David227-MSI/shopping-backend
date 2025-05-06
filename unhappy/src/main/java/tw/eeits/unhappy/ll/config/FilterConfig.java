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





// 舊的JWT攔截器設定，是寫在WebConfig，繼承不同，不能直接用