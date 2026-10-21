
package pe.edu.cibertec.sistemaadopcionmascotas.repository;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByCorreo(String correo);
}
