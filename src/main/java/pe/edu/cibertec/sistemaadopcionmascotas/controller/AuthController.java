
package pe.edu.cibertec.sistemaadopcionmascotas.controller;

import pe.edu.cibertec.sistemaadopcionmascotas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(
            @RequestBody Map<String, String> datos) {

        try {
            String username = datos.get("username");
            String password = datos.get("password");

            if (username == null || username.isBlank()
                    || password == null || password.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje",
                                "Usuario y contraseña son obligatorios"));
            }

            usuarioService.registrarUsuario(username, password);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Usuario registrado correctamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> datos) {

        String username = datos.get("username");
        String password = datos.get("password");

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "Usuario y contraseña son obligatorios"));
        }

        if (usuarioService.validarCredenciales(username, password)) {
            return ResponseEntity.ok(
                    Map.of("mensaje", "Inicio de sesión correcto"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Usuario o contraseña incorrectos"));
    }
}
