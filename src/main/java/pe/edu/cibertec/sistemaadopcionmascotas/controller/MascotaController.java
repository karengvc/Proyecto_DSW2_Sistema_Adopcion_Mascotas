
package pe.edu.cibertec.sistemaadopcionmascotas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import pe.edu.cibertec.sistemaadopcionmascotas.service.MascotaService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public List<Mascota> listar() {
        return mascotaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mascota> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mascotaService.obtener(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardar(
            @RequestPart("mascota") Mascota mascota,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {

        try {
            validarMascota(mascota);

            if (foto != null && !foto.isEmpty()) {
                validarFoto(foto);
                mascota.setFoto(foto.getBytes());
                mascota.setTipoFoto(foto.getContentType());
            }

            mascota.setId(null);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mascotaService.guardar(mascota));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje",
                            "No se pudo procesar la fotografía."));
        }
    }

    // Actualizar datos y, opcionalmente, reemplazar la fotografía.
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarConFoto(
            @PathVariable Long id,
            @RequestPart("mascota") Mascota mascota,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {

        try {
            validarMascota(mascota);
            mascota.setId(id);

            byte[] bytesFoto = null;
            String tipoFoto = null;

            if (foto != null && !foto.isEmpty()) {
                validarFoto(foto);
                bytesFoto = foto.getBytes();
                tipoFoto = foto.getContentType();
            }

            return ResponseEntity.ok(
                    mascotaService.actualizar(mascota, bytesFoto, tipoFoto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje",
                            "No se pudo procesar la fotografía."));
        }
    }

    // Se conserva el endpoint de actualización mediante JSON.
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizar(@RequestBody Mascota mascota) {
        try {
            validarMascota(mascota);
            return ResponseEntity.ok(mascotaService.actualizar(mascota));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable Long id) {
        try {
            Mascota mascota = mascotaService.obtener(id);
            byte[] foto = mascota.getFoto();

            if (foto == null || foto.length == 0) {
                return ResponseEntity.notFound().build();
            }

            String tipo = mascota.getTipoFoto() != null
                    ? mascota.getTipoFoto()
                    : MediaType.IMAGE_JPEG_VALUE;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, tipo)
                    .body(foto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            mascotaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    private void validarMascota(Mascota mascota) {
        if (mascota.getNombre() == null
                || mascota.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la mascota es obligatorio.");
        }

        if (mascota.getEspecie() == null
                || mascota.getEspecie().isBlank()) {
            throw new IllegalArgumentException(
                    "La especie es obligatoria.");
        }
    }

    private void validarFoto(MultipartFile foto) {
        if (foto.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "La fotografía no debe superar los 5 MB.");
        }

        String tipo = foto.getContentType();

        if (tipo == null
                || !List.of("image/jpeg", "image/png", "image/webp")
                .contains(tipo)) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG, PNG o WEBP.");
        }
    }
}
