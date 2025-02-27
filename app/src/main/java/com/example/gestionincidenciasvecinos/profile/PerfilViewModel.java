package com.example.gestionincidenciasvecinos.profile;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.example.gestionincidenciasvecinos.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private DatabaseReference refUsuarios, refIncidencias;

    public PerfilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    private MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();

    public LiveData<Usuario> getUsuarioLiveData () {
        return usuarioLiveData;
    }

    public void getUser(String id) {
        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

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

    public void saveUserData(String id, Usuario usuario, String nombreAntiguo, Context context) {

        changeIncidenceData(usuario.getNombreApellidos(), nombreAntiguo);

        refUsuarios.child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeIncidenceData(String nombreNuevo, String nombreAntiguo) {

        refIncidencias = FirebaseDatabase.getInstance().getReference("incidencias");

        refIncidencias.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot incidencia : snapshot.getChildren()) {
                    Incidencia inc = incidencia.getValue(Incidencia.class);
                    if (inc.getCreador().equals(nombreAntiguo)) {

                        // Borramos la anterior incidencia, y creamos una nueva con los valores (key y creador) actualizados
                        refIncidencias.child(incidencia.getKey().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Incidencia nuevaIncidencia = new Incidencia(
                                        //String titulo, String descripción, String creador, String imageURL
                                        inc.getTitulo(),
                                        inc.getDescripcion(),
                                        nombreNuevo,
                                        inc.getImageURL()
                                );

                                String key = (nuevaIncidencia.getTitulo() + nuevaIncidencia.getCreador())
                                        .replace(" ", "");

                                refIncidencias.child(key).setValue(nuevaIncidencia);

                            }
                        });
                    }
                }
            }
        });

    }

    public interface UsuarioCallback {
        void onUsuarioObtenido(Usuario usuario);
    }


}