package com.foodtech.ms_factura.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    private String nombreCliente;
    private List<Producto> listaProductos;
    private double total;
    private String formato;
}