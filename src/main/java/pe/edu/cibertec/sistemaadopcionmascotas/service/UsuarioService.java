
package pe.edu.cibertec.sistemaadopcionmascotas.service;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(String username, String password) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));

        return usuarioRepository.save(usuario);
    }

    public boolean validarCredenciales(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> passwordEncoder.matches(
                        password, usuario.getPassword()))
                .orElse(false);
    }
}
