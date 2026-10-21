
package pe.edu.cibertec.sistemaadopcionmascotas.controller;

import jakarta.servlet.http.HttpSession;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.SolicitudAdopcion;
import pe.edu.cibertec.sistemaadopcionmascotas.service.SolicitudAdopcionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudAdopcionController {

    private final SolicitudAdopcionService solicitudService;

    public SolicitudAdopcionController(SolicitudAdopcionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Map<String, Object> datos,
            HttpSession session) {

        Long usuarioId = obtenerUsuarioId(session);

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Inicia sesión para solicitar una adopción"));
        }

        try {
            SolicitudAdopcion solicitud = solicitudService.crear(usuarioId, datos);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Solicitud registrada. Tu solicitud está pendiente de revisión.",
                    "id", solicitud.getId(),
                    "estado", solicitud.getEstado()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<?> misSolicitudes(HttpSession session) {
        Long usuarioId = obtenerUsuarioId(session);

        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Inicia sesión para consultar tus solicitudes"));
        }

        return ResponseEntity.ok(solicitudService.listarPorUsuario(usuarioId));
    }

    @GetMapping
    public ResponseEntity<?> listarTodas(HttpSession session) {
        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "No tienes permisos de administrador"));
        }

        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable Long id,
            HttpSession session) {

        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "No tienes permisos de administrador"));
        }

        try {
            SolicitudAdopcion solicitud = solicitudService.aprobar(id);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud aprobada correctamente",
                    "id", solicitud.getId(),
                    "estado", solicitud.getEstado()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos,
            HttpSession session) {

        if (!esAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("mensaje", "No tienes permisos de administrador"));
        }

        try {
            String motivo = datos.get("motivo");
            SolicitudAdopcion solicitud = solicitudService.rechazar(id, motivo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Solicitud rechazada correctamente",
                    "id", solicitud.getId(),
                    "estado", solicitud.getEstado()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("usuarioRol"));
    }

    private Long obtenerUsuarioId(HttpSession session) {
        Object id = session.getAttribute("usuarioId");

        if (id instanceof Number numero) {
            return numero.longValue();
        }

        return null;
    }
}
