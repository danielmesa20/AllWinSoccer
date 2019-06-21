package com.example.allwinsoccer.Models;

public class Equipo {

    private String nombre, idEquipo;
    private  int puntos, gF, gC, pG, pE, pP, grupo;

    public String getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(String idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setGrupo(int grupo) {
        this.grupo = grupo;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getgF() {
        return gF;
    }

    public void setgF(int gF) {
        this.gF = gF;
    }

    public int getgC() {
        return gC;
    }

    public void setgC(int gC) {
        this.gC = gC;
    }

    public int getpG() {
        return pG;
    }

    public void setpG(int pG) {
        this.pG = pG;
    }

    public int getpE() {
        return pE;
    }

    public void setpE(int pE) {
        this.pE = pE;
    }

    public int getpP() {
        return pP;
    }

    public void setpP(int pP) {
        this.pP = pP;
    }
}
