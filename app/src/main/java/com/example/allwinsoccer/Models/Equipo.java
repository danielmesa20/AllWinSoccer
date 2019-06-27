package com.example.allwinsoccer.Models;

public class Equipo {

    private String nombre, idEquipo;
    private  int puntos, gF, gC, pG, pE, pP, grupo;

    public String getIdEquipo() {
        return idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getgF() {
        return gF;
    }

    public int getgC() {
        return gC;
    }

    public int getpG() {
        return pG;
    }

    public int getpE() {
        return pE;
    }

    public int getpP() {
        return pP;
    }

}
