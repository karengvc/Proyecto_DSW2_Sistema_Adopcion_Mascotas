package pe.edu.cibertec.sistemaadopcionmascotas.repository;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}