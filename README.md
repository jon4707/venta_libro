# Ventas (venta_libro)

Gestion de ventas presenciales y online, incluyendo creacion de venta, agregado de productos con descuentos, y finalizacion con calculo de impuestos.

## Puerto

**8087** | DB: `ventas`

**Nota:** El codigo fuente esta en `venta_libro/libro/`.

## Endpoints

### Ventas (`/api/v1/ventas`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/v1/ventas/crear` | Crear venta (cajero) |
| POST | `/api/v1/ventas/{id}/productos` | Agregar producto a la venta |
| DELETE | `/api/v1/ventas/{idVenta}/productos/{idLibro}` | Quitar producto |
| POST | `/api/v1/ventas/{id}/finalizar` | Finalizar venta (pago) |
| POST | `/api/v1/ventas/online` | Crear venta online |
| GET | `/api/v1/ventas` | Listar todas |
| GET | `/api/v1/ventas/{id}` | Obtener por ID |

### Descuentos (`/api/v1/descuentos`)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/v1/descuentos` | Generar descuentos |
| GET | `/api/v1/descuentos` | Listar todos |
| GET | `/api/v1/descuentos/{id}` | Obtener por ID |

## Flujo de venta presencial

### 1. Crear venta

```json
POST /api/v1/ventas/crear
{
  "idSucursal": 1,
  "idCliente": 1
}
```

### 2. Agregar productos

```json
POST /api/v1/ventas/{idVenta}/productos
{
  "idLibro": 1,
  "cantidad": 2,
  "descuentoId": null
}
```

### 3. Finalizar venta

```json
POST /api/v1/ventas/{idVenta}/finalizar
{
  "medioPago": "EFECTIVO",
  "montoPagado": 60000
}
```

### Medios de pago

`EFECTIVO`, `CREDITO`, `DEBITO`, `TRANSFERENCIA`, `JUNAEB`

### Calculos en finalizacion

- Se aplica 19% IVA sobre (subtotal - descuento)
- total = subtotal - descuento + impuestos
- Si es EFECTIVO: `montoPagado` debe ser >= total. Se calcula `vuelto`.
- Para otros medios: `montoPagado = total`, `vuelto = 0`.

## Generar descuentos

```json
POST /api/v1/descuentos
{
  "nombre": "10% OFF Semanal",
  "fechaVencimiento": "2026-12-31",
  "porcentaje": 10,
  "cantidad": 3
}
```

- `porcentaje`: entre 5 y 89
- `cantidad`: numero de codigos a generar
- Los descuentos son de un solo uso (se desactivan al aplicarlos)

## Ejecucion

```cmd
cd venta_libro\libro
.\mvnw.cmd spring-boot:run
```
