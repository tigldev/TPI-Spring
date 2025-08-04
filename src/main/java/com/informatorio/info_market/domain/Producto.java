package com.informatorio.info_market.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
public class Producto {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String descripcion;
    private double precio;
    private int stock;
    private LocalDate fechaDeCreacion;
    private LocalDate fechaActualizacion;

    @ManyToMany
    private List<Categoria> categorias;


    private String marca;


    private Double descuentoAplicado;


    private Double precioOriginal;


    public Producto() {}


    public Producto(String nombre, String descripcion, double precio, int stock, LocalDate fechaDeCreacion, LocalDate fechaActualizacion, String marca) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.fechaDeCreacion = fechaDeCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.marca = marca;
        this.descuentoAplicado = 0.0;
        this.precioOriginal = precio;
    }


    public Producto(UUID id, String nombre, String descripcion, double precio, int stock, LocalDate fechaDeCreacion, LocalDate fechaActualizacion, List<Categoria> categorias, String marca) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.fechaDeCreacion = fechaDeCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.categorias = categorias;
        this.marca = marca;
        this.descuentoAplicado = 0.0;
        this.precioOriginal = precio;
    }


    @PrePersist
    public void prePersist() {
        if (this.fechaDeCreacion == null) {
            this.fechaDeCreacion = LocalDate.now();
        }
        if (this.precioOriginal == null) {
            this.precioOriginal = this.precio;
        }
        if (this.descuentoAplicado == null) {
            this.descuentoAplicado = 0.0;
        }
    }


    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDate.now();
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(LocalDate fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }


    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(Double descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public Double getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(Double precioOriginal) {
        this.precioOriginal = precioOriginal;
    }


    public void aplicarDescuento(Double porcentajeDescuento) {
        if (porcentajeDescuento == null || porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }

        if (this.precioOriginal == null) {
            this.precioOriginal = this.precio;
        }

        this.descuentoAplicado = porcentajeDescuento;
        this.precio = this.precioOriginal * (1 - (porcentajeDescuento / 100));


        if (this.precio < 0) {
            this.precio = 0.0;
        }

        this.precio = Math.round(this.precio * 100.0) / 100.0;
    }

    public void revertirDescuento() {
        if (this.precioOriginal != null) {
            this.precio = this.precioOriginal;
            this.descuentoAplicado = 0.0;
            this.precio = Math.round(this.precio * 100.0) / 100.0;
        }
    }
}