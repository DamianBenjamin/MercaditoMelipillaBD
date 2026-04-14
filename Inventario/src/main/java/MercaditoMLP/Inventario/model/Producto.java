package MercaditoMLP.Inventario.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoria;
    private String nombre;
    private String tamano; //grande o mediano
    private String esEntero; //producto entero o trozado
    private Integer stockTrozos; //cantidad de trozos

    private LocalDate fechaElaboracion;
    @JsonProperty("fechaLlegada")
    private LocalDate fechaLLegada;

}
