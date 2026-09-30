package pe.edu.cibertec.sistemaadopcionmascotas.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import pe.edu.cibertec.sistemaadopcionmascotas.service.MascotaService;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public List<Mascota> listar() {
        return mascotaService.listar();
    }

    @PostMapping
    public Mascota guardar(@RequestBody Mascota mascota) {
        return mascotaService.guardar(mascota);
    }

    @PutMapping
    public Mascota actualizar(@RequestBody Mascota mascota) {
        return mascotaService.actualizar(mascota);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
    }
}