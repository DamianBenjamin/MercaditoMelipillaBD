package MercaditoMLP.Inventario.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteJerarquicoDTO {
    private long totalGeneral;
    private Map<String, CategoriaDetalle> detallePorCategoria;
    private List<AlertaStockDTO> alertasStock = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoriaDetalle {
        private long totalCategoria;
        private Map<String, Long> productos;
    }
}