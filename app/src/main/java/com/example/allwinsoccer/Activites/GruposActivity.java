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
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Equipo;
import com.example.allwinsoccer.Objects.AdapterRecyclerGrupo;
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

public class GruposActivity extends AppCompatActivity {

    private RecyclerView rv_GrupoA, rv_GrupoB, rv_GrupoC;
    private ScrollView scrollView;
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
                    goApostar();
                    return true;
                case R.id.navigation_notifications:
                    goPronostico();
                    return true;
                case R.id.navigation_position:
                    goPosicion();
                    return true;
                case R.id.navigation_update:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setVisibility(View.INVISIBLE);

        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);

        rv_GrupoA = findViewById(R.id.rv_grupoA);
        rv_GrupoA.setLayoutManager(new LinearLayoutManager(this));
        rv_GrupoB = findViewById(R.id.rv_grupoB);
        rv_GrupoB.setLayoutManager(new LinearLayoutManager(this));
        rv_GrupoC = findViewById(R.id.rv_grupoC);
        rv_GrupoC.setLayoutManager(new LinearLayoutManager(this));

        mostrarGrupos();
    }

    private void mostrarGrupos(){

        final ProgressDialog progressDialog = new ProgressDialog(GruposActivity.this);
        progressDialog.setMessage("Cargando los información...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final List<Equipo> equiposGA, equiposGB, equiposGC;
        final AdapterRecyclerGrupo adapterRecyclerGrupoA,adapterRecyclerGrupoB, adapterRecyclerGrupoC;

        equiposGA = new ArrayList<>();
        adapterRecyclerGrupoA = new AdapterRecyclerGrupo(equiposGA);
        rv_GrupoA.setAdapter(adapterRecyclerGrupoA);

        equiposGB = new ArrayList<>();
        adapterRecyclerGrupoB = new AdapterRecyclerGrupo(equiposGB);
        rv_GrupoB.setAdapter(adapterRecyclerGrupoB);

        equiposGC = new ArrayList<>();
        adapterRecyclerGrupoC = new AdapterRecyclerGrupo(equiposGC);
        rv_GrupoC.setAdapter(adapterRecyclerGrupoC);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Equipos").orderBy("puntos", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                equiposGA.clear();
                equiposGB.clear();
                equiposGC.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Equipo e = document.toObject(Equipo.class);
                        if(e.getGrupo() == 0){          // 0: Grupo A
                            equiposGA.add(e);
                        }else if(e.getGrupo() == 1){    // 1: Grupo B
                            equiposGB.add(e);
                        }else{                          // 2: Grupo C
                            equiposGC.add(e);
                        }
                    }
                    adapterRecyclerGrupoA.notifyDataSetChanged();
                    adapterRecyclerGrupoB.notifyDataSetChanged();
                    adapterRecyclerGrupoC.notifyDataSetChanged();
                } else
                    Toast.makeText(GruposActivity.this, "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();

                scrollView.setVisibility(View.VISIBLE);
                navView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }

    private void goPrincipalActivity() {
        Intent i = new Intent(GruposActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPronostico() {
        Intent i = new Intent(GruposActivity.this, PronosticoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPosicion() {
        Intent i = new Intent(GruposActivity.this, PosicionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goApostar() {
        Intent i = new Intent(GruposActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

}
