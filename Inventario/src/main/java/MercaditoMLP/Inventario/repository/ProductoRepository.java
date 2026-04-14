package MercaditoMLP.Inventario.repository;


import MercaditoMLP.Inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Optional<Producto> findFirstByNombreAndCategoriaAndFechaElaboracion(
            String nombre, String categoria, LocalDate fechaElaboracion
    );

    List<Producto> findByCategoriaIgnoreCase(String categoria);

}
