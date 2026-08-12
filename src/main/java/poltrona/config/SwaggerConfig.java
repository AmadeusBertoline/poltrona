package poltrona.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Poltrona - Compras de ingressos no seu cinema favorito.")
                        .version("1.0.0")
                        .description("Sistema de vendas de ingressos.")
                        .contact(new Contact()
                                .name("Amadeus Bertoline")
                                .email("amadeusbertoline123@gmail.com")));

            
    }
}