package com.foodtech.ms_factura.domain;

import java.util.List;

@SuppressWarnings({"PMD.DataClass", "PMD.CyclomaticComplexity"})
public class Factura {
    private String nombreCliente;
    private String emailCliente;
    private List<Producto> listaProductos;
    private double total;
    private String formato;

    public Factura() {
    }

    public Factura(String nombreCliente, List<Producto> listaProductos, double total, String formato) {
        this.nombreCliente = nombreCliente;
        this.listaProductos = listaProductos;
        this.total = total;
        this.formato = formato;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }
}