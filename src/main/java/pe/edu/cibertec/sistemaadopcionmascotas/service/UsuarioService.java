
package pe.edu.cibertec.sistemaadopcionmascotas.service;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;
    private final SecureRandom random = new SecureRandom();

    private static final Pattern CORREO_VALIDO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    @Transactional
    public void registrarUsuario(String username, String correo, String password) {
        username = username.trim();
        correo = correo.trim().toLowerCase(Locale.ROOT);

        if (!CORREO_VALIDO.matcher(correo).matches()) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido");
        }

        if (password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener 8 caracteres e incluir mayúscula, minúscula, número y símbolo");
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        if (usuarioRepository.findByCorreo(correo).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setCorreoVerificado(false);
        usuario.setRol("USUARIO");

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        usuario.setCodigoVerificacion(codigo);
        usuario.setExpiracionCodigo(LocalDateTime.now().plusMinutes(10));

        usuarioRepository.save(usuario);
        correoService.enviarCodigo(correo, codigo);
    }

    @Transactional
    public void verificarCorreo(String correo, String codigo) {
        Usuario usuario = usuarioRepository.findByCorreo(
                        correo.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe una cuenta con ese correo"));

        if (usuario.isCorreoVerificado()) {
            throw new IllegalArgumentException("El correo ya está verificado");
        }

        if (usuario.getExpiracionCodigo() == null
                || LocalDateTime.now().isAfter(usuario.getExpiracionCodigo())) {
            throw new IllegalArgumentException(
                    "El código venció. Regístrate nuevamente para recibir otro");
        }

        if (usuario.getCodigoVerificacion() == null
                || !usuario.getCodigoVerificacion().equals(codigo.trim())) {
            throw new IllegalArgumentException("El código de verificación es incorrecto");
        }

        usuario.setCorreoVerificado(true);
        usuario.setCodigoVerificacion(null);
        usuario.setExpiracionCodigo(null);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> autenticarUsuario(String username, String password) {
        return usuarioRepository.findByUsername(username.trim())
                .filter(Usuario::isCorreoVerificado)
                .filter(usuario -> passwordEncoder.matches(password, usuario.getPassword()));
    }

    public boolean validarCredenciales(String username, String password) {
        return autenticarUsuario(username, password).isPresent();
    }
}
