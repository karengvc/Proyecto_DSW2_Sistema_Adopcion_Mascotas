
package pe.edu.cibertec.sistemaadopcionmascotas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.MascotaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    @Transactional(readOnly = true)
    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mascota obtener(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Mascota no encontrada"));
    }

    @Transactional
    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota actualizar(Mascota datos) {
        return actualizar(datos, null, null);
    }

    @Transactional
    public Mascota actualizar(
            Mascota datos,
            byte[] nuevaFoto,
            String tipoFoto) {

        if (datos.getId() == null) {
            throw new IllegalArgumentException(
                    "El identificador de la mascota es obligatorio");
        }

        Mascota existente = mascotaRepository.findById(datos.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Mascota no encontrada"));

        existente.setNombre(datos.getNombre());
        existente.setEspecie(datos.getEspecie());
        existente.setRaza(datos.getRaza());
        existente.setEdad(datos.getEdad());
        existente.setSexo(datos.getSexo());
        existente.setEstado(datos.getEstado());
        existente.setDescripcion(datos.getDescripcion());


        if (nuevaFoto != null && nuevaFoto.length > 0) {
            existente.setFoto(nuevaFoto);
            existente.setTipoFoto(tipoFoto);
        }

        return mascotaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new IllegalArgumentException("Mascota no encontrada");
        }

        mascotaRepository.deleteById(id);
    }
}
