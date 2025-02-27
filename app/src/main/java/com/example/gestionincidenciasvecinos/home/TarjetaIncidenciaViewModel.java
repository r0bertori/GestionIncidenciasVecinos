package com.example.gestionincidenciasvecinos.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TarjetaIncidenciaViewModel extends ViewModel {

    private DatabaseReference refIncidencias = FirebaseDatabase.getInstance().getReference("incidencias");

    private MutableLiveData<Incidencia> incidenciaMutableLiveData = new MutableLiveData<>();

    public LiveData<Incidencia> getIncidenciaLiveData() {
        return incidenciaMutableLiveData;
    }

    public void getIncidencia(String titulo, String creador) {
        refIncidencias.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for (DataSnapshot incidencia : snapshot.getChildren()) {
                    if (incidencia.getValue(Incidencia.class).getTitulo().equals(titulo) &&
                        incidencia.getValue(Incidencia.class).getCreador().equals(creador)) {

                        incidenciaMutableLiveData.postValue(incidencia.getValue(Incidencia.class));

                    }
                }
            }
        });
    }
}