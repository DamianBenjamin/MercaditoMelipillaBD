package MercaditoMLP.Inventario.controller;


import MercaditoMLP.Inventario.model.Producto;
import MercaditoMLP.Inventario.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Productos", description = "Gestión de Productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    @Operation(summary = "Ver todos los Producto")
    public List<Producto> listarProductos(){
        return productoRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Agregar Producto")
    public ResponseEntity<String> crearProducto(
            @RequestBody Producto producto,
            @RequestParam(defaultValue = "1") int cantidad) {

        if (cantidad <= 0) {
            return ResponseEntity.badRequest().body("Error: La cantidad debe ser al menos 1");
        }

        for (int i = 0; i < cantidad; i++) {
            Producto nuevo = new Producto();

            nuevo.setNombre(producto.getNombre());
            nuevo.setCategoria(producto.getCategoria());
            nuevo.setFechaElaboracion(producto.getFechaElaboracion());
            nuevo.setFechaLLegada(producto.getFechaLLegada());

            if ("Sandwich".equalsIgnoreCase(producto.getCategoria())) {
                nuevo.setTamano("N/A");
                nuevo.setEsEntero("si");
                nuevo.setStockTrozos(1);
            } else {
                nuevo.setTamano(producto.getTamano());
                nuevo.setEsEntero(producto.getEsEntero());

                if ("no".equalsIgnoreCase(producto.getEsEntero())) {
                    if ("Grande".equalsIgnoreCase(producto.getTamano())) nuevo.setStockTrozos(8);
                    else if ("Mediano".equalsIgnoreCase(producto.getTamano())) nuevo.setStockTrozos(4);
                } else {
                    nuevo.setStockTrozos(1);
                }
            }
            productoRepository.save(nuevo);
        }

        return ResponseEntity.ok("Éxito: Se han ingresado " + cantidad + " unidades de " + producto.getNombre());
    }

    @PutMapping("/{id}/productoEnteroTrozar")
    @Operation(summary = "Editar Productos Entero por trozos")
    public ResponseEntity<Producto> trozarPastel(@PathVariable Long id, @RequestParam Integer TrozosVendidos) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setEsEntero("no");
                    int totalBase = 0;

                    if ("Grande".equalsIgnoreCase(producto.getTamano())) {
                        totalBase = 8;
                    } else if ("Mediano".equalsIgnoreCase(producto.getTamano())) {
                        totalBase = 4;
                    }

                    int saldoRestante = totalBase - TrozosVendidos;

                    producto.setStockTrozos(saldoRestante);

                    return ResponseEntity.ok(productoRepository.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/productoTrozado")
    @Operation(summary = "Editar Productos ya trozado")
    public ResponseEntity<?> venderTrozos(@PathVariable Long id, @RequestParam Integer cantidadAVender) {
        return productoRepository.findById(id)
                .map(producto -> {

                    if ("si".equalsIgnoreCase(producto.getEsEntero())) {
                        return ResponseEntity.badRequest().body("Error: El producto está entero. Debes Trozarlo con /{id}/trozar");
                    }

                    if (producto.getStockTrozos() < cantidadAVender) {
                        return ResponseEntity.badRequest().body("Error: Stock insuficiente. Solo quedan " + producto.getStockTrozos() + " trozos.");
                    }

                    int nuevoStock = producto.getStockTrozos() - cantidadAVender;
                    producto.setStockTrozos(nuevoStock);

                    return ResponseEntity.ok(productoRepository.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/eliminar-uno")
    @Operation(summary = "Eliminar Producto 1 por 1")
    public ResponseEntity<String> eliminarUnoPorDetalle(
            @RequestParam String nombre,
            @RequestParam String categoria,
            @RequestParam String fecha) {

        LocalDate fechaBusqueda = LocalDate.parse(fecha);

        return productoRepository.findFirstByNombreAndCategoriaAndFechaElaboracion(
                        nombre, categoria, fechaBusqueda)
                .map(producto -> {
                    productoRepository.delete(producto);
                    return ResponseEntity.ok("Se eliminó: " + nombre);
                })
                .orElse(ResponseEntity.status(404).body("No se encontró ningún producto con esos datos."));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar Producto Por su Nombre")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @GetMapping("/categoria")
    @Operation(summary = "Buscar Producto Por su Categoria")
    public List<Producto> buscarPorCategoria(@RequestParam("tipo") String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

}
