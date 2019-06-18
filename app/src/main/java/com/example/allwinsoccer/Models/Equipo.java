package com.example.allwinsoccer.Models;

public class Equipo {

    private String nombre, grupo;
    private  int puntos, gFavor, gContra, pG, pE, pP;

    public int getpG() { return pG; }

    public void setpG(int pG) { this.pG = pG; }

    public int getpE() { return pE; }

    public void setpE(int pE) { this.pE = pE; }

    public int getpP() { return pP; }

    public void setpP(int pP) { this.pP = pP; }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getgFavor() {
        return gFavor;
    }

    public void setgFavor(int gFavor) {
        this.gFavor = gFavor;
    }

    public int getgContra() {
        return gContra;
    }

    public void setgContra(int gContra) {
        this.gContra = gContra;
    }

}
