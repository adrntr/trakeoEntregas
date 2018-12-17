package com.example.ingeniera.trakeoentregas.Ingreso;

public class HojasDeRuta {
    int Codigo,cantDestinos,cantPendientes;
    String fecha;

    public int getCodigo() {
        return Codigo;
    }

    public int getCantPendientes() {
        return cantPendientes;
    }

    public void setCantPendientes(int cantPendientes) {
        this.cantPendientes = cantPendientes;
    }

    public void setCodigo(int codigo) {
        Codigo = codigo;
    }

    public int getCantDestinos() {
        return cantDestinos;
    }

    public void setCantDestinos(int cantDestinos) {
        this.cantDestinos = cantDestinos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

