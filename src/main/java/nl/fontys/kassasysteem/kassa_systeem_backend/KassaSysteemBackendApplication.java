package nl.fontys.kassasysteem.kassa_systeem_backend;

import nl.fontys.kassasysteem.kassa_systeem_backend.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication

@EnableConfigurationProperties(JwtProperties.class)
public class KassaSysteemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KassaSysteemBackendApplication.class, args);
	}

}
