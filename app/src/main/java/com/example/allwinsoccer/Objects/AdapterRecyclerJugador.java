package com.example.allwinsoccer.Objects;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Models.Jugador;
import com.example.allwinsoccer.R;

import java.util.List;

public class AdapterRecyclerJugador extends RecyclerView.Adapter<AdapterRecyclerJugador.JugadoresViewHolder> {

    private List<Jugador> jugadores;
    private OnNoteListener mOnNoteListener;

    public AdapterRecyclerJugador(List<Jugador> jugadores, OnNoteListener onNoteListener) {
        this.jugadores = jugadores;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public JugadoresViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_jugadores, viewGroup, false);
        return new JugadoresViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull JugadoresViewHolder jugadoresViewHolder, int i) {
        Jugador j = jugadores.get(i);
        jugadoresViewHolder.nombre_jugador.setText(j.getNombre());
        jugadoresViewHolder.posicion_jugador.setText(j.getPosicion());
        actualizarIMG(j.getPais(),jugadoresViewHolder.bandera_jugador);
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public static class JugadoresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nombre_jugador, posicion_jugador;
        ImageView bandera_jugador;
        OnNoteListener onNoteListener;

        JugadoresViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            bandera_jugador = itemView.findViewById(R.id.bandera_jugador);
            nombre_jugador = itemView.findViewById(R.id.nombre_jugador);
            posicion_jugador = itemView.findViewById(R.id.posicion_jugador);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }


    public interface OnNoteListener {
        void onNoteClick(int position);
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


