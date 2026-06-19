package venta.libro.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MedioPago {
    CREDITO,
    DEBITO,
    EFECTIVO,
    TRANSFERENCIA,
    JUNAEB;

    @JsonCreator
    public static MedioPago fromString(String value) {
        return MedioPago.valueOf(value.toUpperCase());
    }
    
}
