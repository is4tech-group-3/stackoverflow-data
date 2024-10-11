package com.stackoverflow.config;

import com.stackoverflow.repository.UserRepository;
import com.stackoverflow.service.audit.AuditService;
import com.stackoverflow.util.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AuditService auditService;
    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @Bean
    public AuditInterceptor auditInterceptor() {
        return new AuditInterceptor(auditService, currentUser, userRepository);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean<AuditFilter> contentCachingFilter() {
        FilterRegistrationBean<AuditFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuditFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
