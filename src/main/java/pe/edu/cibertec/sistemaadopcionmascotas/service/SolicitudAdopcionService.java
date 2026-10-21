
package pe.edu.cibertec.sistemaadopcionmascotas.service;

import pe.edu.cibertec.sistemaadopcionmascotas.entity.Mascota;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.SolicitudAdopcion;
import pe.edu.cibertec.sistemaadopcionmascotas.entity.Usuario;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.MascotaRepository;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.SolicitudAdopcionRepository;
import pe.edu.cibertec.sistemaadopcionmascotas.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudAdopcionService(
            SolicitudAdopcionRepository solicitudRepository,
            MascotaRepository mascotaRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SolicitudAdopcion crear(Long usuarioId, Map<String, Object> datos) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.isCorreoVerificado()) {
            throw new IllegalArgumentException(
                    "Debes verificar tu correo antes de solicitar una adopción");
        }

        Long mascotaId = obtenerLong(datos, "mascotaId");

        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Mascota no encontrada"));

        if (mascota.getEstado() == null
                || !mascota.getEstado().equalsIgnoreCase("Disponible")) {
            throw new IllegalArgumentException(
                    "Esta mascota ya no está disponible");
        }

        if (solicitudRepository.existsByMascotaIdAndEstado(
                mascotaId, "Aprobada")) {
            throw new IllegalArgumentException(
                    "Esta mascota ya tiene una adopción aprobada");
        }

        SolicitudAdopcion solicitud = new SolicitudAdopcion();

        solicitud.setUsuario(usuario);
        solicitud.setMascota(mascota);
        solicitud.setNombre(obtenerTexto(datos, "nombre"));
        solicitud.setDni(obtenerTexto(datos, "dni"));
        solicitud.setEdad(obtenerLong(datos, "edad").intValue());
        solicitud.setSexo(obtenerTexto(datos, "sexo"));
        solicitud.setTelefono(obtenerTexto(datos, "telefono"));
        solicitud.setCorreo(usuario.getCorreo());
        solicitud.setCiudad(obtenerTexto(datos, "ciudad"));
        solicitud.setDireccion(obtenerTexto(datos, "direccion"));
        solicitud.setExperiencia(obtenerTexto(datos, "experiencia"));
        solicitud.setMotivo(obtenerTexto(datos, "motivo"));

        Object compromiso = datos.get("compromisoResponsable");

        if (!(compromiso instanceof Boolean valor) || !valor) {
            throw new IllegalArgumentException(
                    "Debes aceptar el compromiso de adopción responsable");
        }

        solicitud.setCompromisoResponsable(true);

        validar(solicitud);

        return solicitudRepository.save(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> listarPorUsuario(Long usuarioId) {
        return solicitudRepository
                .findByUsuarioIdOrderByFechaSolicitudDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> listarTodas() {
        return solicitudRepository.findAllByOrderByFechaSolicitudDesc();
    }

    @Transactional
    public SolicitudAdopcion aprobar(Long solicitudId) {

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró la solicitud"));

        Mascota mascota = mascotaRepository.findById(
                        solicitud.getMascota().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró la mascota"));

        /*
         * Si la solicitud ya estaba aprobada, sincronizamos el estado
         * de la mascota. Esto también permite corregir aprobaciones
         * anteriores que dejaron la mascota como Disponible.
         */
        if ("Aprobada".equalsIgnoreCase(solicitud.getEstado())) {
            mascota.setEstado("Adoptado");
            mascotaRepository.save(mascota);
            return solicitud;
        }

        if (!"Pendiente".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo se pueden aprobar solicitudes pendientes");
        }

        if (mascota.getEstado() == null
                || !mascota.getEstado().equalsIgnoreCase("Disponible")) {
            throw new IllegalArgumentException(
                    "La mascota ya no está disponible para adopción");
        }

        if (solicitudRepository.existsByMascotaIdAndEstado(
                mascota.getId(), "Aprobada")) {
            throw new IllegalArgumentException(
                    "Esta mascota ya tiene una adopción aprobada");
        }

        solicitud.setEstado("Aprobada");
        solicitud.setMotivoRechazo(null);

        mascota.setEstado("Adoptado");
        mascotaRepository.save(mascota);

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public SolicitudAdopcion rechazar(Long solicitudId, String motivo) {

        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró la solicitud"));

        if (!"Pendiente".equalsIgnoreCase(solicitud.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo se pueden rechazar solicitudes pendientes");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar el motivo del rechazo");
        }

        if (motivo.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 500 caracteres");
        }

        solicitud.setEstado("Rechazada");
        solicitud.setMotivoRechazo(motivo.trim());

        return solicitudRepository.save(solicitud);
    }

    private void validar(SolicitudAdopcion solicitud) {

        if (solicitud.getNombre().length() > 100
                || !solicitud.getDni().matches("\\d{8}")
                || solicitud.getEdad() < 18
                || solicitud.getEdad() > 120
                || !solicitud.getTelefono().matches("\\d{9}")
                || solicitud.getCiudad().length() > 100
                || solicitud.getDireccion().length() > 200
                || solicitud.getMotivo().length() < 20
                || solicitud.getMotivo().length() > 1000) {

            throw new IllegalArgumentException(
                    "Revisa los datos: DNI de 8 dígitos, teléfono de 9, "
                            + "edad mínima de 18 años y motivo de al menos 20 caracteres");
        }

        if (!List.of("Sí", "No").contains(solicitud.getExperiencia())) {
            throw new IllegalArgumentException(
                    "Selecciona si tienes experiencia con mascotas");
        }

        if (!List.of(
                "Femenino",
                "Masculino",
                "Otro",
                "Prefiero no decirlo"
        ).contains(solicitud.getSexo())) {
            throw new IllegalArgumentException(
                    "Selecciona una opción válida para el sexo");
        }
    }

    private String obtenerTexto(
            Map<String, Object> datos,
            String campo) {

        Object valor = datos.get(campo);

        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio");
        }

        return valor.toString().trim();
    }

    private Long obtenerLong(
            Map<String, Object> datos,
            String campo) {

        try {
            return Long.valueOf(datos.get(campo).toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser un número válido");
        }
    }
}
