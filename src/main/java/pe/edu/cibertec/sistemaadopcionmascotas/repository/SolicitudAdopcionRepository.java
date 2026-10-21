package pe.edu.cibertec.sistemaadopcionmascotas.repository;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.SolicitudAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudAdopcionRepository
        extends JpaRepository<SolicitudAdopcion, Long> {

    List<SolicitudAdopcion> findByUsuarioIdOrderByFechaSolicitudDesc(Long usuarioId);

    List<SolicitudAdopcion> findAllByOrderByFechaSolicitudDesc();

    boolean existsByMascotaIdAndEstado(Long mascotaId, String estado);
}
