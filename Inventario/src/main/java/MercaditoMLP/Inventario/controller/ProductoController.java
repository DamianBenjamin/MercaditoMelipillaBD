package MercaditoMLP.Inventario.controller;


import MercaditoMLP.Inventario.dto.ReporteJerarquicoDTO;
import MercaditoMLP.Inventario.dto.ResumenStockDTO;
import MercaditoMLP.Inventario.model.Producto;
import MercaditoMLP.Inventario.repository.ProductoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/reporte/detallado-por-nombre")
    @Operation(summary = "Obtiene todos los productos agrupados por nombre pero con sus detalles individuales")
    public Map<String, List<Producto>> obtenerDetalleParaGestion() {
        List<Producto> todos = productoRepository.findAll();
        // Agrupamos por nombre para que el Frontend sepa qué "carpetas" crear
        return todos.stream().collect(Collectors.groupingBy(Producto::getNombre));
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
            nuevo.setFechaLlegada(producto.getFechaLlegada());

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
    public ResponseEntity<?> trozarProducto(
            @PathVariable Long id,
            @RequestParam int trozos) {

        return productoRepository.findById(id)
                .map(producto -> {
                    // Evitamos errores de NullPointerException usando limpiadores seguros
                    String categoria = (producto.getCategoria() != null) ? producto.getCategoria().toLowerCase().trim() : "";
                    String tamano = (producto.getTamano() != null) ? producto.getTamano().toLowerCase().trim() : "";

                    // 1. Validación de Categoría flexible (acepta pastelería con/sin tilde, tortas, etc.)
                    if (!categoria.contains("pasteleria") && !categoria.contains("pastelería") && !categoria.contains("torta")) {
                        return ResponseEntity.badRequest().body("Solo se pueden trozar productos de pastelería o tortas.");
                    }

                    // 2. Validación de Tamaño flexible (si el tamaño contiene "grande" o si el nombre del producto dice "grande")
                    String nombreCompleto = (producto.getNombre() != null) ? producto.getNombre().toLowerCase() : "";
                    if (!tamano.contains("grande") && !nombreCompleto.contains("grande")) {
                        return ResponseEntity.badRequest().body("Solo se pueden trozar pasteles grandes.");
                    }

                    // 3. Validación de estado
                    if ("no".equalsIgnoreCase(producto.getEsEntero())) {
                        return ResponseEntity.badRequest().body("Este producto ya ha sido trozado.");
                    }
                    if (trozos <= 0) {
                        return ResponseEntity.badRequest().body("La cantidad de trozos debe ser mayor a 0.");
                    }

                    // Aplicamos los cambios y guardamos en Neon
                    producto.setEsEntero("No");
                    producto.setStockTrozos(trozos);

                    productoRepository.save(producto);
                    return ResponseEntity.ok().body("Producto trozado exitosamente en " + trozos + " porciones.");
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


    @PutMapping("/{id}")
    @Operation(summary = "Editar un producto por su ID (Corrección de datos)")
    public ResponseEntity<Producto> editarProducto(@PathVariable Long id, @RequestBody Producto productoEditado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    // Actualizamos los campos básicos
                    producto.setNombre(productoEditado.getNombre());
                    producto.setCategoria(productoEditado.getCategoria());
                    producto.setFechaElaboracion(productoEditado.getFechaElaboracion());
                    producto.setFechaLlegada(productoEditado.getFechaLlegada());
                    producto.setTamano(productoEditado.getTamano());

                    // Si cambian la categoría a Sandwich, aplicamos la regla de negocio automáticamente
                    if ("Sandwich".equalsIgnoreCase(productoEditado.getCategoria())) {
                        producto.setTamano("N/A");
                        producto.setEsEntero("si");
                        producto.setStockTrozos(1);
                    } else {
                        producto.setEsEntero(productoEditado.getEsEntero());
                        producto.setStockTrozos(productoEditado.getStockTrozos());
                    }

                    return ResponseEntity.ok(productoRepository.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto directamente por su ID")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
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

    @GetMapping("/reporte/por-fecha")
    @Operation(summary = "Reporte detallado por fecha específica: Total -> Categoría -> Producto")
    public ResponseEntity<?> obtenerReportePorFecha(@RequestParam String fecha) {

        LocalDate fechaBusqueda = LocalDate.parse(fecha);

        List<Producto> productosDelDia = productoRepository.findByFechaLlegada(fechaBusqueda);

        if (productosDelDia.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron productos registrados en la fecha: " + fecha);
        }

        long totalGeneral = productosDelDia.size();

        Map<String, ReporteJerarquicoDTO.CategoriaDetalle> detalleFinal = productosDelDia.stream()
                .collect(Collectors.groupingBy(
                        Producto::getCategoria,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                listaPorCat -> {
                                    long totalCat = listaPorCat.size();
                                    Map<String, Long> productosDetalle = listaPorCat.stream()
                                            .collect(Collectors.groupingBy(
                                                    p -> p.getNombre() + " (" + (p.getTamano() != null ? p.getTamano() : "N/A") + ")",
                                                    Collectors.counting()
                                            ));
                                    return new ReporteJerarquicoDTO.CategoriaDetalle(totalCat, productosDetalle);
                                }
                        )
                ));

        return ResponseEntity.ok(new ReporteJerarquicoDTO(totalGeneral, detalleFinal));
    }

    @GetMapping("/reporte/prioridad-venta")
    @Operation(summary = "Lista de productos mas antiguos (venta inmediata)")
    public List<Producto> listarPorAntiguedad() {
        return productoRepository.findAllByOrderByFechaElaboracionAsc();
    }

    @GetMapping("/reporte/stock-detallado")
    @Operation(summary = "Ver total y detalle agrupado por nombre de una categoría")
    public ResponseEntity<ResumenStockDTO> obtenerStockDetallado(@RequestParam String categoria) {

        List<Producto> productos = productoRepository.findByCategoriaIgnoreCase(categoria);

        long totalGlobal = productos.size();

        Map<String, Long> detalle = productos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getNombre() + " (" + (p.getTamano() != null ? p.getTamano() : "N/A") + ")",
                        Collectors.counting()
                ));

        return ResponseEntity.ok(new ResumenStockDTO(categoria, totalGlobal, detalle));
    }

    @GetMapping("/reporte/jerarquico-completo")
    @Operation(summary = "Reporte total: General -> Por Categoría -> Por Producto")
    public ResponseEntity<ReporteJerarquicoDTO> obtenerReporteJerarquico() {
        List<Producto> todos = productoRepository.findAll();
        long totalGeneral = todos.size();

        Map<String, ReporteJerarquicoDTO.CategoriaDetalle> detalleFinal = todos.stream()
                .collect(Collectors.groupingBy(
                        Producto::getCategoria,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                listaPorCat -> {
                                    long totalCat = listaPorCat.size();
                                    Map<String, Long> productosDetalle = listaPorCat.stream()
                                            .collect(Collectors.groupingBy(
                                                    p -> p.getNombre() + " (" + (p.getTamano() != null ? p.getTamano() : "N/A") + ")",
                                                    Collectors.counting()
                                            ));
                                    return new ReporteJerarquicoDTO.CategoriaDetalle(totalCat, productosDetalle);
                                }
                        )
                ));

        return ResponseEntity.ok(new ReporteJerarquicoDTO(totalGeneral, detalleFinal));
    }

    @GetMapping("/reporte/alerta-reposicion")
    @Operation(summary = "Lista de productos con stock crítico (Enteros y Trozados con)")
    public List<Producto> obtenerAlertasStock() {
        return productoRepository.findAll().stream()
                .filter(p -> {

                    int stock = (p.getStockTrozos() != null) ? p.getStockTrozos() : 0;

                    if ("no".equalsIgnoreCase(p.getEsEntero())) {
                        return stock <= 5;
                    }

                    if ("si".equalsIgnoreCase(p.getEsEntero())) {
                        return stock <= 1;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

}
