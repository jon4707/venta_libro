# Ventas — Procesamiento de Compras y Descuentos

## Que es

El servicio financiero de la libreria. Registra cada venta (presencial o online), valida stock, aplica descuentos, calcula impuestos, procesa pagos, y actualiza el inventario. Es el unico servicio que modifica los precios finales y las unidades vendidas de los libros.

## Dos formas de vender

### Venta presencial (en caja)

Un proceso de 3 pasos que modela lo que hace un cajero en la tienda:

1. **Crear venta** — Se abre una venta vacia para un cliente en una sucursal.
2. **Agregar productos** — Uno por uno, como en una caja real. Por cada libro se consulta el precio actual al **Inventario**, se valida que haya stock, y se va acumulando el subtotal. Si el cliente tiene un codigo de descuento, se aplica ahi.
3. **Finalizar** — Se calculan los impuestos, se procesa el pago, y se descuenta el stock.

### Venta online

Todo ocurre en un solo paso atomico: se envia el carrito completo, se valida todo, se aplica descuento si hay, se paga, y se descuenta stock. Si algo falla, toda la operacion se rechaza.

## Impuestos (IVA chileno)

```
impuestos = (subtotal - descuento) * 19%
total = subtotal - descuento + impuestos
```

El IVA se calcula sobre la base imponible (subtotal menos descuentos), no sobre el subtotal completo.

## Sistema de descuentos

Los descuentos son codigos de un solo uso. Un admin los crea con un nombre, porcentaje (entre 5% y 89%), fecha de vencimiento, y cuantos generar. Cada codigo es el ID de la base de datos.

**Como funciona:**
- En venta presencial: el descuento se aplica producto por producto al momento de agregarlo.
- En venta online: el descuento se aplica una vez, sobre el primer articulo del carrito.
- **Al aplicarse, el descuento se desactiva automaticamente** — no se puede usar dos veces.

## Metodos de pago

| Metodo | Comportamiento |
|--------|---------------|
| EFECTIVO | Se requiere `montoPagado`. Si paga de mas, se calcula el vuelto. |
| CREDITO | Se cobra el monto exacto del total. |
| DEBITO | Igual que credito. |
| TRANSFERENCIA | Igual que credito. |
| JUNAEB | Igual que credito. (Tarjeta de beneficios estudiantiles chilena) |

## Ejecutar

```cmd
cd venta_libro\libro
.\mvnw.cmd spring-boot:run
```

Puerto: **8087** | DB: `ventas`

## Endpoints

**Ventas** (`/api/v1/ventas`):
- `POST /crear` — Crear venta presencial
- `POST /{id}/productos` — Agregar producto (body: `{idLibro, cantidad, descuentoId?}`)
- `DELETE /{idVenta}/productos/{idLibro}` — Quitar producto
- `POST /{id}/finalizar` — Finalizar y pagar (body: `{medioPago, montoPagado}`)
- `POST /online` — Venta online atomica
- `GET /` — Listar todas
- `GET /{id}` — Ver por ID

**Descuentos** (`/api/v1/descuentos`):
- `POST /` — Generar codigos (body: `{nombre, fechaVencimiento, porcentaje, cantidad}`)
- `GET /` — Listar todos
- `GET /{id}` — Ver por ID
