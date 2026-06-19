package venta.libro.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoVenta {
   VENTA_CAJERO,
   VENTA_WEB;

   @JsonCreator
    public static TipoVenta fromString(String value) {
        return TipoVenta.valueOf(value.toUpperCase());
    }
}
