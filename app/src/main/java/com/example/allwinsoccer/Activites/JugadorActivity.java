package com.example.allwinsoccer.Activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Jugador;
import com.example.allwinsoccer.Objects.AdapterRecyclerJugador;
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

public class JugadorActivity extends AppCompatActivity implements AdapterRecyclerJugador.OnNoteListener {

    private RecyclerView rv_jugadores, rv_porteros;
    private List<Jugador> jugadores, porteros;
    private AdapterRecyclerJugador adapterRecyclerJugador, adapterRecyclerPorteros;
    private String idJugador = null, idPortero = null;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugador);
        rv_jugadores = findViewById(R.id.rv_jugadores);
        rv_jugadores.setLayoutManager(new LinearLayoutManager(this));
        rv_porteros = findViewById(R.id.rv_porteros);
        rv_porteros.setLayoutManager(new LinearLayoutManager(this));
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);
        mostrarJugadores();
    }

    private void mostrarJugadores() {

        final ProgressDialog progressDialog = new ProgressDialog(JugadorActivity.this);
        progressDialog.setMessage("Cargando los Jugadores...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        jugadores = new ArrayList<>();
        porteros = new ArrayList<>();
        adapterRecyclerJugador = new AdapterRecyclerJugador(jugadores, this);
        adapterRecyclerPorteros = new AdapterRecyclerJugador(porteros, this);
        rv_jugadores.setAdapter(adapterRecyclerJugador);
        rv_porteros.setAdapter(adapterRecyclerPorteros);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Jugadores").orderBy("pais", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                jugadores.clear();
                porteros.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Jugador j = document.toObject(Jugador.class);
                        if (j.getPosicion().equals("Portero")) {
                            porteros.add(j);
                        } else {
                            jugadores.add(j);
                        }
                    }
                    adapterRecyclerJugador.notifyDataSetChanged();
                    adapterRecyclerPorteros.notifyDataSetChanged();
                } else {
                    Toast.makeText(JugadorActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                scrollView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }

    public void premiosTorneo(View view){

        Toast.makeText(this, "j "+idJugador, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "p "+idPortero, Toast.LENGTH_SHORT).show();

        if(idJugador == null || idPortero == null){
            Toast.makeText(this, "Debe elegir ambos jugadores", Toast.LENGTH_SHORT).show();
        }else if(getIntent().getStringExtra("idUser").isEmpty() ||  getIntent().getStringExtra("idUser")==null){
            Toast.makeText(this, "Error user", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro son los jugadores que quieres ingresar? (no podrás cambiarlos)");
            builder.setTitle("Advertencia");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final ProgressDialog progressDialog = new ProgressDialog(JugadorActivity.this);
                    progressDialog.setMessage("Guardando Datos");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Usuarios").document( getIntent().getStringExtra("idUser")).update("idMejorJugador",idJugador,"idMejorPorteros", idPortero);
                    reiniciar();
                    Toast.makeText(JugadorActivity.this, "Elecciones guardadas Correctamente", Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void reiniciar() {
        idPortero = null;
        idJugador = null;
    }

    @Override
    public void onNoteClick(int position) {
        Jugador j = jugadores.get(position);
        Toast.makeText(this, "a "+j.getPosicion(), Toast.LENGTH_SHORT).show();
        if(j.getPosicion().equals("Portero")){
            idPortero = j.getIdJugador();
        }else{
            idJugador = j.getIdJugador();
        }
    }

}
