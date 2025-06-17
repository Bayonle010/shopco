package com.shopco.core.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =  @Info(
                contact = @Contact(name = "BayFi ltd", url = "?", email = "support@bayfi.com"),
                title = "OpenAI specification: BayFi",
                version = "1.0",
                license = @License(name = "MIT license", url = "https://choosealicense.com/licenses/mit/"),
                termsOfService = "Terms of Service"
        ),


        servers = {
                @Server(description = "localhost server ", url = "http://localhost:8000"),
                @Server(description = "localhost server for docker", url = "http://localhost:8080"),
                @Server(description = "Test server", url = "https://bayfi.onrender.com")
        },
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecurityScheme(name = "bearerAuth", description = "JWT authentication description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
