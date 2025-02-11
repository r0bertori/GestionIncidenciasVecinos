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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroActivity extends AppCompatActivity {

    EditText etCorreoRegistro;
    EditText etPwdRegistro;
    EditText etRepetirPwdRegistro;
    TextView tvIniciarSesionRegistro;
    TextView tvMensajeErrorRegistro;
    Button btnRegistro;
    FirebaseAuth mAuth;
    DatabaseReference refUsuarios;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Componentes
        etCorreoRegistro = findViewById(R.id.etCorreoRegistro);
        etPwdRegistro = findViewById(R.id.etPwdRegistro);
        etRepetirPwdRegistro = findViewById(R.id.etRepetirPwdRegistro);
        tvIniciarSesionRegistro = findViewById(R.id.tvIniciarSesionRegistro);
        tvMensajeErrorRegistro = findViewById(R.id.tvMensajeErrorRegistro);
        btnRegistro = findViewById(R.id.btnRegistro);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Leer de la BBDD
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvMensajeErrorRegistro.setVisibility(View.GONE);

                String correo = etCorreoRegistro.getText().toString();
                String pwd = etPwdRegistro.getText().toString();
                String repetirPwd = etRepetirPwdRegistro.getText().toString();

                if (!correo.isEmpty() && !pwd.isEmpty() && !repetirPwd.isEmpty()) {
                    if (contraseniasCorrectas(pwd, repetirPwd)) {
                        if (!correoYaRegistrado(correo)) {
                            registrarUsuario(correo, pwd);
                        } else {
                            tvMensajeErrorRegistro.setText("El correo ya está registrado");
                            tvMensajeErrorRegistro.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    tvMensajeErrorRegistro.setText("Debes rellenar todos los campos");
                    tvMensajeErrorRegistro.setVisibility(View.VISIBLE);
                }

            }
        });

        tvIniciarSesionRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private boolean correoYaRegistrado(String correo) {

        final boolean[] correoYaRegistrado = {false};

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot usuario : snapshot.getChildren()) {
                        if (usuario.getKey().equals(correo)) {
                            correoYaRegistrado[0] = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        refUsuarios.addValueEventListener(listener);

        return correoYaRegistrado[0];

    }

    private void registrarUsuario(String correo, String pwd) {
        mAuth.createUserWithEmailAndPassword(correo, pwd)
                .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registroEnDataBase(correo);
                            Log.d("FirebaseAuth", "Registro hecho");
                        } else if (pwd.length() >= 6){
                            Toast.makeText(RegistroActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("FirebaseAuth", task.getException().toString());
                        }
                    }
                });
    }

    private void registroEnDataBase(String correo) {

        String id = correo
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");

        Usuario u = new Usuario(id, correo, false);

        refUsuarios.child(id).setValue(u).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               guardarUsuario(id);
               Toast.makeText(this, "Usuario registardo con éxito", Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
               Log.d("FirebaseAuth", "Error al guardar usuario en BBDD");
           }
        });

    }

    private boolean contraseniasCorrectas(String pwd, String repetirPwd) {
        boolean validas = true;

        // Comprobar que sean iguales
        if (!pwd.equals(repetirPwd)) {
            tvMensajeErrorRegistro.setText("Las contraseñas no coinciden");
            tvMensajeErrorRegistro.setVisibility(View.VISIBLE);
        }

        // Comprobar que tenga 6 o más caracteres
        else if (pwd.length() < 6) {
            tvMensajeErrorRegistro.setText("La contraseña debe contener 6 o más caracteres");
            tvMensajeErrorRegistro.setVisibility(View.VISIBLE);
        }

        return validas;
    }

    private void guardarUsuario(String id) {

        SharedPreferences sharedPreferences = getSharedPreferences("MiAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", id);
        editor.apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

    }
}