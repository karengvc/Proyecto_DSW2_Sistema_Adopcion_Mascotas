
package pe.edu.cibertec.sistemaadopcionmascotas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.MascotaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MascotaRepositoryTests {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Test
    void insertarYListarMascota() {
        Mascota mascota = crearMascota();
        mascotaRepository.save(mascota);

        List<Mascota> mascotas = mascotaRepository.findAll();

        assertTrue(mascotas.stream()
                .anyMatch(m -> "Prueba".equals(m.getNombre())));
    }

    @Test
    void actualizarMascota() {
        Mascota mascota = mascotaRepository.save(crearMascota());

        mascota.setEdad(4);
        mascotaRepository.save(mascota);

        Mascota actualizada = mascotaRepository.findById(mascota.getId())
                .orElseThrow();

        assertEquals(4, actualizada.getEdad());
    }

    @Test
    void eliminarMascota() {
        Mascota mascota = mascotaRepository.save(crearMascota());
        Long id = mascota.getId();

        mascotaRepository.deleteById(id);

        assertFalse(mascotaRepository.findById(id).isPresent());
    }

    private Mascota crearMascota() {
        Mascota mascota = new Mascota();
        mascota.setNombre("Prueba");
        mascota.setEspecie("Perro");
        mascota.setRaza("Mestizo");
        mascota.setEdad(2);
        mascota.setSexo("Hembra");
        mascota.setEstado("Disponible");
        mascota.setDescripcion("Mascota para prueba automatizada");
        return mascota;
    }
}
