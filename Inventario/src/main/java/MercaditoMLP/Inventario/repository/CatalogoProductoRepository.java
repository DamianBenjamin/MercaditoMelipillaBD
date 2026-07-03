package MercaditoMLP.Inventario.repository;

import MercaditoMLP.Inventario.model.CatalogoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoProductoRepository extends JpaRepository<CatalogoProducto, Long> {

    @Query(value = "SELECT * FROM catalogo_productos WHERE " +
            "translate(lower(nombre), 'áéíóúü', 'aeiouu') LIKE " +
            "translate(lower(concat('%', :termino, '%')), 'áéíóúü', 'aeiouu')",
            nativeQuery = true)
    List<CatalogoProducto> buscarSinTildes(@Param("termino") String termino);

    Optional<CatalogoProducto> findByNombreIgnoreCase(String nombre);
}