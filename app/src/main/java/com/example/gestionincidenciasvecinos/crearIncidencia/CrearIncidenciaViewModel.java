package com.example.gestionincidenciasvecinos.crearIncidencia;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CrearIncidenciaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private DatabaseReference refUsuarios, refIncidencias;
    private StorageReference storageReference;
    private MutableLiveData<String> usuarioLiveData = new MutableLiveData<>();

    public CrearIncidenciaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getUsuarioLiveData () {
        return usuarioLiveData;
    }

    public void getUserFullName(String id) {

        refUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");

        refUsuarios.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("nombreApellidos").getValue(String.class);
//                    callback.onUsuarioObtenido(usuario);
                    usuarioLiveData.postValue(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PERFIL", "Error al cargar el usuario");
            }
        });

    }

    private MutableLiveData<Boolean> crearIncidenciaLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getCrearIncidenciaLiveData() {
        return crearIncidenciaLiveData;
    }

    public void crearIncidencia(Incidencia incidencia, Context context) {

        refIncidencias = FirebaseDatabase.getInstance().getReference("incidencias");
        storageReference = FirebaseStorage.getInstance().getReference("incidencias_images");

        String id = (incidencia.getTitulo() + incidencia.getCreador()).replace(" ", "");
        Uri imageUri = incidencia.getImage();

        StorageReference imageRef = storageReference.child(id + ".jpg"); // Referencia de la imagen en Storage
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        // Creo otra incidencia con URL de la imagen en lugar de URI
                        Incidencia inc = new Incidencia (
                                incidencia.getTitulo(),
                                incidencia.getDescripcion(),
                                incidencia.getCreador(),
                                uri.toString()
                                );

                        // TODO Idea : subir a storage la imagen al seleccionarla, y si seleccionas otra, borra la anterior

                        refIncidencias.child(id).setValue(inc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    crearIncidenciaLiveData.postValue(true);
                                } else {
                                    crearIncidenciaLiveData.postValue(false);
                                }
                            }
                        });

                    }
                });
            }
        });

    }

}