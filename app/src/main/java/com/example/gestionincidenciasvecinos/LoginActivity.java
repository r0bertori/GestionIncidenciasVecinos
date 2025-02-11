package com.example.gestionincidenciasvecinos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gestionincidenciasvecinos.ui.home.HomeFragment;
import com.example.gestionincidenciasvecinos.ui.home.HomeViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreoLogin;
    EditText etPwdLogin;
    Button btnIniciarSesion;
    SignInButton btnSignInGoogle;
    TextView tvMensajeErrorLogin;
    TextView tvRegistrarseLogin;
    FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount googleSignInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("FirebaseAuth", "Inicio de sesion COMPLETADO " + mAuth.getCurrentUser().getDisplayName() + " - " + mAuth.getCurrentUser().getDisplayName());
                                String usuario = mAuth.getCurrentUser().getEmail();
                                Log.d("FirebaseAuth", "Usuario: " + usuario);
                                String id = usuario
                                        .replace(".", "")
                                        .replace("#", "")
                                        .replace("$", "")
                                        .replace("[", "")
                                        .replace("]", "");
                                guardarUsuario(id);
                            } else {
                                Log.d("FirebaseAuth", "Inicio de sesion FALLIDO: " + task.getException());
                                Toast.makeText(LoginActivity.this, "ERROR AL INICIAR SESIÓN CON GOOGLE", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    Log.e("FirebaseAuth", "Error en la autenticación con Google", e);
                }
            } else {
                Log.d("FirebaseAuth", "Resultado: " + result.getResultCode());
            }
        }
    });

    private void guardarUsuario(String id) {

        SharedPreferences sharedPreferences = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", id);
        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                String id = usuario
                        .replace(".", "")
                        .replace("#", "")
                        .replace("$", "")
                        .replace("[", "")
                        .replace("]", "");
                Log.d("FirebaseAuth", "ID: " + id);
                guardarUsuario(id);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPwdLogin = findViewById(R.id.etPwdLogin);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        tvMensajeErrorLogin = findViewById(R.id.tvMensajeErrorLogin);
        tvRegistrarseLogin = findViewById(R.id.tvRegistrarseLogin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        FirebaseApp.getInstance("[DEFAULT]");
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        mAuth = FirebaseAuth.getInstance();

        etCorreoLogin.requestFocus();

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String correo = etCorreoLogin.getText().toString();
                String pwd = etPwdLogin.getText().toString();

                if (!correo.isEmpty()) {

                    if (!pwd.isEmpty()) {

                        tvMensajeErrorLogin.setVisibility(View.GONE);

                        mAuth.signInWithEmailAndPassword(correo, pwd)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseAuth", "Login realizado con éxito");
                                            String id = correo
                                                    .replace(".", "")
                                                    .replace("#", "")
                                                    .replace("$", "")
                                                    .replace("[", "")
                                                    .replace("]", "");
                                            guardarUsuario(id);
                                        } else {
                                            tvMensajeErrorLogin.setText("El correo y/o contraseña son incorrectos");
                                            tvMensajeErrorLogin.setVisibility(View.VISIBLE);
                                            Log.d("FirebaseAuth", task.getException().toString());
                                        }
                                    }
                                });

                    } else {
                        tvMensajeErrorLogin.setText("Debe rellenar todos los campos");
                        tvMensajeErrorLogin.setVisibility(View.VISIBLE);
                    }

                } else {
                    tvMensajeErrorLogin.setText("Debe rellenar todos los campos");
                    tvMensajeErrorLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        tvRegistrarseLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

    }

}