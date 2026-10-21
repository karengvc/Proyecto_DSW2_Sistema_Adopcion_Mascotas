
package pe.edu.cibertec.sistemaadopcionmascotas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.UsuarioRepository;

@Configuration
public class CrearAdministrador {

    @Bean
    public CommandLineRunner crearCuentaAdministrador(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String username = "admin";

            Usuario admin = usuarioRepository.findByUsername(username)
                    .orElseGet(() -> {
                        Usuario nuevo = new Usuario();
                        nuevo.setUsername(username);
                        nuevo.setCorreo("admin@adoptapet.com");
                        return nuevo;
                    });

            admin.setPassword(passwordEncoder.encode("AdminPet2026*"));
            admin.setCorreoVerificado(true);
            admin.setCodigoVerificacion(null);
            admin.setExpiracionCodigo(null);
            admin.setRol("ADMIN");

            usuarioRepository.save(admin);

            System.out.println("======================================");
            System.out.println("ADMINISTRADOR CONFIGURADO CORRECTAMENTE");
            System.out.println("Usuario: admin");
            System.out.println("Contraseña: AdminPet2026*");
            System.out.println("Rol: ADMIN");
            System.out.println("======================================");
        };
    }
}
