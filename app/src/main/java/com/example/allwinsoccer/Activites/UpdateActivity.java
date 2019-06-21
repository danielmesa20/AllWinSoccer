package com.example.allwinsoccer.Activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Equipo;
import com.example.allwinsoccer.Models.Partido;
import com.example.allwinsoccer.Models.Pronostico;
import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.Objects.AdapterRecyclerPartido;
import com.example.allwinsoccer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class UpdateActivity extends AppCompatActivity implements AdapterRecyclerPartido.OnNoteListener {

    private RecyclerView rv;
    private List<Partido> partidos;
    private AdapterRecyclerPartido adapterRecyclerPartido;
    private ImageView b_local, b_visit;
    private TextView n_local, n_visit;
    private EditText g_local, g_visit;
    private String idPartido, nombreL, nombreV;
    private LinearLayout ly1, ly2, ly3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    goPronostico();
                    return true;
                case R.id.navigation_position:
                    goPosicion();
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
        setContentView(R.layout.activity_update);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        rv = findViewById(R.id.rv_partidos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        b_local = findViewById(R.id.b_local);
        b_visit = findViewById(R.id.b_visit);
        n_local = findViewById(R.id.name_local);
        n_visit = findViewById(R.id.name_visit);
        g_local = findViewById(R.id.goles_local);
        g_visit = findViewById(R.id.goles_visit);
        ly1 = findViewById(R.id.linearLayoutLocal);
        ly2 = findViewById(R.id.linearLayoutVisit);
        ly3 = findViewById(R.id.linearLayoutBotton);

        ly1.setVisibility(View.GONE);
        ly2.setVisibility(View.GONE);
        ly3.setVisibility(View.GONE);

        listarPartidos();
    }

    private void listarPartidos() {

        final ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this);
        progressDialog.setMessage("Cargando los Partidos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        partidos = new ArrayList<>();
        adapterRecyclerPartido = new AdapterRecyclerPartido(partidos, this);
        rv.setAdapter(adapterRecyclerPartido);

        db.collection("Partidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                partidos.clear();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        final Partido partido = d.toObject(Partido.class);
                        partidos.add(partido);
                    }
                    Collections.sort(partidos, new Comparator<Partido>() {
                        @Override
                        public int compare(Partido o1, Partido o2) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
                            Date f1 = null, f2 = null;
                            try {
                                f1 = dateFormat.parse(o1.getFecha());
                                f2 = dateFormat.parse(o2.getFecha());
                            } catch (ParseException ex) {
                                Toast.makeText(UpdateActivity.this, "Error parse", Toast.LENGTH_SHORT).show();
                            }

                            assert f1 != null;
                            return f1.compareTo(f2);
                        }
                    });
                    adapterRecyclerPartido.notifyDataSetChanged();
                }

                progressDialog.dismiss();
            }
        });

    }

    private void actualizar(String name_local, String name_visit, String idP) {
        n_local.setText(name_local);
        n_visit.setText(name_visit);
        nombreL = name_local;
        nombreV = name_visit;
        idPartido = idP;
        ly1.setVisibility(View.VISIBLE);
        ly2.setVisibility(View.VISIBLE);
        ly3.setVisibility(View.VISIBLE);
    }

    private void reiniciar() {
        n_local.setText(R.string.equipoL);
        n_visit.setText(R.string.equipoV);
        idPartido = null;
        nombreV = null;
        nombreL = null;
        g_local.setText("");
        g_visit.setText("");
        ly1.setVisibility(View.GONE);
        ly2.setVisibility(View.GONE);
        ly3.setVisibility(View.GONE);
    }

    public void actualizarResultadoPartido(View view) {
        final String glocalS = g_local.getText().toString(), gvisitS = g_visit.getText().toString();
        if (idPartido == null) {
            Toast.makeText(this, "Debe elegir un partido", Toast.LENGTH_SHORT).show();
        } else if (glocalS.isEmpty() || gvisitS.isEmpty()) {
            Toast.makeText(UpdateActivity.this, "Debe rellenar los campos", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(glocalS) > 99 || Integer.parseInt(gvisitS) > 99) {
            Toast.makeText(this, "Debe ingresar un número entre [0-99] ", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro que es el resultado que quiere ingresar? (no podrá cambiarlo)");
            builder.setTitle("Advertencia");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //db.collection("Partidos").document(idPartido).update("glocal", Integer.parseInt(glocalS),"gvisit", Integer.parseInt(gvisitS));
                    actualizarTablas(nombreL, Integer.parseInt(glocalS), Integer.parseInt(gvisitS));
                    actualizarTablas(nombreV, Integer.parseInt(gvisitS), Integer.parseInt(glocalS));
                    //actualizarPuntosPronostico(idPartido, glocalS, gvisitS);
                    reiniciar();
                    Toast.makeText(UpdateActivity.this, "Pronóstico registrado Correctamente", Toast.LENGTH_SHORT).show();

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

    public void actualizarPuntosPronostico(final String id, final String golesl, final String golesv) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pronosticos").whereEqualTo("idPartido", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Pronostico p = document.toObject(Pronostico.class);
                        int puntos = 0;

                        if (((Integer.parseInt(golesl) > Integer.parseInt(golesv)) && (p.getGlocal() > p.getGvisit())) || ((Integer.parseInt(golesl) < Integer.parseInt(golesv)) && (p.getGlocal() < p.getGvisit())) || ((Integer.parseInt(golesl) == Integer.parseInt(golesv)) && (p.getGlocal() == p.getGvisit())))
                            puntos = 5;

                        if (Integer.parseInt(golesl) == p.getGlocal())
                            puntos = puntos + 10;

                        if (Integer.parseInt(golesv) == p.getGvisit())
                            puntos = puntos + 10;

                        db.collection("Pronosticos").document(p.getIdPronostico()).update("puntos", puntos);
                        if (puntos > 0)
                            actualizarPuntajeUsuario(p.getIdUsuario(), puntos);
                    }
                } else {
                    Toast.makeText(UpdateActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void actualizarPuntajeUsuario(String idUser, final int puntos) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").whereEqualTo("idUsuario", idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Usuario u = document.toObject(Usuario.class);
                        db.collection("Usuarios").document(u.getIdUsuario()).update("puntosUser", u.getPuntosUser() + puntos);
                    }
                } else {
                    Toast.makeText(UpdateActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarTablas(final String nombre, final int gl, final int gv) {
        db.collection("Equipos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        Equipo e = d.toObject(Equipo.class);
                        if(e.getNombre().equals(nombre)){
                            Toast.makeText(UpdateActivity.this, "n "+nombre, Toast.LENGTH_SHORT).show();
                            if(gl > gv){
                                db.collection("Equipos").document(e.getIdEquipo()).update("pG",e.getpG() + 1,
                                        "gF", e.getgF() + gl, "gC", e.getgC() + gv, "puntos",e.getPuntos() + 3 );
                            }else if(gl == gv){
                                db.collection("Equipos").document(e.getIdEquipo()).update("pE",e.getpE() + 1,
                                        "gF", e.getgF() + gl, "gC", e.getgC() + gv, "puntos",e.getPuntos() + 1 );
                            }else{
                                db.collection("Equipos").document(e.getIdEquipo()).update("pP",e.getpP() + 1,
                                        "gF", e.getgF() + gl, "gC", e.getgC() + gv);
                            }
                        }
                    }
                }
            }
        });

    }

    private void goPrincipalActivity() {
        Intent i = new Intent(UpdateActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPronostico() {
        Intent i = new Intent(UpdateActivity.this, PronosticoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPosicion() {
        Intent i = new Intent(UpdateActivity.this, PosicionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goApostarActivity() {
        Intent i = new Intent(UpdateActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goGrupos() {
        Intent i = new Intent(UpdateActivity.this, GruposActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
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

    @Override
    public void onNoteClick(int position) {
        Partido p = partidos.get(position);
        actualizar(p.getNlocal(), p.getNvisit(), p.getIdPartido());
        actualizarIMG(p.getNlocal(), b_local);
        actualizarIMG(p.getNvisit(), b_visit);
    }

}
