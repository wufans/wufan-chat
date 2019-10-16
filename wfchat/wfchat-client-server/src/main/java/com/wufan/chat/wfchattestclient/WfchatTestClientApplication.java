package com.wufan.chat.wfchattestclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务消费者，对外暴露http接口的模块
 */
@ImportResource({"classpath:dubbo-consumer.xml"})
@Configuration
@SpringBootApplication
public class WfchatTestClientApplication implements ServletContextInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WfchatTestClientApplication.class, args);
    }
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "1024000");
    }



    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
        corsConfiguration.addAllowedHeader("*"); // 2允许任何头
        corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        List<String> listOrigins = new ArrayList<>();
//        list.add("http://localhost:8081");
        listOrigins.add("http://localhost:8080");
        corsConfiguration.setAllowedOrigins(listOrigins);

        List<String> listHeaders = new ArrayList<>();
        listHeaders.add("Content-Type");
        listHeaders.add("Access-Token");
        corsConfiguration.setAllowedHeaders(listHeaders);

        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
