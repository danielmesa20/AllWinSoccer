package com.example.allwinsoccer.Objects;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.R;

import java.util.List;

public class AdapterRecyclerPosicion extends RecyclerView.Adapter<AdapterRecyclerPosicion.UsuariosViewHolder> {

    private List<Usuario> usuarios;
    private String userId;

    public AdapterRecyclerPosicion(List<Usuario> usuarios, String userId) {
        this.usuarios = usuarios;
        this.userId = userId;
    }

    @NonNull
    @Override
    public UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_items_posicion, viewGroup, false);
        return new UsuariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosViewHolder usuariosViewHolder, int i) {
        Usuario u = usuarios.get(i);
        i++;
        usuariosViewHolder.nombre_usuario.setText(u.getNombre());
        usuariosViewHolder.puntos_usuario.setText(String.valueOf(u.getPuntosUser()));
        usuariosViewHolder.lugar.setText(String.valueOf(i));

        if (i == 1) {
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#FFC22A"));
        } else if (i == 2) {
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#8A9597"));
        } else if (i == 3) {
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#cd7f32"));
        } else {
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#37724d"));
        }

        if (u.getIdUsuario().equals(userId)) {
            usuariosViewHolder.itemView.findViewById(R.id.linearLayout7).setBackgroundColor(Color.parseColor("#145BBE"));
        } else {
            usuariosViewHolder.itemView.findViewById(R.id.linearLayout7).setBackgroundColor(Color.parseColor("#008577"));
        }
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuariosViewHolder extends RecyclerView.ViewHolder {

        TextView nombre_usuario, lugar, puntos_usuario;
        ImageView foto_usuario;

        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_usuario = itemView.findViewById(R.id.name_user);
            foto_usuario = itemView.findViewById(R.id.img_user);
            lugar = itemView.findViewById(R.id.lugar);
            puntos_usuario = itemView.findViewById(R.id.puntos_user);
        }
    }

}
