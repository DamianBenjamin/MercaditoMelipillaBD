package MercaditoMLP.Inventario.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Ej: "local1_fabrica" o "local2_ventas"

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombreLocal; // Para mostrar en pantalla ("Fábrica de Pasteles" o "Sucursal Ventas")

    @Column(nullable = false)
    private String rol;
}
