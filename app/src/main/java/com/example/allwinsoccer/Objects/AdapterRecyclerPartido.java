package com.example.allwinsoccer.Objects;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Models.Partido;
import com.example.allwinsoccer.R;

import java.util.List;

public class AdapterRecyclerPartido extends RecyclerView.Adapter<AdapterRecyclerPartido.PartidosViewHolder> {

    private List<Partido> partidos;
    private OnNoteListener mOnNoteListener;

    public AdapterRecyclerPartido(List<Partido> partidos, OnNoteListener onNoteListener) {
        this.partidos = partidos;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public PartidosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_items, viewGroup, false);
        return new PartidosViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidosViewHolder partidosViewHolder, int i) {
        Partido p = partidos.get(i);
        if (p.getGlocal() != -1) {
            partidosViewHolder.fecha.setText(R.string.partidoFinalizado);
            partidosViewHolder.tv_name_local.setText(p.getNlocal() + "    " + p.getGlocal());
            partidosViewHolder.tv_name_visit.setText(p.getGvisit() + "    " + p.getNvisit());

        } else {
            partidosViewHolder.fecha.setText("Fecha: " + p.getFecha());
            partidosViewHolder.tv_name_local.setText(p.getNlocal());
            partidosViewHolder.tv_name_visit.setText(p.getNvisit());
        }
        actualizarIMG(p.getNlocal(), partidosViewHolder.iv_bandera_local);
        actualizarIMG(p.getNvisit(), partidosViewHolder.iv_bandera_visit);
    }

    @Override
    public int getItemCount() {
        return partidos.size();
    }

    public static class PartidosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name_local, tv_name_visit, fecha;
        ImageView iv_bandera_local, iv_bandera_visit;
        OnNoteListener onNoteListener;

        PartidosViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            tv_name_local = itemView.findViewById(R.id.nLocal);
            tv_name_visit = itemView.findViewById(R.id.nVisit);
            iv_bandera_local = itemView.findViewById(R.id.bLocal);
            iv_bandera_visit = itemView.findViewById(R.id.bVisit);
            fecha = itemView.findViewById(R.id.fecha);
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
