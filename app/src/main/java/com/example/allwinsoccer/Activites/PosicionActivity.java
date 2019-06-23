package com.example.allwinsoccer.Activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.Objects.AdapterRecyclerPosicion;
import com.example.allwinsoccer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PosicionActivity extends AppCompatActivity{

    private RecyclerView rv;
    private BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goPrincipalActivity();
                    return true;
                case R.id.navigation_dashboard:
                    goApostarActivity();
                    return true;
                case R.id.navigation_notifications:
                    goPronosticoActivity();
                    return true;
                case R.id.navigation_position:
                    return true;
                case R.id.navigation_update:
                   goGrupos();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setVisibility(View.INVISIBLE);
        rv = findViewById(R.id.rv_usuarios);
        rv.setLayoutManager(new LinearLayoutManager(this));
        listarUsuarios();
    }

    private void listarUsuarios() {

        final ProgressDialog progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Cargando Posiciones...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final List<Usuario> usuarios;
        final AdapterRecyclerPosicion adapterRecyclerPosicion;
        usuarios = new ArrayList<>();
        adapterRecyclerPosicion = new AdapterRecyclerPosicion(usuarios, getIntent().getStringExtra("idUser"));
        rv.setAdapter(adapterRecyclerPosicion);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").orderBy("puntosUser", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                usuarios.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Usuario u = document.toObject(Usuario.class);
                        usuarios.add(u);
                    }
                    adapterRecyclerPosicion.notifyDataSetChanged();
                } else {
                    Toast.makeText(PosicionActivity.this, "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                }

                navView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });

    }

    private void goPrincipalActivity() {
        Intent i = new Intent(PosicionActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void goApostarActivity() {
        Intent i = new Intent(PosicionActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPronosticoActivity() {
        Intent i = new Intent(PosicionActivity.this, PronosticoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goGrupos() {
        Intent i = new Intent(PosicionActivity.this, GruposActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

}
