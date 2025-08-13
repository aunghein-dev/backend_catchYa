package com.catch_ya_group.catch_ya.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Catch-Ya Spring Boot API",
                contact = @Contact(
                        name = "AHT",
                        email = "aunghein.mailer@gmail.com",
                        url = "https://github.com/aunghein-dev"
                ),
                description = "API documentation for Catch-Ya Project",
                version = "0.1 demo",
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )

        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://api.catchya.online"
                )
        }
)

@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication provided",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//
//                .info(new Info()
//                        .title("Catch-Ya API")
//                        .version("1.0")
//                        .description("API documentation for Catch-Ya Project")
//                        .contact(new Contact()
//                                .name("Hein Thant")
//                                .email("aunghein.mailer@gmail.com")
//                                .url("https://github.com/aunghein-dev"))
//                        .license(new License()
//                                .name("Apache 2.0")
//                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
//    }
}