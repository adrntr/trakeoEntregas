package com.example.ingeniera.trakeoentregas.Destino;

public class Destinos {

    private Double latitude,longitude;
    private String nombre_tipo_registro,nombre_cliente,nombre_transporte,direccion_transporte,telefono,fechaHoraEntrega,motivo,direccion;
    private int id,id_cliente,cantidad,id_tipo_registro,orden,id_externo;
    Boolean entregado,agregadoDurRecorrido,cancelado;

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    public Boolean getAgregadoDurRecorrido() {
        return agregadoDurRecorrido;
    }

    public void setAgregadoDurRecorrido(Boolean agregadoDurRecorrido) {
        this.agregadoDurRecorrido = agregadoDurRecorrido;
    }

    public String getDireccion() {
        return direccion;

    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre_tipo_registro() {

        return nombre_tipo_registro;
    }

    public void setNombre_tipo_registro(String nombre_tipo_registro) {
        this.nombre_tipo_registro = nombre_tipo_registro;
    }

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

    public int getOrden() {
        return orden;
    }

    public int getId_externo() {
        return id_externo;
    }

    public void setId_externo(int id_externo) {
        this.id_externo = id_externo;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getNombre_transporte() {
        return nombre_transporte;
    }

    public void setNombre_transporte(String nombre_transporte) {
        this.nombre_transporte = nombre_transporte;
    }

    public String getDireccion_transporte() {
        return direccion_transporte;
    }

    public void setDireccion_transporte(String direccion_transporte) {
        this.direccion_transporte = direccion_transporte;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaHoraEntrega() {
        return fechaHoraEntrega;
    }

    public void setFechaHoraEntrega(String fechaHoraEntrega) {
        this.fechaHoraEntrega = fechaHoraEntrega;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getId_tipo_registro() {
        return id_tipo_registro;
    }

    public void setId_tipo_registro(int id_tipo_registro) {
        this.id_tipo_registro = id_tipo_registro;
    }

    public Boolean getEntregado() {
        return entregado;
    }

    public void setEntregado(Boolean entregado) {
        this.entregado = entregado;
    }
}
