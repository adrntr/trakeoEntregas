package com.example.ingeniera.trakeoentregas.Ingreso;

public class HojasDeRuta {
    int Codigo,cantDestinos;
    String fecha;

    public int getCodigo() {
        return Codigo;
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

