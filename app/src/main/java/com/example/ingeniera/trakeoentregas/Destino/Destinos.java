package com.example.ingeniera.trakeoentregas.Destino;

import java.util.ArrayList;

public class Destinos {

    private Double latitude,longitude;
    private String nombre_tipo_registro,nombre_cliente,nombre_transporte,direccion_transporte,telefono,fechaHoraEntrega,motivo,direccion
            ,horario1_inicio,horario1_fin,horario2_incio,horario2_fin,fecha_despacho;
    private int id,id_cliente,cantidad,id_tipo_registro,orden,id_externo,id_transporte;
    Boolean entregado,agregadoDurRecorrido,cancelado;

    private ArrayList<Integer> ids_registro_ruta,ids_tipos_registro;

    public ArrayList<Integer> getIds_tipos_registro() {
        return ids_tipos_registro;
    }

    public void setIds_tipos_registro(ArrayList<Integer> ids_tipos_registro) {
        this.ids_tipos_registro = ids_tipos_registro;
    }

    public String getFecha_despacho() {
        return fecha_despacho;
    }

    public void setFecha_despacho(String fecha_despacho) {
        this.fecha_despacho = fecha_despacho;
    }

    public ArrayList<Integer> getIds_registro_ruta() {
        return ids_registro_ruta;
    }

    public void setIds_registro_ruta(ArrayList<Integer> ids_registro_ruta) {
        this.ids_registro_ruta = ids_registro_ruta;
    }

    public int getId_transporte() {
        return id_transporte;

    }

    public void setId_transporte(int id_transporte) {
        this.id_transporte = id_transporte;
    }

    public String gethorario1_inicio() {
        return horario1_inicio;
    }

    public void sethorario1_inicio(String horario1_inicio) {
        this.horario1_inicio = horario1_inicio;
    }

    public String gethorario1_fin() {
        return horario1_fin;
    }

    public void sethorario1_fin(String horario1_fin) {
        this.horario1_fin = horario1_fin;
    }

    public String gethorario2_incio() {
        return horario2_incio;
    }

    public void sethorario2_incio(String horario2_incio) {
        this.horario2_incio = horario2_incio;
    }

    public String gethorario2_fin() {
        return horario2_fin;
    }

    public void sethorario2_fin(String horario2_fin) {
        this.horario2_fin = horario2_fin;
    }

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
