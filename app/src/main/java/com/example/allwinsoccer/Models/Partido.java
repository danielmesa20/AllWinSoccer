package com.example.allwinsoccer.Models;

public class Partido {

    private String idPartido, nlocal, nvisit, ubicacion, fase, grupo, fecha;
    private int glocal, gvisit;


    public String getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(String idPartido) {
        this.idPartido = idPartido;
    }

    public String getNlocal() {
        return nlocal;
    }

    public void setNlocal(String nlocal) {
        this.nlocal = nlocal;
    }

    public String getNvisit() {
        return nvisit;
    }

    public void setNvisit(String nvisit) {
        this.nvisit = nvisit;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getGlocal() {
        return glocal;
    }

    public void setGlocal(int glocal) {
        this.glocal = glocal;
    }

    public int getGvisit() {
        return gvisit;
    }

    public void setGvisit(int gvisit) {
        this.gvisit = gvisit;
    }
}
