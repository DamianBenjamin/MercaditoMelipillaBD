package MercaditoMLP.Inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ResumenStockDTO {
    private String categoria;
    private long totalGlobal;
    private Map<String, Long> detallePorNombre;
}