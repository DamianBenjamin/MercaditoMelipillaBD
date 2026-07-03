package MercaditoMLP.Inventario.controller;

import MercaditoMLP.Inventario.model.CatalogoProducto;
import MercaditoMLP.Inventario.repository.CatalogoProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @Autowired
    private CatalogoProductoRepository catalogoRepository;

    @GetMapping("/buscar")
    public ResponseEntity<List<CatalogoProducto>> buscarEnCatalogo(@RequestParam String termino) {
        return ResponseEntity.ok(catalogoRepository.buscarSinTildes(termino));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarNuevoNombre(@RequestBody CatalogoProducto nuevoItem) {
        if (catalogoRepository.findByNombreIgnoreCase(nuevoItem.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().body("El producto ya existe en el catálogo.");
        }
        return ResponseEntity.ok(catalogoRepository.save(nuevoItem));
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar un producto de la Lista Maestra por su ID")
    public ResponseEntity<?> eliminarDelCatalogo(@PathVariable Long id) {
        return catalogoRepository.findById(id)
                .map(producto -> {
                    catalogoRepository.delete(producto);
                    return ResponseEntity.ok().body("¡Producto '" + producto.getNombre() + "' eliminado con éxito de la Lista Maestra!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("El producto con ID " + id + " no existe en el catálogo."));
    }
}