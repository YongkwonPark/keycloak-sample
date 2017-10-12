package com.woowahan;

import com.woowahan.commons.web.filter.FilterSecurityInterceptor;
import com.woowahan.commons.web.method.support.KeycloakMethodArgumentResolver;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

public class SampleApplication extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Nullable
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }

    @Nullable
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" };
    }

    @Nullable
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter("utf-8");
        ServletContextRequestLoggingFilter loggingFilter = new ServletContextRequestLoggingFilter();
        KeycloakOIDCFilter keycloakOIDCFilter = new KeycloakOIDCFilter();
        FilterSecurityInterceptor securityInterceptor = new FilterSecurityInterceptor(Collections.singletonList("Role_ServerAccess_fresh-mm-alpha"));

        return new Filter[]{ encodingFilter, loggingFilter, keycloakOIDCFilter, securityInterceptor };
    }


    @Configuration
    public static class AppConfig {

    }

    @Configuration
    @EnableWebMvc
    @ComponentScan
    public static class WebConfig implements WebMvcConfigurer {

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new KeycloakMethodArgumentResolver());
        }

    }

}
