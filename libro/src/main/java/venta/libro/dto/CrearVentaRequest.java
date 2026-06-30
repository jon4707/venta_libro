package venta.libro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearVentaRequest {
    @NotNull
    private Long idCliente;

    @NotNull
    private Long idSucursal;
}
