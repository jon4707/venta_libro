package venta.libro.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import venta.libro.model.MedioPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraOnlineRequest {
    @NotNull
    private Long idOrden;

    @NotNull
    private Long idCliente;

    @NotNull
    private Long idSucursal;

    private Long descuentoId;

    @NotNull
    private MedioPago medioPago;

    @NotNull
    private List<ItemCarritoDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemCarritoDTO {
        private Long idLibro;
        private Integer cantidad;
    }
}
