
package pe.edu.cibertec.sistemaadopcionmascotas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "solicitud_adopcion")
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, length = 20)
    private String sexo;

    @Column(nullable = false, length = 9)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String correo;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String experiencia;

    @Column(nullable = false, length = 1000)
    private String motivo;

    @Column(nullable = false)
    private boolean compromisoResponsable;

    @Column(nullable = false, length = 30)
    private String estado = "Pendiente";

    @Column(length = 500)
    private String motivoRechazo;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
}
