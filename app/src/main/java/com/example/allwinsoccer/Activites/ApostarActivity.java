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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private BottomNavigationView navView;
    private LinearLayout ly1, ly2, ly3;

    //Barra de navegación
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
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setVisibility(View.INVISIBLE);
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
        ly3 = findViewById(R.id.linearLayout);
        ly1.setVisibility(View.GONE);
        ly2.setVisibility(View.GONE);
        ly3.setVisibility(View.GONE);
        mostrarPartidos();
    }

    //Muestra una lista de los partidos en los que puede apostar el usuario
    private void mostrarPartidos() {

        final ProgressDialog progressDialog = new ProgressDialog(ApostarActivity.this);
        progressDialog.setMessage("Cargando los Partidos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        partidos = new ArrayList<>();
        adapterRecyclerPartido = new AdapterRecyclerPartido(partidos, this);
        rv.setAdapter(adapterRecyclerPartido);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Partidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //Se traen todos los partidos de la base de datos
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d : Objects.requireNonNull(task.getResult())) {
                        final Partido partido = d.toObject(Partido.class);
                        partidos.clear();
                        if (verificarFechaPartido(Objects.requireNonNull(partido).getFecha())) {
                            //Se buscan los pronosticos del usuario
                            db.collection("Pronosticos").whereEqualTo("idUsuario", getIntent().getStringExtra("idUser")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    int aux = 0;
                                    boolean apostado = false;
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        List<DocumentSnapshot> partidosApostados = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d : partidosApostados) {
                                            Pronostico pronostico = d.toObject(Pronostico.class);
                                            //Se compara con los pronosticos del usuario, que este solo pueda apostar una vez por paratido
                                            if (partido.getIdPartido().equals(Objects.requireNonNull(pronostico).getIdPartido()))
                                                apostado = true;
                                        }
                                    }
                                    if (queryDocumentSnapshots.isEmpty() || !apostado) {
                                        partidos.add(partido);
                                        aux=1;
                                        Collections.sort(partidos, new Comparator<Partido>() {
                                            @Override
                                            public int compare(Partido o1, Partido o2) {  //Se ordenar los partidos por fecha
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
                                                Date f1 = null, f2 = null;
                                                try {
                                                    f1 = dateFormat.parse(o1.getFecha());
                                                    f2 = dateFormat.parse(o2.getFecha());
                                                } catch (ParseException ex) {
                                                    Toast.makeText(ApostarActivity.this, "Error parse", Toast.LENGTH_SHORT).show();
                                                }
                                                assert f1 != null;
                                                return f1.compareTo(f2);
                                            }
                                        });
                                        adapterRecyclerPartido.notifyDataSetChanged();
                                    }
                                    if(aux==0){
                                        Toast.makeText(ApostarActivity.this, "No hay partidos para apostar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }
                navView.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }

    // Actualizar las imagenes y nombres de las equipos en la vista
    private void actualizar(String name_local, String name_visit, String idP) {
        n_local.setText(name_local);
        n_visit.setText(name_visit);
        g_local.setText("");
        g_visit.setText("");
        idPartido = idP;
        ly1.setVisibility(View.VISIBLE);
        ly2.setVisibility(View.VISIBLE);
        ly3.setVisibility(View.VISIBLE);
    }

    // Devuelve la fecha actual del telefono, para poder compararla con la fecha del partido y saber si el usuario puede apostar en ese partido
    private String fActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    // Se verifica si el partido tiene una fecha valida, es decir que si ya el partido paso
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

    // Se borran las imagenes y nombres de las equipos en la vista
    private void reiniciar() {
        n_local.setText(R.string.equipoL);
        n_visit.setText(R.string.equipoV);
        g_local.setText("");
        g_visit.setText("");
        idPartido = null;
        ly1.setVisibility(View.GONE);
        ly2.setVisibility(View.GONE);
        ly3.setVisibility(View.GONE);
    }

    // Se registra el pronostico ingresado por el usuario
    public void registrarPronostico(View view) {

        final String glocalS = g_local.getText().toString(), gvisitS = g_visit.getText().toString();
        if (idPartido == null) {                                                                        // Verifica que el usuario haya elegido un partido
            Toast.makeText(this, "Debe elegir un partido", Toast.LENGTH_SHORT).show();
        } else if (glocalS.isEmpty() || gvisitS.isEmpty()) {                                            // Verifica que el usuario haya ingresado los goles
            Toast.makeText(ApostarActivity.this, "Debe rellenar los campos", Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(glocalS.trim()) > 99 || Integer.parseInt(gvisitS.trim()) > 99) {    // Verifica que los goles serian entre [0-99]
            Toast.makeText(this, "Debe ingresar un número entre [0-99] ", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro que es el resultado que quiere ingresar? (no podrá cambiarlo)");
            builder.setTitle("Advertencia");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {            // Guarda el pronostico del usuario
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Pronostico p = new Pronostico();
                    p.setIdPronostico(UUID.randomUUID().toString());
                    p.setIdUsuario(getIntent().getStringExtra("idUser"));
                    p.setIdPartido(idPartido);
                    p.setGlocal(Integer.parseInt(glocalS.trim()));
                    p.setGvisit(Integer.parseInt(gvisitS.trim()));
                    p.setPuntos(-1);
                    p.setNlocal(n_local.getText().toString());
                    p.setNvisit(n_visit.getText().toString());
                    p.setFecha(fActual());
                    db.collection("Pronosticos").document(p.getIdPronostico()).set(p);
                    Toast.makeText(ApostarActivity.this, "Pronóstico registrado Correctamente", Toast.LENGTH_SHORT).show();
                    mostrarPartidos();
                    reiniciar();
                }
            });

            builder.setNeutralButton("VER REGLAS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {     // Mostrar reglas al usuario
                    mostrarReglas().show();
                }
            });

            builder.setNegativeButton("VOLVER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {        // Volver a la vista apostar
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // AlerDialog que muestra las reglas al usuario
    private AlertDialog mostrarReglas() {
        final AlertDialog alertDialogReglas;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ViewGroup nullParent = null;
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.alertdialogreglas, nullParent);
        Button bsalir = v.findViewById(R.id.salir_b);
        builder.setView(v);
        alertDialogReglas = builder.create();
        bsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogReglas.dismiss();
            }
        });
        return alertDialogReglas;
    }

    // Actualiza las banderas de los equipos en la vista
    private void actualizarIMG(String name, ImageView iv) {
        iv.setVisibility(View.VISIBLE);                         //Se pone el imageView visible al usuario
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

    // Metodos para ir a otras activities con los botones de la barra de navegación
    private void goPrincipalActivity() {
        Intent i = new Intent(ApostarActivity.this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    @Override
    public void onNoteClick(int position) {                 //Al hacer click en un elemento del RecyclerView
        Partido p = partidos.get(position);
        actualizar(p.getNlocal(), p.getNvisit(), p.getIdPartido());
        actualizarIMG(p.getNlocal(), b_local);
        actualizarIMG(p.getNvisit(), b_visit);
    }

}
