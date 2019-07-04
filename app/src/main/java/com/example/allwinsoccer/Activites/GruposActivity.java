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
import com.example.allwinsoccer.Models.Partido;
import com.example.allwinsoccer.Objects.AdapterRecyclerGrupo;
import com.example.allwinsoccer.Objects.AdapterRecyclerPartido;
import com.example.allwinsoccer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GruposActivity extends AppCompatActivity implements AdapterRecyclerPartido.OnNoteListener {

    private RecyclerView rv_GrupoA, rv_GrupoB, rv_GrupoC, rv_cuartos, rv_semifinal, rv_tercerlugar, rv_final;
    private ScrollView scrollView;
    private BottomNavigationView navView;
    private ProgressDialog progressDialog;

    // Barra de navegación
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
        rv_cuartos = findViewById(R.id.rv_cuartos);
        rv_cuartos.setLayoutManager(new LinearLayoutManager(this));
        rv_semifinal = findViewById(R.id.rv_semifinales);
        rv_semifinal.setLayoutManager(new LinearLayoutManager(this));
        rv_tercerlugar = findViewById(R.id.rv_tercerlugar);
        rv_tercerlugar.setLayoutManager(new LinearLayoutManager(this));
        rv_final = findViewById(R.id.rv_final);
        rv_final.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(GruposActivity.this);
        mostrarGrupos();
        mostrarFaseFinal();
    }

    //Muestra la tabla de posiciones de los equipos en la fase de grupos
    private void mostrarGrupos() {

        progressDialog.setMessage("Cargando datos de la Copa...");      // Mensaje de carga
        progressDialog.setCancelable(false);
        progressDialog.show();

        final List<Equipo> equiposGA, equiposGB, equiposGC;
        final AdapterRecyclerGrupo adapterRecyclerGrupoA, adapterRecyclerGrupoB, adapterRecyclerGrupoC;

        equiposGA = new ArrayList<>();                                  // Tabla grupo A
        adapterRecyclerGrupoA = new AdapterRecyclerGrupo(equiposGA);
        rv_GrupoA.setAdapter(adapterRecyclerGrupoA);

        equiposGB = new ArrayList<>();                                  // Tabla grupo B
        adapterRecyclerGrupoB = new AdapterRecyclerGrupo(equiposGB);
        rv_GrupoB.setAdapter(adapterRecyclerGrupoB);

        equiposGC = new ArrayList<>();                                  // Tabla grupo C
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
                        if (e.getGrupo() == 0) {           // 0: Grupo A
                            equiposGA.add(e);
                        } else if (e.getGrupo() == 1) {    // 1: Grupo B
                            equiposGB.add(e);
                        } else {                           // 2: Grupo C
                            equiposGC.add(e);
                        }
                    }
                    adapterRecyclerGrupoA.notifyDataSetChanged();
                    adapterRecyclerGrupoB.notifyDataSetChanged();
                    adapterRecyclerGrupoC.notifyDataSetChanged();
                } else
                    Toast.makeText(GruposActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Muestra los encuentros de la fase final de la copa
    private void mostrarFaseFinal() {
        final List<Partido> cuartosFinal, semifinales, tercerlugar, finals;
        final AdapterRecyclerPartido adapterRecyclerCuartos, adapterRecyclerSemifinal, adapterRecyclerTercer, adapterRecyclerFinal;

        cuartosFinal = new ArrayList<>();
        semifinales = new ArrayList<>();
        tercerlugar = new ArrayList<>();
        finals = new ArrayList<>();

        adapterRecyclerCuartos = new AdapterRecyclerPartido(cuartosFinal, this);
        adapterRecyclerSemifinal = new AdapterRecyclerPartido(semifinales, this);
        adapterRecyclerTercer = new AdapterRecyclerPartido(tercerlugar, this);
        adapterRecyclerFinal = new AdapterRecyclerPartido(finals, this);

        rv_cuartos.setAdapter(adapterRecyclerCuartos);
        rv_semifinal.setAdapter(adapterRecyclerSemifinal);
        rv_tercerlugar.setAdapter(adapterRecyclerTercer);
        rv_final.setAdapter(adapterRecyclerFinal);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Partidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cuartosFinal.clear();
                    semifinales.clear();
                    finals.clear();
                    tercerlugar.clear();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Partido p = document.toObject(Partido.class);
                        switch (p.getFase()) {
                            case "Cuartos de final":
                                cuartosFinal.add(p);
                                Collections.sort(cuartosFinal, new Comparator<Partido>() {
                                    @Override
                                    public int compare(Partido o1, Partido o2) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
                                        Date f1 = null, f2 = null;
                                        try {
                                            f1 = dateFormat.parse(o1.getFecha());
                                            f2 = dateFormat.parse(o2.getFecha());
                                        } catch (ParseException ex) {
                                            Toast.makeText(GruposActivity.this, "Error parse", Toast.LENGTH_SHORT).show();
                                        }
                                        assert f1 != null;
                                        return f1.compareTo(f2);
                                    }
                                });
                                break;
                            case "Semifinales":
                                semifinales.add(p);
                                Collections.sort(semifinales, new Comparator<Partido>() {
                                    @Override
                                    public int compare(Partido o1, Partido o2) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
                                        Date f1 = null, f2 = null;
                                        try {
                                            f1 = dateFormat.parse(o1.getFecha());
                                            f2 = dateFormat.parse(o2.getFecha());
                                        } catch (ParseException ex) {
                                            Toast.makeText(GruposActivity.this, "Error parse", Toast.LENGTH_SHORT).show();
                                        }
                                        assert f1 != null;
                                        return f1.compareTo(f2);
                                    }
                                });
                                break;
                            case "Tercer lugar":
                                tercerlugar.add(p);
                                break;
                            case "Final":
                                finals.add(p);
                                break;
                        }
                    }
                    adapterRecyclerCuartos.notifyDataSetChanged();
                    adapterRecyclerSemifinal.notifyDataSetChanged();
                    adapterRecyclerTercer.notifyDataSetChanged();
                    adapterRecyclerFinal.notifyDataSetChanged();
                } else
                    Toast.makeText(GruposActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();

                scrollView.setVisibility(View.VISIBLE);
                navView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }

    //Metodos por ir de esta activity a otra
    private void goPrincipalActivity() {
        Intent i = new Intent(GruposActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goApostar() {
        Intent i = new Intent(GruposActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    @Override
    public void onNoteClick(int position) {
    }
}
