package venta.libro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venta.libro.model.MedioPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarCompraRequest {
    @NotNull
    private MedioPago medioPago;

    private Double montoPagado;
}
