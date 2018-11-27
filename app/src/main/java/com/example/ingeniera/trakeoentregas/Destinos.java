package com.example.ingeniera.trakeoentregas;

import com.google.android.gms.maps.model.LatLng;

public class Destinos {

    private Double latitude,longitude;
    private String nombre_cliente,transporte,direccion_transporte,telefono,fechaHoraEntrega;
    private int idDestino,codigoRemito,idCliente,cantidadBultos,tipoEntrega;
    Boolean entregado;


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

    public String getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(String fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    public String getTelefono() {
        return telefono;

    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getIdDestino() {
        return idDestino;
    }

    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public int getCodigoRemito() {
        return codigoRemito;
    }

    public void setCodigoRemito(int codigoRemito) {
        this.codigoRemito = codigoRemito;
    }

    public int getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(int tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public Boolean getEntregado() {
        return entregado;
    }

    public void setEntregado(Boolean entregado) {
        this.entregado = entregado;
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
