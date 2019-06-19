package com.example.allwinsoccer.Objects;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Models.Equipo;
import com.example.allwinsoccer.R;

import java.util.List;

public class AdapterRecyclerGrupo extends RecyclerView.Adapter<AdapterRecyclerGrupo.GruposViewHolder>{

    private List<Equipo> equipos;

    public AdapterRecyclerGrupo(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    @NonNull
    @Override
    public GruposViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_partidos, viewGroup, false);
        return new GruposViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GruposViewHolder gruposViewHolder, int i) {
        Equipo e = equipos.get(i);
        gruposViewHolder.nombre.setText(e.getNombre());
        gruposViewHolder.pJugados.setText(String.valueOf(e.getpG()+e.getpE()+e.getpP()));
        gruposViewHolder.pGanados.setText(String.valueOf(e.getpG()));
        gruposViewHolder.pEmpatados.setText(String.valueOf(e.getpE()));
        gruposViewHolder.pPerdidos.setText(String.valueOf(e.getpP()));
        gruposViewHolder.gFavor.setText(String.valueOf(e.getgF()));
        gruposViewHolder.gContra.setText(String.valueOf(e.getgC()));
        gruposViewHolder.gDife.setText(String.valueOf(e.getgF()-e.getgC()));
        gruposViewHolder.puntos.setText(String.valueOf(e.getPuntos()));
        actualizarIMG(e.getNombre(),gruposViewHolder.bandera);
    }

    @Override
    public int getItemCount() {
       return equipos.size();
    }

    public static class GruposViewHolder extends RecyclerView.ViewHolder{

        TextView pJugados, pGanados, pEmpatados, pPerdidos, gFavor, gContra, gDife, puntos, nombre;
        ImageView bandera;

        public GruposViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            bandera = itemView.findViewById(R.id.bandera);
            pJugados = itemView.findViewById(R.id.pJugados);
            pGanados = itemView.findViewById(R.id.pGanados);
            pEmpatados = itemView.findViewById(R.id.pEmpatados);
            pPerdidos = itemView.findViewById(R.id.pPerdidos);
            gFavor = itemView.findViewById(R.id.gFavor);
            gContra = itemView.findViewById(R.id.gContra);
            gDife = itemView.findViewById(R.id.gDife);
            puntos = itemView.findViewById(R.id.puntos);
        }
    }

    private void actualizarIMG(String name, ImageView iv) {
        iv.setVisibility(View.VISIBLE);
        switch (name) {
            case "Argentina":
                iv.setImageResource(R.drawable.ar);
                break;
            case "Brasil":
                iv.setImageResource(R.drawable.bra);
                break;
            case "Bolivia":
                iv.setImageResource(R.drawable.bo);
                break;
            case "Perú":
                iv.setImageResource(R.drawable.pe);
                break;
            case "Chile":
                iv.setImageResource(R.drawable.cl);
                break;
            case "Colombia":
                iv.setImageResource(R.drawable.co);
                break;
            case "Ecuador":
                iv.setImageResource(R.drawable.ec);
                break;
            case "Japón":
                iv.setImageResource(R.drawable.jp);
                break;
            case "Paraguay":
                iv.setImageResource(R.drawable.py);
                break;
            case "Qatar":
                iv.setImageResource(R.drawable.qa);
                break;
            case "Uruguay":
                iv.setImageResource(R.drawable.uy);
                break;
            case "Venezuela":
                iv.setImageResource(R.drawable.ven);
                break;
            default:
                break;
        }
    }
}
