package com.example.ingeniera.trakeoentregas;

import com.google.android.gms.maps.model.LatLng;

public class Destinos {

    private Double latitude,longitude;
    private String nombre_cliente,transporte,direccion_transporte;
    private int idCliente,cantidadBultos;


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getTransporte() {
        return transporte;
    }

    public void setTransporte(String transporte) {
        this.transporte = transporte;
    }

    public String getDireccion_transporte() {
        return direccion_transporte;
    }

    public void setDireccion_transporte(String direccion_transporte) {
        this.direccion_transporte = direccion_transporte;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCantidadBultos() {
        return cantidadBultos;
    }

    public void setCantidadBultos(int cantidadBultos) {
        this.cantidadBultos = cantidadBultos;
    }
}
