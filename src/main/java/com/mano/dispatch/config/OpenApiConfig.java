
package com.mano.dispatch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dispatchOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("FreightFox - Dispatch Load Balancer API")
                        .description(
                                "REST APIs for managing delivery orders, "
                                        + "vehicles, and automated dispatch assignments. "
                                        + "The system prioritizes orders and selects "
                                        + "suitable vehicles based on capacity and distance."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mano R")
                                .email("manoranganathan10@gmail.com")
                        )
                );
    }
}
