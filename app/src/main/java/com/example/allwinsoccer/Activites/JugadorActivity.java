package com.example.allwinsoccer.Activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Jugador;
import com.example.allwinsoccer.Models.Usuario;
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

    private RecyclerView rv_jugadores;
    private List<Jugador> jugadores;
    private AdapterRecyclerJugador adapterRecyclerJugador;
    private String idJugador = null, nombre = null;
    private ConstraintLayout constraintLayout;
    private TextView jugador_nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugador);
        rv_jugadores = findViewById(R.id.rv_jugadores);
        constraintLayout = findViewById(R.id.jugador_ly);
        constraintLayout.setVisibility(View.INVISIBLE);
        rv_jugadores.setLayoutManager(new LinearLayoutManager(this));
        jugador_nombre = findViewById(R.id.jugador_nombre);
        mostrarJugadores();
    }

    private void mostrarJugadores() {

        final ProgressDialog progressDialog = new ProgressDialog(JugadorActivity.this);
        progressDialog.setMessage("Cargando los Jugadores...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        jugadores = new ArrayList<>();
        adapterRecyclerJugador = new AdapterRecyclerJugador(jugadores, this);
        rv_jugadores.setAdapter(adapterRecyclerJugador);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Jugadores").orderBy("pais", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                jugadores.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Jugador j = document.toObject(Jugador.class);
                        if (!j.getPosicion().equals("Portero")) {
                            jugadores.add(j);
                        }
                    }
                    adapterRecyclerJugador.notifyDataSetChanged();
                } else {
                    Toast.makeText(JugadorActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                constraintLayout.setVisibility(View.VISIBLE);
                jugador_nombre.setVisibility(View.GONE);
                progressDialog.dismiss();
                mostrarInformacion();
            }
        });
    }

    private  void mostrarInformacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("En esta pantalla podrás escoger tu candidado al Balón de Oro de la Copa América 2019." +
                " Si aciertas se te asignaran 20 puntos al finalizar la competición.");
        builder.setTitle("Info, fecha tope 15:00 28/06");
        builder.setPositiveButton("CONTINUAR",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("VOLVER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goPrincipal();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private  void mostrarConfirmacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se ha guardado tu elección de "+nombre+" al Balón de Oro, los puntos correspondientes " +
                "se sumarán al finalizar la Copa Ámerica 2019.");
        builder.setTitle("Confirmación");
        builder.setPositiveButton("CONTINUAR",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                goPrincipal();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void premiosTorneo(View view) {
        if (idJugador == null || idJugador.isEmpty()) {
            Toast.makeText(this, "Primero debe elegir un jugador", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro que "+nombre+" es el jugador que quiere elegir? (no podrá cambiarlo)");
            builder.setTitle("Confirmación");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String id = idJugador;
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Usuarios").whereEqualTo("idUsuario", getIntent().getStringExtra("idUser")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Usuario u = document.toObject(Usuario.class);
                                    db.collection("Usuarios").document(u.getIdUsuario()).update("idMejorJugador", id);
                                }
                            } else {
                                Toast.makeText(JugadorActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Toast.makeText(JugadorActivity.this, "Guardado correctamente ", Toast.LENGTH_SHORT).show();
                    mostrarConfirmacion();
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

    private void goPrincipal() {
        Intent i = new Intent(JugadorActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onNoteClick(int position) {
        Jugador j = jugadores.get(position);
        idJugador = j.getIdJugador();
        nombre = j.getNombre();
        jugador_nombre.setText("Seleccionado: "+nombre);
        jugador_nombre.setVisibility(View.VISIBLE);
    }

}
