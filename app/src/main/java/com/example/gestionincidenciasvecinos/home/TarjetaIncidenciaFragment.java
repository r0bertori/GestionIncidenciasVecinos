package com.example.gestionincidenciasvecinos.home;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gestionincidenciasvecinos.R;
import com.example.gestionincidenciasvecinos.databinding.FragmentTarjetaIncidenciaBinding;
import com.example.gestionincidenciasvecinos.models.Incidencia;

public class TarjetaIncidenciaFragment extends Fragment {

    private TarjetaIncidenciaViewModel mViewModel;
    private String titulo, descripcion, creador;
    private FragmentTarjetaIncidenciaBinding binding;
    private TarjetaIncidenciaViewModel viewModel;
    private Incidencia inc;

    public static TarjetaIncidenciaFragment newInstance(String titulo, String descripcion, String creador) {

        TarjetaIncidenciaFragment fragment = new TarjetaIncidenciaFragment();
        Bundle args = new Bundle();
        args.putString("titulo", titulo);
        args.putString("descripcion", descripcion);
        args.putString("creador", creador);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTarjetaIncidenciaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TarjetaIncidenciaViewModel.class);

        titulo = getArguments().getString("titulo");
        descripcion = getArguments().getString("descripcion");
        creador = getArguments().getString("creador");

        binding.tvTituloTarjetaIncidencia.setText(titulo);
//        binding.tvDescripcionTarjetaIncidencia.setText(descripcion);
        binding.tvUsuarioTarjetaIncidencia.setText(creador);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewModel.getIncidenciaLiveData().observe(getViewLifecycleOwner(), incidencia -> {
                    inc = incidencia;

                    DetallesIncidenciaFragment detallesFragment = DetallesIncidenciaFragment.newInstance(inc);
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, detallesFragment);
//                    transaction.addToBackStack(null); // Permite volver atrás con el botón de retroceso
                    transaction.commit();


                });

                viewModel.getIncidencia(titulo, creador);
            }
        });

    }

    public void enableTvCreador() {
        binding.tvUsuarioTarjetaIncidencia.setVisibility(View.VISIBLE);
    }
}