package com.example.gestionincidenciasvecinos.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gestionincidenciasvecinos.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionincidenciasvecinos.databinding.FragmentHomeBinding;
import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private HomeViewModel viewModel;
    private boolean userEsAdmin = false;
    private String id, usuarioNomApe;
    private IncidenciasAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        limpiarFragmentos();
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        id = mAuth.getCurrentUser().getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");


        // Obtener las incidencias
        // No se ejecuta hasta que obtengamos al usuario actual (y si es admin o no)
        viewModel.getIncidenciasLiveData().observe(getViewLifecycleOwner(), incidencias -> {
            limpiarFragmentos();
            crearFragments(incidencias);
        });

        // Obtener al usuario actual (y si es admin)
        viewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {
            userEsAdmin = usuario.getEsAdmin();
            usuarioNomApe = usuario.getNombreApellidos();

            if (!userEsAdmin) {
                binding.tvTituloHome.setText("Tus incidencias");
                viewModel.getIncidenciasPorUsuario(usuarioNomApe);
                binding.tvTituloUsuariosHome.setVisibility(View.GONE);
                binding.svUsuarios.setVisibility(View.GONE);
            } else {
                binding.tvTituloHome.setText("Todas las incidencias");
                viewModel.getTodasIncidencias();
                binding.tvTituloUsuariosHome.setVisibility(View.VISIBLE);
                binding.svUsuarios.setVisibility(View.VISIBLE);

                viewModel.getUsuariosLiveData().observe(getViewLifecycleOwner(), usuarios -> {
                    String users = "";
                    for (int i=0; i<usuarios.size(); i++) {
                        users += usuarios.get(i).getNombreApellidos() + " (";
                        if (usuarios.get(i).getEsAdmin()) {
                            users += "admin";
                        } else {
                            users += "vecino";
                        }
                        users += ")\n";
                    }
                    binding.tvUsuariosHome.setText(users);
                });

                viewModel.getTodosUsuarios();
            }

        });

        viewModel.getUsuario(id);

    }

    private void crearFragments(List<Incidencia> incidencias) {

        // PARA USUARIOS NORMALES
        if (!userEsAdmin) {
            for (Incidencia incidencia : incidencias) {
                TarjetaIncidenciaFragment tarjeta = new TarjetaIncidenciaFragment().newInstance(
                        incidencia.getTitulo(),
                        incidencia.getDescripcion(),
                        usuarioNomApe
                );

                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.llContenedorFragments, tarjeta)
                        .commitNow();
            }
        } else {

            for (Incidencia incidencia : incidencias) {

                TarjetaIncidenciaFragment tarjeta = new TarjetaIncidenciaFragment().newInstance(
                        incidencia.getTitulo(),
                        incidencia.getDescripcion(),
                        incidencia.getCreador()
                );

                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.llContenedorFragments, tarjeta)
                        .commitNow();

                tarjeta.enableTvCreador();

            }
        }

    }

    private void limpiarFragmentos() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            getChildFragmentManager().beginTransaction().remove(fragment).commitNow();
        }
        binding.llContenedorFragments.removeAllViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}