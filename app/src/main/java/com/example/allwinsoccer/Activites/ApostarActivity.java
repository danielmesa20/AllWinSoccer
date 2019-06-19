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
import android.widget.TextView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Partido;
import com.example.allwinsoccer.Models.Pronostico;
import com.example.allwinsoccer.Objects.AdapterRecyclerPartido;
import com.example.allwinsoccer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class ApostarActivity extends AppCompatActivity implements AdapterRecyclerPartido.OnNoteListener {

    private RecyclerView rv;
    private List<Partido> partidos;
    private AdapterRecyclerPartido adapterRecyclerPartido;
    private ImageView b_local, b_visit;
    private TextView n_local, n_visit;
    private EditText g_local, g_visit;
    private String idPartido;
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
        setContentView(R.layout.activity_apostar);
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

        b_local.setVisibility(View.INVISIBLE);
        b_visit.setVisibility(View.INVISIBLE);
        n_local.setVisibility(View.INVISIBLE);
        n_visit.setVisibility(View.INVISIBLE);
        g_local.setVisibility(View.INVISIBLE);
        g_visit.setVisibility(View.INVISIBLE);

        listarPartidos();

    }

    private void listarPartidos() {

        final ProgressDialog progressDialog = new ProgressDialog(ApostarActivity.this);
        progressDialog.setMessage("Cargando los Partidos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        partidos = new ArrayList<>();
        adapterRecyclerPartido = new AdapterRecyclerPartido(partidos, this);
        rv.setAdapter(adapterRecyclerPartido);

        db.collection("Partidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot d : task.getResult()) {
                        final Partido partido = d.toObject(Partido.class);
                        partidos.clear();
                        if (verificarFechaPartido(Objects.requireNonNull(partido).getFecha())) {
                            db.collection("Pronosticos").whereEqualTo("idUsuario", getIntent().getStringExtra("idUser")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    boolean apostado = false;
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        List<DocumentSnapshot> partidosApostados = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d : partidosApostados) {
                                            Pronostico pronostico = d.toObject(Pronostico.class);
                                            if (partido.getIdPartido().equals(Objects.requireNonNull(pronostico).getIdPartido()))
                                                apostado = true;
                                        }
                                    }
                                    if (queryDocumentSnapshots.isEmpty() || !apostado) {
                                        partidos.add(partido);
                                        adapterRecyclerPartido.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
                progressDialog.dismiss();
            }
        });

    }

    private void actualizar(String name_local, String name_visit, String idP) {
        n_local.setText(name_local);
        n_visit.setText(name_visit);
        g_local.setText("");
        g_visit.setText("");
        idPartido = idP;
        n_local.setVisibility(View.VISIBLE);
        n_visit.setVisibility(View.VISIBLE);
        g_local.setVisibility(View.VISIBLE);
        g_visit.setVisibility(View.VISIBLE);
    }

    private String fActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private Boolean verificarFechaPartido(String fPartido) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        try {
            Date fechaActual = dateFormat.parse(fActual()), fechaPartido = dateFormat.parse(fPartido);
            float diferencia = (float) ((fechaPartido.getTime() - fechaActual.getTime()) / 60000);
            return diferencia > 30;
        } catch (java.text.ParseException e) {
            Toast.makeText(this, "Error fecha", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void reiniciar() {
        n_local.setText(R.string.equipoL);
        n_visit.setText(R.string.equipoV);
        g_local.setText("");
        g_visit.setText("");
        b_local.setVisibility(View.INVISIBLE);
        b_visit.setVisibility(View.INVISIBLE);
        n_visit.setVisibility(View.INVISIBLE);
        n_local.setVisibility(View.INVISIBLE);
        g_visit.setVisibility(View.INVISIBLE);
        g_local.setVisibility(View.INVISIBLE);
        idPartido = null;
    }

    public void registrarPronostico(View view) {

        final String glocalS = g_local.getText().toString(), gvisitS = g_visit.getText().toString();
        if (idPartido == null || getIntent().getStringExtra("idUser") == null) {
            Toast.makeText(this, "Debe elegir un partido", Toast.LENGTH_SHORT).show();
        } else if (glocalS.isEmpty() || gvisitS.isEmpty()) {
            Toast.makeText(ApostarActivity.this, "Debe rellenar los campos", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(glocalS) > 99 || Integer.parseInt(gvisitS) > 99) {
            Toast.makeText(this, "Debe ingresar un número entre [0-99] ", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro que es el resultado que quiere ingresar? (no podrá cambiarlo)");
            builder.setTitle("Advertencia");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Pronostico p = new Pronostico();
                    p.setIdPronostico(UUID.randomUUID().toString());
                    p.setIdUsuario(getIntent().getStringExtra("idUser"));
                    p.setIdPartido(idPartido);
                    p.setGlocal(Integer.parseInt(glocalS));
                    p.setGvisit(Integer.parseInt(gvisitS));
                    p.setPuntos(-1); //quitar
                    p.setNlocal(n_local.getText().toString());
                    p.setNvisit(n_visit.getText().toString());
                    p.setFecha(fActual());
                    db.collection("Pronosticos").document(p.getIdPronostico()).set(p);
                    Toast.makeText(ApostarActivity.this, "Pronóstico registrado Correctamente", Toast.LENGTH_SHORT).show();
                    listarPartidos();
                    reiniciar();
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

    private void goPrincipalActivity() {
        Intent i = new Intent(ApostarActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPronostico() {
        Intent i = new Intent(ApostarActivity.this, PronosticoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goPosicion() {
        Intent i = new Intent(ApostarActivity.this, PosicionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", getIntent().getStringExtra("idUser"));
        startActivity(i);
    }

    private void goGrupos() {
        Intent i = new Intent(ApostarActivity.this, GruposActivity.class);
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
