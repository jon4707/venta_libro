package venta.libro.dto;

import java.util.Date;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearDescuentosRequest {
    @NotNull
    private String nombre;

    @NotNull
    private Date fechaVencimiento;

    @Min(5)
    @Max(89)
    private Integer porcentaje;

    @Min(1)
    private Integer cantidad;
}
