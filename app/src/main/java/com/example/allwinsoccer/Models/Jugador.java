package com.example.allwinsoccer.Models;

public class Jugador {

    private String idJugador, nombre, pais, posicion;

    public String getIdJugador() {
        return idJugador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public String getPosicion() {
        return posicion;
    }

    @Override
    public String toString() {
        return  nombre+" ," +pais ;
    }
}
