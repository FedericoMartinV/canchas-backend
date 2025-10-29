package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import model.Cancha;
import model.Usuario;
import repository.CanchaRepository;
import repository.UsuarioRepository;

@Component
public class DataLoader implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    
    private final UsuarioRepository usuarioRepository;
    private final CanchaRepository canchaRepository;
    
    public DataLoader(UsuarioRepository usuarioRepository, CanchaRepository canchaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.canchaRepository = canchaRepository;
    }
    
    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Cargando datos iniciales...");
            
            // Usuarios de ejemplo
            Usuario usuario1 = new Usuario();
            usuario1.setNombre("Carlos");
            usuario1.setApellido("Rodriguez");
            usuario1.setEmail("carlos@example.com");
            usuario1.setTelefono("261-1234567");
            
            Usuario usuario2 = new Usuario();
            usuario2.setNombre("Maria");
            usuario2.setApellido("Gonzalez");
            usuario2.setEmail("maria@example.com");
            usuario2.setTelefono("261-7654321");
            
            usuarioRepository.save(usuario1);
            usuarioRepository.save(usuario2);
            
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setEmail("admin@canchas.com");
            admin.setTelefono("261-0000000");
            usuarioRepository.save(admin);
            
            // Canchas de ejemplo
            Cancha cancha1 = new Cancha();
            cancha1.setNombre("Cancha Central");
            cancha1.setUbicacion("Av. San Martin 500, Godoy Cruz");
            cancha1.setTipo("Futbol 5");
            cancha1.setPrecioPorHora(15000.0);
            cancha1.setIluminacion(true);
            
            Cancha cancha2 = new Cancha();
            cancha2.setNombre("Cancha Norte");
            cancha2.setUbicacion("Calle Las Heras 200, Godoy Cruz");
            cancha2.setTipo("Futbol 7");
            cancha2.setPrecioPorHora(20000.0);
            cancha2.setIluminacion(true);
            
            Cancha cancha3 = new Cancha();
            cancha3.setNombre("Cancha Sur");
            cancha3.setUbicacion("Av. España 1000, Godoy Cruz");
            cancha3.setTipo("Futbol 11");
            cancha3.setPrecioPorHora(35000.0);
            cancha3.setIluminacion(false);
            
            canchaRepository.save(cancha1);
            canchaRepository.save(cancha2);
            canchaRepository.save(cancha3);
            
            log.info("Datos iniciales cargados correctamente");
        }
    }
}