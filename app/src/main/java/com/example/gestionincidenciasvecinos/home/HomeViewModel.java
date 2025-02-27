package com.example.gestionincidenciasvecinos.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.example.gestionincidenciasvecinos.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");;
    private DatabaseReference refIncidencias = FirebaseDatabase.getInstance().getReference("incidencias");

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    // Obtener un usuario específico
    private MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public void getUsuario(String id) {

        refUsuarios.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
//                    callback.onUsuarioObtenido(usuario);
                    usuarioLiveData.postValue(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PERFIL", "Error al cargar el usuario");
            }
        });

    }

    // Obtener incidencias
    private MutableLiveData<List<Incidencia>> incidenciasLiveData = new MutableLiveData<>();

    public LiveData<List<Incidencia>> getIncidenciasLiveData() {
        return incidenciasLiveData;
    }

        // Obtener incidencias por usuario
    public void getIncidenciasPorUsuario(String usuario) {

        refIncidencias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    List<Incidencia> incidencias = new ArrayList<>();
                    for (DataSnapshot incidenciaSnapshot : snapshot.getChildren()) {
                        Incidencia incidencia = incidenciaSnapshot.getValue(Incidencia.class);
                        if (incidencia.getCreador().equals(usuario)) {
                            incidencias.add(incidencia);
                        }
                    }
                    incidenciasLiveData.postValue(incidencias);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

        // Obtener todas las incidencias
    public void getTodasIncidencias() {

        List<Incidencia> incidencias = new ArrayList<>();

        refIncidencias.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot incidencia : snapshot.getChildren()) {
                    incidencias.add(incidencia.getValue(Incidencia.class));
                }
                incidenciasLiveData.postValue(incidencias);
            }
        });

    }

    // Obtener todos los usuarios
    private MutableLiveData<List<Usuario>> usuariosLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Usuario>> getUsuariosLiveData() {
        return usuariosLiveData;
    }

    public void getTodosUsuarios() {

        List<Usuario> usuarios = new ArrayList<>();

        refUsuarios.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot usuario : snapshot.getChildren()) {
                    Usuario usu = usuario.getValue(Usuario.class);
                    usuarios.add(usu);
                }

                usuariosLiveData.postValue(usuarios);
            }
        });

    }
}