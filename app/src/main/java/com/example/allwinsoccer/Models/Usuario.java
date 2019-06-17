package com.example.allwinsoccer.Models;

public class Usuario {

    private String idUsuario, email, nombre, idMejorPortero, idMejorJugador, url;
    private int puntosUser;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
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

    public void setIdMejorPortero(String idMejorPortero) {
        this.idMejorPortero = idMejorPortero;
    }

    public String getIdMejorJugador() {
        return idMejorJugador;
    }

    public void setIdMejorJugador(String idMejorJugador) {
        this.idMejorJugador = idMejorJugador;
    }

    public int getPuntosUser() {
        return puntosUser;
    }

    public void setPuntosUser(int puntosUser) {
        this.puntosUser = puntosUser;
    }
}
