package venta.libro.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoDTO {
    private Long id;
    private String nombre;
    private Date fechaCreacion;
    private Date fechaVencimiento;
    private Integer porcentaje;
    private Boolean estado;
}
