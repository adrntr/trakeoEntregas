package com.example.ingeniera.trakeoentregas.Destino;

public class Usuarios {
    String nombre,dni,tipo; //tipo puede ser Responsable o Acompañante


    public Usuarios(String nombre, String dni, String tipo) {
        this.nombre = nombre;
        this.dni = dni;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
