package com.example.gestionincidenciasvecinos.home;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class DetallesIncidenciaViewModel extends ViewModel {

    private DatabaseReference refIncidencias = FirebaseDatabase.getInstance().getReference("incidencias");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("incidencias_images");;
    public MutableLiveData<Boolean> eliminarLiveData = new MutableLiveData<Boolean>();

    public MutableLiveData<Boolean> getEliminarLiveData() {
        return eliminarLiveData;
    }

    public void eliminarIncidencia(String key) {

        refIncidencias.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        eliminarLiveData.postValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        eliminarLiveData.postValue(false);
                    }
                });

    }

    public void actualizarIncidencia(Incidencia incidencia, String keyAntigua) {

        Uri imageUri = incidencia.getImage();
        String keyNueva = (incidencia.getTitulo() + incidencia.getCreador()).replace(" ", "");

        // Borramos la anterior incidencia y creamos otra (la modificada) para evitar errores con la key
        refIncidencias.child(keyAntigua).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("DETALLES_INCIDENCIA", "Incidencia antigua removida");
                if (imageUri != null) {
                    Log.d("DETALLES_INCIDENCIA", "Existe una nueva imagen");
                    StorageReference imageAntiguaRef = storageReference.child(keyAntigua + ".jpg"); // Referencia de la imagen antigua en Storage
                    StorageReference imageNuevaRef = storageReference.child(keyNueva + ".jpg"); // Referencia de la imagen nueva en Storage

                    // Borrar la imagen antigua
                    imageAntiguaRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("DETALLES_INCIDENCIA", "Imagen antigua removida");
                            // Guardar la imagen nueva
                            imageNuevaRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("DETALLES_INCIDENCIA", "Imagen nueva guardada");
                                    imageNuevaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("DETALLES_INCIDENCIA", "URL nueva guardada");

                                            Incidencia nuevaIncidencia = new Incidencia(
                                                    incidencia.getTitulo(),
                                                    incidencia.getDescripcion(),
                                                    incidencia.getCreador(),
                                                    uri.toString()
                                            );

                                            refIncidencias.child(keyNueva).setValue(nuevaIncidencia)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d("DETALLES_INCIDENCIA", "Incidencia actualizada con éxito");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("DETALLES_INCIDENCIA", "Error al actualizar la incidencia");
                                                        }
                                                    });

                                        }

                                    });

                                }

                            });
                        }
                    });

                }

                else {

                    Incidencia nuevaIncidencia = new Incidencia(
                            incidencia.getTitulo(),
                            incidencia.getDescripcion(),
                            incidencia.getCreador(),
                            incidencia.getImageURL()
                    );

                    refIncidencias.child(keyNueva).setValue(nuevaIncidencia)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("DETALLES_INCIDENCIA", "Incidencia actualizada con éxito");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DETALLES_INCIDENCIA", "Error al actualizar la incidencia");
                                }
                            });
                }

            }

        });

    }

}