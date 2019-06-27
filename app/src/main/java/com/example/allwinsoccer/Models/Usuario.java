package com.example.allwinsoccer.Models;

public class Usuario {

    private String idUsuario, email, nombre, idMejorPortero, idMejorJugador, url;
    private int puntosUser;

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdMejorPortero() {
        return idMejorPortero;
    }

    public String getIdMejorJugador() {
        return idMejorJugador;
    }

    public int getPuntosUser() {
        return puntosUser;
    }

    public void setPuntosUser(int puntosUser) {
        this.puntosUser = puntosUser;
    }
}
