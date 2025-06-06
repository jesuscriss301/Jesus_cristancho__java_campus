package com.prueba.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "detalle_venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida;

    @Column(name = "precio_unitario_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioVenta;

    public DetalleVenta(Venta venta, Producto producto, Integer cantidadVendida, BigDecimal precioUnitarioVenta) {
        this.venta = venta;
        this.producto = producto;
        this.cantidadVendida = cantidadVendida;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }
}