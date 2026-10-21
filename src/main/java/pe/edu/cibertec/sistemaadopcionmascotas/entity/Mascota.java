
package pe.edu.cibertec.sistemaadopcionmascotas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mascota")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String especie;
    private String raza;
    private Integer edad;
    private String sexo;
    private String descripcion;
    private String estado;

    @JsonIgnore
    @Column(columnDefinition = "bytea")
    private byte[] foto;

    private String tipoFoto;
}
