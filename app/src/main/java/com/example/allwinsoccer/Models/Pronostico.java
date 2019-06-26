package com.example.allwinsoccer.Models;

public class Pronostico {

    private String idPronostico, idUsuario, idPartido, nlocal, nvisit, fecha;
    private int puntos, gvisit, glocal, extra;

    public int getExtra() { return extra; }

    public void setExtra(int extra) { this.extra = extra; }

    public String getIdPronostico() {
        return idPronostico;
    }

    public void setIdPronostico(String idPronostico) {
        this.idPronostico = idPronostico;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getGvisit() {
        return gvisit;
    }

    public void setGvisit(int gvisit) {
        this.gvisit = gvisit;
    }

    public int getGlocal() {
        return glocal;
    }

    public void setGlocal(int glocal) {
        this.glocal = glocal;
    }

    @Override
    public String toString() {
        if(puntos != -1){
            return nlocal+"  "+glocal+ "   -   " +gvisit+"  "+nvisit+ "\n" + "Puntos Obtenidos:  "+puntos;
        }else{
            return nlocal+"  "+glocal+ "   -   " +gvisit+"  "+nvisit+ "\n" + "Partido no finalizado";
        }
    }
}
