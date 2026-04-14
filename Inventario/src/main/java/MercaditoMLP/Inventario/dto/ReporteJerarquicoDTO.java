package MercaditoMLP.Inventario.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteJerarquicoDTO {
    private long totalGeneral;
    private Map<String, CategoriaDetalle> detallePorCategoria;

    @Data
    @AllArgsConstructor
    public static class CategoriaDetalle {
        private long totalCategoria;
        private Map<String, Long> productos;
    }
}