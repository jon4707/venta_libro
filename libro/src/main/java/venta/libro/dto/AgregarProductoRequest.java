package venta.libro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarProductoRequest {
    @NotNull
    private Long idLibro;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private Long descuentoId;
}
