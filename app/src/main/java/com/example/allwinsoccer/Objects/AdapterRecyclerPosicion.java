package com.example.allwinsoccer.Objects;


import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecyclerPosicion extends RecyclerView.Adapter<AdapterRecyclerPosicion.UsuariosViewHolder> {

    private List<Usuario> usuarios;
    private Context c;
    private  String userId;

    public AdapterRecyclerPosicion(List<Usuario> usuarios, Context c, String userId) {
        this.usuarios = usuarios;
        this.c = c;
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
        /*if(u.getUrl() != null){
            loadImageFromUrl(u.getUrl(), usuariosViewHolder);
        }*/
       /*if(i==1){
            usuariosViewHolder.foto_usuario.setImageResource(R.drawable.icono_usuario_oro);
        }else if(i==2){
            usuariosViewHolder.foto_usuario.setImageResource(R.drawable.icono_usuario_plata);
        }else if(i==3){
            usuariosViewHolder.foto_usuario.setImageResource(R.drawable.icono_usuario_bronze);
        }else{
            usuariosViewHolder.foto_usuario.setImageResource(R.drawable.iconousuario);
        }*/

        if(i==1){
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#FFC22A"));
        }else if(i==2){
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#8A9597"));
        }else if(i==3){
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#cd7f32"));
        }else{
            usuariosViewHolder.itemView.findViewById(R.id.lugar).setBackgroundColor(Color.parseColor("#000000"));
        }

        if(u.getIdUsuario().equals(userId)){
            usuariosViewHolder.itemView.findViewById(R.id.linearLayout7).setBackgroundColor(Color.parseColor("#145BBE"));
        }else{
            usuariosViewHolder.itemView.findViewById(R.id.linearLayout7).setBackgroundColor(Color.parseColor("#008577"));
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
