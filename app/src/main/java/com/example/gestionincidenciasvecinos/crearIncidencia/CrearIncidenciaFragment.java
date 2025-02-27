package com.example.gestionincidenciasvecinos.crearIncidencia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionincidenciasvecinos.databinding.FragmentCrearIncidenciaBinding;
import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class CrearIncidenciaFragment extends Fragment {

    private FragmentCrearIncidenciaBinding binding;
    private FirebaseAuth mAuth;
    private CrearIncidenciaViewModel viewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearIncidenciaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CrearIncidenciaViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(binding.ibImagenCrearIncidencia);
                    }
                }
        );

        binding.ibImagenCrearIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setPackage("com.google.android.apps.photos");
                imagePickerLauncher.launch(i);
            }
        });

        binding.btnCrearIncidenciaCrearIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Si el usuario no existe en la BBDD (Sign-In with Google)
                if (viewModel.getUsuarioLiveData().getValue() == null) {
                    Toast.makeText(getContext(), "Rellena tus datos en 'Perfil'", Toast.LENGTH_SHORT).show();
                }

                // Si existe (Sign in normal o si lo ha personalizado en 'perfil')
                else {
                    if (camposRellenados()) {

                        binding.btnCrearIncidenciaCrearIncidencia.setEnabled(false);

                        viewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), fullName -> {
                            String titulo = binding.etTituloCrearIncidencia.getText().toString();
                            String desc = binding.etDescripcionCrearIncidencia.getText().toString();
                            String usuario = fullName;

                            Incidencia incidencia = new Incidencia(titulo, desc, usuario, imageUri);

                            viewModel.getCrearIncidenciaLiveData().observe(getViewLifecycleOwner(), crearIncidencia -> {
                                if (crearIncidencia) {

                                    binding.etTituloCrearIncidencia.setText("");
                                    binding.etDescripcionCrearIncidencia.setText("");
                                    binding.ibImagenCrearIncidencia.setImageDrawable(null);

                                    Toast.makeText(getContext(), "Incidencia creada con éxito", Toast.LENGTH_SHORT).show();

                                    binding.btnCrearIncidenciaCrearIncidencia.setEnabled(true);

                                } else {
                                    Toast.makeText(getContext(), "Error al crear la incidencia", Toast.LENGTH_SHORT).show();

                                    binding.btnCrearIncidenciaCrearIncidencia.setEnabled(true);
                                }
                            });

                            viewModel.crearIncidencia(incidencia, getContext());
                        });
                    } else {
                        Toast.makeText(getContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        String id = mAuth.getCurrentUser().getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");

        viewModel.getUserFullName(id);

    }

    private boolean camposRellenados() {

        if (!binding.etTituloCrearIncidencia.getText().toString().isEmpty() &&
            !binding.etDescripcionCrearIncidencia.getText().toString().isEmpty() &&
            !(imageUri == null)) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}