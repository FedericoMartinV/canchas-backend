package config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI canchasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Canchas Fútbol API")
                        .description("""
                                API REST para el sistema de reservas de canchas de fútbol.
                                
                                Permite gestionar **usuarios**, **canchas** y **reservas**.
                                Al confirmar o cancelar una reserva se dispara el patrón Observer,
                                que notifica automáticamente al usuario y al administrador.
                                
                                > ⚠️ La autenticación de login aún no valida contraseñas (pendiente Spring Security).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Canchas Fútbol")
                                .url("https://github.com/FedericoMartinV/canchas-backend")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local de desarrollo")
                ));
    }
}
