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





// 舊的JWT攔截器設定，是寫在WebConfig，繼承不同，不能直接用