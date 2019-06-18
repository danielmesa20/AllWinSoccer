package com.example.allwinsoccer.Activites;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.allwinsoccer.Models.Usuario;
import com.example.allwinsoccer.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener  {

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        SignInButton signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i, 777);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 777){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            if(account != null){
                buscarAUsuarios(account.getId(),account.getEmail(),String.valueOf(account.getPhotoUrl()));
                goPrincipalActivity();
            }
        }else{
            Toast.makeText(this, "NO se pudo ingresar "+result.getStatus().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void buscarAUsuarios(final String id, final String email, final String url){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Usuarios").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && !document.exists()){
                        agregarUsuario(email,id,url);
                    }else{
                        verificarUrl(id,url);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarUrl(final String id, final String url){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").whereEqualTo("idUsuario", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Usuario u = document.toObject(Usuario.class);
                        if (u.getUrl() == null || u.getUrl().isEmpty()) {
                            db.collection("Usuarios").document(u.getIdUsuario()).update("url", url);
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void agregarUsuario(String email, String idUsuario, String url){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setIdUsuario(idUsuario);
        u.setPuntosUser(0);
        u.setNombre(generarNombre(email));
        u.setUrl(url);
        db.collection("Usuarios").document(idUsuario).set(u);
    }

    public  String generarNombre(String email){
        String name = "";
        boolean parar = false;
        int i=0;
        while (i < email.length() && !parar){
            if(email.charAt(i) == '@'){
                parar = true;
            }

            if(!parar){
                name = name + email.charAt(i);
            }

            i++;
        }
        return name;
    }

    private void goPrincipalActivity() {
        Intent i = new Intent(this, PrincipalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
