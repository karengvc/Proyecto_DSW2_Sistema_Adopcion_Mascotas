
package pe.edu.cibertec.sistemaadopcionmascotas.controller;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import pe.edu.cibertec.sistemaadopcionmascotas.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String correo = datos.get("correo");
            String password = datos.get("password");

            if (username == null || username.isBlank()
                    || correo == null || correo.isBlank()
                    || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensaje", "Usuario, correo y contraseña son obligatorios"));
            }

            usuarioService.registrarUsuario(username, correo, password);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("mensaje",
                            "Te enviamos un código a tu correo. Verifica tu cuenta para iniciar sesión"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("mensaje",
                            "No se pudo completar el registro o enviar el correo. Verifica la configuración de Gmail."));
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<?> verificar(@RequestBody Map<String, String> datos) {
        try {
            String correo = datos.get("correo");
            String codigo = datos.get("codigo");

            if (correo == null || correo.isBlank()
                    || codigo == null || codigo.isBlank()) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensaje", "Correo y código son obligatorios"));
            }

            usuarioService.verificarCorreo(correo, codigo);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Correo verificado. Ya puedes iniciar sesión"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> datos,
            HttpSession session) {

        String username = datos.get("username");
        String password = datos.get("password");

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", "Usuario y contraseña son obligatorios"));
        }

        return usuarioService.autenticarUsuario(username, password)
                .map(usuario -> {
                    session.setAttribute("usuarioId", usuario.getId());
                    session.setAttribute("usuarioUsername", usuario.getUsername());
                    session.setAttribute("usuarioRol", usuario.getRol());
                    session.setAttribute("usuarioCorreo", usuario.getCorreo());

                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Inicio de sesión correcto");
                    respuesta.put("username", usuario.getUsername());
                    respuesta.put("rol", usuario.getRol());
                    respuesta.put("correo", usuario.getCorreo() == null ? "" : usuario.getCorreo());

                    return ResponseEntity.ok(respuesta);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Map.of("mensaje",
                                "Usuario o contraseña incorrectos, o correo aún sin verificar")));
    }

    @GetMapping("/sesion")
    public ResponseEntity<?> sesion(HttpSession session) {
        Object usuarioId = session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "No hay una sesión iniciada"));
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", usuarioId);
        respuesta.put("username", session.getAttribute("usuarioUsername"));
        respuesta.put("rol", session.getAttribute("usuarioRol"));
        Object correo = session.getAttribute("usuarioCorreo");
        respuesta.put("correo", correo == null ? "" : correo);

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(
                Map.of("mensaje", "Sesión cerrada correctamente"));
    }
}
