package com.example.allwinsoccer.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrincipalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private String idUsuario;
    private ConstraintLayout cl;
    private ImageView bota, guante;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    goApostarActivity();
                    return true;
                case R.id.navigation_notifications:
                    goPronosticoActivity();
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
        setContentView(R.layout.activity_principal);
        cl = findViewById(R.id.container);
        cl.setVisibility(View.INVISIBLE);
        bota = findViewById(R.id.jugador_torneo);
        guante = findViewById(R.id.portero_torneo);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private AlertDialog mostrarMensajeFinal() {
        final AlertDialog alertDialogReglas;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ViewGroup nullParent = null;
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.mensajefinal, nullParent);
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

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //Hora igual del telefono
    private String fActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    //Manejo de resultado de la peticion a Google
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
                try {
                    Date fechaActual = dateFormat.parse(fActual()), fechaTope = dateFormat.parse("15:00 13/07");
                    float diferencia = (float) ((fechaTope.getTime() - fechaActual.getTime()) / 60000);
                    if (diferencia > 0) {
                        verificarJugadores(account.getId());
                        idUsuario = account.getId();
                    } else {
                        cl.setVisibility(View.VISIBLE);
                        bota.setVisibility(View.GONE);
                        guante.setVisibility(View.GONE);
                        idUsuario = account.getId();
                        if (!getIntent().hasExtra("idUser"))
                            mostrarMensajeFinal().show();
                    }
                } catch (java.text.ParseException e) {
                    Toast.makeText(this, "Error fecha", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error handleSignInResult", Toast.LENGTH_SHORT).show();
            }
        } else
            goLogin();
    }

    // Verificar si el usuario ya ingreso sus pronosticos al jugador y portero del torneo
    private void verificarJugadores(final String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Usuarios").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Usuario u = document.toObject(Usuario.class);
                        assert u != null;
                        if (u.getIdMejorJugador() != null) { // Si el usuario ya ingreso el Jugador del torneo se quita el boton asociado
                            bota.setVisibility(View.GONE);
                        }
                        if (u.getIdMejorPortero() != null) { // Si el usuario ya ingreso el portero del torneo se quita el boton asociado
                            guante.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Toast.makeText(PrincipalActivity.this, "Sin internet", Toast.LENGTH_SHORT).show();
                }
                cl.setVisibility(View.VISIBLE);
                if (!getIntent().hasExtra("idUser"))
                    mostrarMensajeFinal().show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // Cerra sesión en Google
    public void logOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(PrincipalActivity.this, "Vuelve Pronto", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(PrincipalActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    Toast.makeText(PrincipalActivity.this, "Error al salir", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Metodos para ir a las otras activities
    private void goApostarActivity() {
        Intent i = new Intent(PrincipalActivity.this, ApostarActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    private void goPronosticoActivity() {
        Intent i = new Intent(PrincipalActivity.this, PronosticoActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    private void goPosicion() {
        Intent i = new Intent(PrincipalActivity.this, PosicionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    public void goUpdate(View view) {
        Intent i = new Intent(PrincipalActivity.this, UpdateActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    public void goJugadores(View view) {
        Intent i = new Intent(PrincipalActivity.this, JugadorActivity.class);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    public void goPorteros(View view) {
        Intent i = new Intent(PrincipalActivity.this, PorteroActivity.class);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }

    private void goLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void goGrupos() {
        Intent i = new Intent(PrincipalActivity.this, GruposActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("idUser", idUsuario);
        startActivity(i);
    }
}
