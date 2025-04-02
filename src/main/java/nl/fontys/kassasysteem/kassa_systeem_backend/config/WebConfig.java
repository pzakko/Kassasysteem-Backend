package nl.fontys.kassasysteem.kassa_systeem_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // Vue draait op deze poort
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
