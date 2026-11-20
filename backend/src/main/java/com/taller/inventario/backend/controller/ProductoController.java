package com.taller.inventario.backend.controller;

import com.taller.inventario.backend.model.Producto;
import com.taller.inventario.backend.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Listar todos los repuestos del inventario.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener un repuesto por su código / ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable("id") String id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo repuesto en el inventario.
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * Actualizar un repuesto existente por su código / ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(
            @PathVariable("id") String id,
            @RequestBody Producto productoActualizado
    ) {
        return productoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(productoActualizado.getNombre());
                    existente.setMarca(productoActualizado.getMarca());
                    existente.setCategoria(productoActualizado.getCategoria());
                    existente.setUnidadMedida(productoActualizado.getUnidadMedida());
                    existente.setCantidad(productoActualizado.getCantidad());
                    existente.setStockMinimo(productoActualizado.getStockMinimo());
                    existente.setPrecioCompra(productoActualizado.getPrecioCompra());
                    existente.setPrecioVenta(productoActualizado.getPrecioVenta());
                    existente.setUbicacionZona(productoActualizado.getUbicacionZona());
                    existente.setUbicacionEstante(productoActualizado.getUbicacionEstante());
                    existente.setUbicacionRepisa(productoActualizado.getUbicacionRepisa());
                    existente.setProveedorNombre(productoActualizado.getProveedorNombre());
                    existente.setEstado(productoActualizado.getEstado());
                    Producto guardado = productoRepository.save(existente);
                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> {
                    productoActualizado.setCodigo(id);
                    Producto guardado = productoRepository.save(productoActualizado);
                    return ResponseEntity.ok(guardado);
                });
    }

    /**
     * Eliminar un repuesto por su código / ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable("id") String id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
