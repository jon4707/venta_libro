package venta.libro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CantidadDTO {
    private Long id;
    private Long idVenta;
    private Long idLibro;
    private Integer cantidad;
}
