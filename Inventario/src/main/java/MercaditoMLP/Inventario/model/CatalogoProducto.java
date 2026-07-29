package MercaditoMLP.Inventario.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogo_productos")
@Data
public class CatalogoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoriaDefecto;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario = 0.0;
}