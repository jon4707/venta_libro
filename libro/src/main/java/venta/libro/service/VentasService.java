package venta.libro.service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.transaction.Transactional;
import venta.libro.dto.AgregarProductoRequest;
import venta.libro.dto.CantidadDTO;
import venta.libro.dto.CompraOnlineRequest;
import venta.libro.dto.CrearVentaRequest;
import venta.libro.dto.FinalizarCompraRequest;
import venta.libro.dto.VentasDTO;
import venta.libro.model.Cantidad;
import venta.libro.model.Descuento;
import venta.libro.model.MedioPago;
import venta.libro.model.TipoVenta;
import venta.libro.model.Ventas;
import venta.libro.exception.VentaNotFoundException;
import venta.libro.repository.CantidadRepository;
import venta.libro.repository.DescuentoRepository;
import venta.libro.repository.VentasRepository;

@Service
@Transactional
public class VentasService {

	@Autowired
	private VentasRepository ventasRepository;

	@Autowired
	private CantidadRepository cantidadRepository;

	@Autowired
	private DescuentoRepository descuentoRepository;

	@Autowired
	private RestTemplate restTemplate;

	private static final String INVENTARIO_URL = "http://localhost:9080/api";

	private VentasDTO toDTO(Ventas v) {
		VentasDTO dto = new VentasDTO();
		dto.setId(v.getId());
		dto.setIdSucursal(v.getIdSucursal());
		dto.setProductos(v.getProductos().stream().map(this::toCantidadDTO).collect(Collectors.toList()));
		dto.setFechaCompra(v.getFechaCompra());
		dto.setSubtotal(v.getSubtotal());
		dto.setDescuentoTotal(v.getDescuentoTotal());
		dto.setImpuestos(v.getImpuestos());
		dto.setTotal(v.getTotal());
		dto.setMedioPago(v.getMedioPago());
		dto.setMontoPagado(v.getMontoPagado());
		dto.setTipoVenta(v.getTipoVenta());
		dto.setVuelto(v.getVuelto());
		dto.setIdCliente(v.getIdCliente());
		return dto;
	}

	private CantidadDTO toCantidadDTO(Cantidad c) {
		return new CantidadDTO(c.getId(), c.getIdVenta(), c.getIdLibro(), c.getCantidad());
	}

	public VentasDTO crearVentaCajero(CrearVentaRequest request) {
		Ventas v = new Ventas();
		v.setIdSucursal(request.getIdSucursal());
		v.setFechaCompra(new Date(Instant.now().toEpochMilli()));
		v.setSubtotal(0.0);
		v.setDescuentoTotal(0.0);
		v.setImpuestos(0.0);
		v.setTotal(0.0);
		v.setTipoVenta(TipoVenta.VENTA_CAJERO);
		v.setIdCliente(request.getIdCliente());
		return toDTO(ventasRepository.save(v));
	}

	public CantidadDTO añadirProducto(Long idVenta, AgregarProductoRequest request) {
		Ventas venta = ventasRepository.findById(idVenta)
				.orElseThrow(() -> new VentaNotFoundException(idVenta));

		Double precioVenta = obtenerPrecioLibro(request.getIdLibro());
		validarStockSuficiente(request.getIdLibro(), venta.getIdSucursal(), request.getCantidad());

		Cantidad c = new Cantidad();
		c.setIdLibro(request.getIdLibro());
		c.setCantidad(request.getCantidad());
		c.setVenta(venta);
		venta.getProductos().add(c);
		cantidadRepository.save(c);

		venta.setSubtotal(venta.getSubtotal() + (precioVenta * request.getCantidad()));

		if (request.getDescuentoId() != null) {
			Descuento desc = descuentoRepository.findByIdAndEstadoTrue(request.getDescuentoId())
					.orElseThrow(() -> new RuntimeException("Descuento no encontrado o inactivo"));
			double descuentoAplicado = precioVenta * desc.getPorcentaje() / 100.0;
			venta.setDescuentoTotal(venta.getDescuentoTotal() + descuentoAplicado);
			desc.setEstado(false);
			descuentoRepository.save(desc);
		}

		ventasRepository.save(venta);
		return toCantidadDTO(c);
	}

	public void eliminarProducto(Long idVenta, Long idLibro) {
		Ventas venta = ventasRepository.findById(idVenta)
				.orElseThrow(() -> new VentaNotFoundException(idVenta));

		Cantidad cant = cantidadRepository.findByIdVentaAndIdLibro(idVenta, idLibro)
				.orElseThrow(() -> new RuntimeException("Producto no encontrado en la venta"));

		Double precioVenta = obtenerPrecioLibro(idLibro);
		venta.setSubtotal(venta.getSubtotal() - (precioVenta * cant.getCantidad()));
		venta.getProductos().remove(cant);
		cantidadRepository.delete(cant);
		ventasRepository.save(venta);
	}

	public VentasDTO finalizarCompra(Long idVenta, FinalizarCompraRequest request) {
		Ventas venta = ventasRepository.findById(idVenta)
				.orElseThrow(() -> new VentaNotFoundException(idVenta));

		if (venta.getProductos().isEmpty()) {
			throw new RuntimeException("La venta no tiene productos");
		}

		double descuento = venta.getDescuentoTotal() != null ? venta.getDescuentoTotal() : 0.0;
		double subtotal = venta.getSubtotal() != null ? venta.getSubtotal() : 0.0;
		double impuestos = (subtotal - descuento) * 0.19;
		double total = subtotal - descuento + impuestos;

		venta.setImpuestos(impuestos);
		venta.setTotal(total);
		venta.setMedioPago(request.getMedioPago());

		if (request.getMedioPago() == MedioPago.EFECTIVO) {
			if (request.getMontoPagado() == null || request.getMontoPagado() < total) {
				throw new RuntimeException("Monto pagado insuficiente. Total: " + total);
			}
			venta.setMontoPagado(request.getMontoPagado());
			venta.setVuelto(request.getMontoPagado() - total);
		} else {
			venta.setMontoPagado(total);
			venta.setVuelto(0.0);
		}

		reducirStockVenta(venta);
		registrarUnidadesVendidas(venta);

		ventasRepository.save(venta);
		return toDTO(venta);
	}

	public VentasDTO realizarCompraOnline(CompraOnlineRequest request) {
		Ventas venta = new Ventas();
		venta.setIdSucursal(request.getIdSucursal());
		venta.setFechaCompra(new Date(Instant.now().toEpochMilli()));
		venta.setSubtotal(0.0);
		venta.setDescuentoTotal(0.0);
		venta.setImpuestos(0.0);
		venta.setTotal(0.0);
		venta.setTipoVenta(TipoVenta.VENTA_WEB);
		venta.setIdCliente(request.getIdCliente());
		venta.setMedioPago(request.getMedioPago());

		double subtotal = 0.0;
		for (CompraOnlineRequest.ItemCarritoDTO item : request.getItems()) {
			Double precio = obtenerPrecioLibro(item.getIdLibro());
			validarStockSuficiente(item.getIdLibro(), request.getIdSucursal(), item.getCantidad());

			Cantidad c = new Cantidad();
			c.setIdLibro(item.getIdLibro());
			c.setCantidad(item.getCantidad());
			c.setVenta(venta);
			venta.getProductos().add(c);

			subtotal += precio * item.getCantidad();
		}

		venta.setSubtotal(subtotal);

		if (request.getDescuentoId() != null) {
			Descuento desc = descuentoRepository.findByIdAndEstadoTrue(request.getDescuentoId())
					.orElseThrow(() -> new RuntimeException("Descuento no encontrado o inactivo"));
			Long primerLibro = request.getItems().get(0).getIdLibro();
			Double precioPrimero = obtenerPrecioLibro(primerLibro);
			double descuentoAplicado = precioPrimero * desc.getPorcentaje() / 100.0;
			venta.setDescuentoTotal(descuentoAplicado);
			desc.setEstado(false);
			descuentoRepository.save(desc);
		}

		double descuento = venta.getDescuentoTotal() != null ? venta.getDescuentoTotal() : 0.0;
		double impuestos = (subtotal - descuento) * 0.19;
		double total = subtotal - descuento + impuestos;
		venta.setImpuestos(impuestos);
		venta.setTotal(total);
		venta.setMontoPagado(total);
		venta.setVuelto(0.0);

		try {
			reducirStockVenta(venta);
		} catch (RuntimeException e) {
			throw new RuntimeException("Pago rechazado: " + e.getMessage());
		}
		registrarUnidadesVendidas(venta);

		ventasRepository.save(venta);
		return toDTO(venta);
	}

	public List<VentasDTO> listarVentas() {
		return ventasRepository.findAll().stream()
				.map(this::toDTO).collect(Collectors.toList());
	}

	public VentasDTO obtenerVenta(Long id) {
		return ventasRepository.findById(id)
				.map(this::toDTO)
				.orElseThrow(() -> new VentaNotFoundException(id));
	}

	private Double obtenerPrecioLibro(Long idLibro) {
		try {
			return restTemplate.getForObject(INVENTARIO_URL + "/libros/{id}/precio", Double.class, idLibro);
		} catch (Exception e) {
			throw new RuntimeException("Error al obtener precio del libro " + idLibro, e);
		}
	}

	private void validarStockSuficiente(Long idLibro, Long idSucursal, int cantidad) {
		try {
			String url = INVENTARIO_URL + "/stock-libros/validar";
			var request = new java.util.HashMap<String, Object>();
			request.put("idSucursal", idSucursal);
			var items = java.util.List.of(
					new java.util.HashMap<String, Object>() {
						{
							put("idLibro", idLibro);
							put("cantidad", cantidad);
						}
					});
			request.put("items", items);
			Boolean suficiente = restTemplate.postForObject(url, request, Boolean.class);
			if (Boolean.FALSE.equals(suficiente)) {
				throw new RuntimeException("Stock insuficiente para el libro " + idLibro);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error al validar stock", e);
		}
	}

	private void reducirStockVenta(Ventas venta) {
		for (Cantidad c : venta.getProductos()) {
			try {
				String url = INVENTARIO_URL + "/stock-libros/reducir";
				var request = new java.util.HashMap<String, Object>();
				request.put("idLibro", c.getIdLibro());
				request.put("idSucursal", venta.getIdSucursal());
				request.put("stock", c.getCantidad());
				restTemplate.postForObject(url, request, String.class);
			} catch (Exception e) {
				throw new RuntimeException("Error al reducir stock del libro " + c.getIdLibro(), e);
			}
		}
	}

	private void registrarUnidadesVendidas(Ventas venta) {
		for (Cantidad c : venta.getProductos()) {
			try {
				String url = INVENTARIO_URL + "/libros/{id}/unidades-vendidas?cantidad={cantidad}";
				restTemplate.postForObject(url, null, String.class, c.getIdLibro(), c.getCantidad());
			} catch (Exception e) {
				System.err.println("Error al registrar unidades vendidas del libro " + c.getIdLibro()
						+ ": " + e.getMessage());
			}
		}
	}
}
