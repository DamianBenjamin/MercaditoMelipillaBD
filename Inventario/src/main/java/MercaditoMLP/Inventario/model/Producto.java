package MercaditoMLP.Inventario.model;


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
    private String tamano;
    private String esEntero;
    private Integer stockTrozos;

    private LocalDate fechaElaboracion;

    private LocalDate fechaLlegada;

}
