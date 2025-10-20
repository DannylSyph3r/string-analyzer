package dev.slethware.stringanalyzer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Akinola",
						email = "slethware@gmail.com"
				),
				description = "OpenAPI documentation for String Analyzer API",
				title = "String Analyzer API",
				version = "1.0"
		)
)
@SpringBootApplication
public class StringAnalyzerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StringAnalyzerApiApplication.class, args);
	}

}