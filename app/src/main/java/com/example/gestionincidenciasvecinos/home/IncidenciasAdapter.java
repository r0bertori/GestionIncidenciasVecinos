package com.example.gestionincidenciasvecinos.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gestionincidenciasvecinos.home.TarjetaIncidenciaFragment;
import com.example.gestionincidenciasvecinos.models.Incidencia;
import java.util.List;

public class IncidenciasAdapter extends FragmentStateAdapter {

    private List<Incidencia> incidencias;
    private boolean userEsAdmin;

    public IncidenciasAdapter(@NonNull FragmentActivity fragmentActivity, List<Incidencia> incidencias, boolean userEsAdmin) {
        super(fragmentActivity);
        this.incidencias = incidencias;
        this.userEsAdmin = userEsAdmin;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Incidencia incidencia = incidencias.get(position);
        return TarjetaIncidenciaFragment.newInstance(
                incidencia.getTitulo(),
                incidencia.getDescripcion(),
                incidencia.getCreador()
        );
    }

    @Override
    public int getItemCount() {
        return incidencias.size();
    }

    public void actualizarIncidencias(List<Incidencia> nuevasIncidencias) {
        incidencias.clear();
        incidencias.addAll(nuevasIncidencias);
        notifyDataSetChanged();
    }
}
