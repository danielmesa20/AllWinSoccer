package com.example.allwinsoccer.Objects;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Models.Pronostico;
import com.example.allwinsoccer.R;

import java.util.List;

public class AdapterRecyclerPronostico extends RecyclerView.Adapter<AdapterRecyclerPronostico.PronosticosViewHolder>{

    private List<Pronostico> pronosticos;
    private OnNoteListener mOnNoteListener;

    public AdapterRecyclerPronostico(List<Pronostico> pronosticos, OnNoteListener onNoteListener) {
        this.pronosticos = pronosticos;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public PronosticosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_items_pronostico, viewGroup, false);
        return new PronosticosViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PronosticosViewHolder pronosticosViewHolder, int i) {
            Pronostico p = pronosticos.get(i);
            pronosticosViewHolder.tv_name_local.setText(p.getNlocal());
            pronosticosViewHolder.tv_name_visit.setText(p.getNvisit());
            pronosticosViewHolder.tv_goles_local.setText(String.valueOf(p.getGlocal()));
            pronosticosViewHolder.tv_goles_visit.setText(String.valueOf(p.getGvisit()));
            if(p.getPuntos()!=-1){
                pronosticosViewHolder.puntos.setText("Puntos Obtenidos: "+String.valueOf(p.getPuntos()));
            }else{
                pronosticosViewHolder.puntos.setText(R.string.partidoNoFinalizado);
            }
            actualizarIMG(p.getNlocal(),pronosticosViewHolder.iv_bandera_local);
            actualizarIMG(p.getNvisit(),pronosticosViewHolder.iv_bandera_visit);
    }

    @Override
    public int getItemCount() {
        return pronosticos.size();
    }

    public static class PronosticosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_name_local,tv_name_visit, puntos, tv_goles_local, tv_goles_visit;
        ImageView iv_bandera_local, iv_bandera_visit;
        OnNoteListener onNoteListener;

        public PronosticosViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            tv_name_local = itemView.findViewById(R.id.nLocal);
            tv_name_visit = itemView.findViewById(R.id.nVisit);
            tv_goles_local = itemView.findViewById(R.id.gLocal);
            tv_goles_visit = itemView.findViewById(R.id.gVisit);
            iv_bandera_local = itemView.findViewById(R.id.bLocal);
            iv_bandera_visit = itemView.findViewById(R.id.bVisit);
            puntos = itemView.findViewById(R.id.aux);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    private void actualizarIMG(String name, ImageView iv) {
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


