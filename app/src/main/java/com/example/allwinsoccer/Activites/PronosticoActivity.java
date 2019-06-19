package com.example.allwinsoccer.Activites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Partido;
import com.example.allwinsoccer.Models.Pronostico;
import com.example.allwinsoccer.Objects.AdapterRecyclerPronostico;
import com.example.allwinsoccer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PronosticoActivity extends AppCompatActivity implements AdapterRecyclerPronostico.OnNoteListener {

    private RecyclerView rv;
    private List<Pronostico> pronosticos;
    private AdapterRecyclerPronostico adapterRecyclerPronostico;
    private TextView tv_puntos;
    ProgressDialog   progressDialog, progressDialog2;

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
        setContentView(R.layout.activity_pronostico);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        rv = findViewById(R.id.rv_partidos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        tv_puntos = findViewById(R.id.puntos);
        tv_puntos.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(PronosticoActivity.this);
        progressDialog2 = new ProgressDialog(PronosticoActivity.this);
        listarPronosticos();
    }

    private void listarPronosticos() {

        progressDialog2.setMessage("Cargando Pronósticos");
        progressDialog2.setCancelable(false);
        progressDialog2.show();

        pronosticos = new ArrayList<>();
        adapterRecyclerPronostico = new AdapterRecyclerPronostico(pronosticos, this);
        rv.setAdapter(adapterRecyclerPronostico);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pronosticos").whereEqualTo("idUsuario",getIntent().getStringExtra("idUser")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                pronosticos.clear();
                if (task.isSuccessful()) {
                    int puntos = 0;
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Pronostico p = document.toObject(Pronostico.class);
                        pronosticos.add(p);
                        if(p.getPuntos() != -1){
                            puntos = puntos + p.getPuntos();
                        }
                    }
                    if(puntos > 0)
                        tv_puntos.setText(getString(R.string.puntaje,puntos));
                    else
                        tv_puntos.setText(R.string.noPuntaje);

                    tv_puntos.setVisibility(View.VISIBLE);
                    adapterRecyclerPronostico.notifyDataSetChanged();
                } else
                    Toast.makeText(PronosticoActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();

                progressDialog2.dismiss();
            }
        });

    }

    private void buscarPartido(String idPartido){
        progressDialog.setMessage("Cargando detalles del partido");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Partidos").whereEqualTo("idPartido", idPartido).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Partido p = document.toObject(Partido.class);
                        detallesPartido(p).show();
                    }
                } else
                    Toast.makeText(PronosticoActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AlertDialog detallesPartido(Partido p){
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ViewGroup nullParent = null;
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.alertdialogdetails, nullParent);
        TextView nl = v.findViewById(R.id.nl), nv = v.findViewById(R.id.nv), gl = v.findViewById(R.id.gl), gv = v.findViewById(R.id.gv);
        TextView fp = v.findViewById(R.id.fp), fasep = v.findViewById(R.id.fasep), estado = v.findViewById(R.id.estado), ubi = v.findViewById(R.id.ubicacion);
        ImageView bl = v.findViewById(R.id.bl), bv = v.findViewById(R.id.bv);
        actualizarIMG(p.getNlocal(),bl);
        actualizarIMG(p.getNvisit(),bv);
        nl.setText(p.getNlocal());
        nv.setText(p.getNvisit());
        ubi.setText(getString(R.string.ubicacionPartido, p.getUbicacion()));
        fasep.setText(p.getFase());
        fp.setText(getString(R.string.fechaPartido, p.getFecha()));
        if(p.getGvisit() == -1){
            estado.setText(R.string.partidoNoFinalizado);
            gl.setText(R.string.NA);
            gv.setText(R.string.NA);
        }else{
            estado.setText(R.string.partidoFinalizado);
            gl.setText(String.valueOf(p.getGlocal()));
            gv.setText(String.valueOf(p.getGvisit()));
        }
        Button bsalir = v.findViewById(R.id.bsalir);
        builder.setView(v);
        alertDialog = builder.create();
        bsalir.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

        progressDialog.dismiss();
        return alertDialog;
    }

    private void actualizarIMG(String name, ImageView iv) {
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

    private void goPrincipalActivity() {
        Intent i = new Intent(PronosticoActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void goApostar() {
        Intent i = new Intent(PronosticoActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPosicion() {
        Intent i = new Intent(PronosticoActivity.this, PosicionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser",getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    @Override
    public void onNoteClick(int position) {
        Pronostico p = pronosticos.get(position);
        buscarPartido(p.getIdPartido());
    }

    private void goGrupos() {
        Intent i = new Intent(PronosticoActivity.this, GruposActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

}
