
package pe.edu.cibertec.sistemaadopcionmascotas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String correo;

    @Column(nullable = false)
    private boolean correoVerificado = false;

    private String codigoVerificacion;

    private LocalDateTime expiracionCodigo;

    @Column(nullable = false)
    private String rol = "USUARIO";
}
