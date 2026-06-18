package MercaditoMLP.Inventario.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaStockDTO {

    private String productoNombre;
    private String categoria;
    private int cantidadActual;
    private String estado;

}
