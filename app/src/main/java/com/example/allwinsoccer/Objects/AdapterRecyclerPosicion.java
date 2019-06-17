package com.example.allwinsoccer.Objects;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allwinsoccer.Activites.PosicionActivity;
import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerPosicion extends RecyclerView.Adapter<AdapterRecyclerPosicion.UsuariosViewHolder> {

    private List<Usuario> usuarios;
    private Context c;
    private int lugar;

    public AdapterRecyclerPosicion(List<Usuario> usuarios, Context c) {
        this.usuarios = usuarios;
        this.c = c;
        lugar = 0;
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
        usuariosViewHolder.nombre_usuario.setText(u.getNombre()+" puntos: "+String.valueOf(u.getPuntosUser()));
        usuariosViewHolder.lugar.setText(String.valueOf(i));
        if(u.getUrl() != null){
            loadImageFromUrl(u.getUrl(), usuariosViewHolder);
        }
    }

    private void loadImageFromUrl(String url, UsuariosViewHolder u){
        Picasso.with(c).load(url).placeholder(R.drawable.iconousuario)
        .error(R.drawable.iconousuario)
        .into(u.foto_usuario, new com.squareup.picasso.Callback(){
            @Override
            public void onSuccess() { }

            @Override
            public void onError() { }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public static class UsuariosViewHolder extends RecyclerView.ViewHolder{

        TextView nombre_usuario, lugar;
        ImageView foto_usuario;

        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre_usuario = itemView.findViewById(R.id.name_user);
            foto_usuario = itemView.findViewById(R.id.img_user);
            lugar = itemView.findViewById(R.id.lugar);
        }
    }

}
