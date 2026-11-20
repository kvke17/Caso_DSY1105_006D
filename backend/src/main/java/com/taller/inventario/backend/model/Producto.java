package com.taller.inventario.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String marca;
    private String categoria;
    private String unidadMedida;
    private Integer cantidad;
    private Integer stockMinimo;
    private Double precioCompra;
    private Double precioVenta;
    private String ubicacionZona;
    private String ubicacionEstante;
    private String ubicacionRepisa;
    private String proveedorNombre;
    private String estado;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String marca, String categoria, String unidadMedida,
                    Integer cantidad, Integer stockMinimo, Double precioCompra, Double precioVenta,
                    String ubicacionZona, String ubicacionEstante, String ubicacionRepisa,
                    String proveedorNombre, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.ubicacionZona = ubicacionZona;
        this.ubicacionEstante = ubicacionEstante;
        this.ubicacionRepisa = ubicacionRepisa;
        this.proveedorNombre = proveedorNombre;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getUbicacionZona() {
        return ubicacionZona;
    }

    public void setUbicacionZona(String ubicacionZona) {
        this.ubicacionZona = ubicacionZona;
    }

    public String getUbicacionEstante() {
        return ubicacionEstante;
    }

    public void setUbicacionEstante(String ubicacionEstante) {
        this.ubicacionEstante = ubicacionEstante;
    }

    public String getUbicacionRepisa() {
        return ubicacionRepisa;
    }

    public void setUbicacionRepisa(String ubicacionRepisa) {
        this.ubicacionRepisa = ubicacionRepisa;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
