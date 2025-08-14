package com.catch_ya_group.catch_ya.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Catch-Ya Spring Boot API",
                version = "v1.0.0",
                description = "Comprehensive API documentation for Catch-Ya Project",
                contact = @Contact(
                        name = "Aung Hein Thant",
                        email = "aunghein.mailer@gmail.com",
                        url = "https://github.com/aunghein-dev"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production",
                        url = "https://api.catchya.online"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT-based authentication using Bearer token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // No need for additional beans unless you want custom OpenAPI customization.
    // All global configuration and security are handled via annotations.
}
