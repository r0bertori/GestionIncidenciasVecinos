package com.example.gestionincidenciasvecinos.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestionincidenciasvecinos.LoginActivity;
import com.example.gestionincidenciasvecinos.models.Usuario;
import com.example.gestionincidenciasvecinos.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;


public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;
    private FirebaseAuth mAuth;
    private String id, correo, pwd, nombreAntiguo;
    private boolean esAdmin;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        id = mAuth.getCurrentUser().getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");

        // Rellenar los datos del usuario
        viewModel.getUsuarioLiveData().observe(getViewLifecycleOwner(), usuario -> {

            actualizarDatosUsuario(usuario);

            if (usuario.getEsAdmin()) {
                esAdmin = true;
            } else esAdmin = false;

        });

        viewModel.getUser(id);

        binding.btnGuardarCambiosPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nombreApellidos = binding.etNombrePerfil.getText() + " " + binding.etApellidosPerfil.getText();

                // Obtener datos
                Usuario usuario = new Usuario(correo, pwd, nombreApellidos,
                        binding.etPisoLetra.getText().toString(),
                        binding.etTelefonoPerfil.getText().toString()
                );

                if (esAdmin) {
                    usuario.setEsAdmin(true);
                }

                // Guardar cambios (requireContext para hacer un Toast si se guardan los datos)
                viewModel.saveUserData(id, usuario, nombreAntiguo, requireContext());

                actualizarDatosUsuario(usuario);

            }
        });

        binding.btnCerrarSesionPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmación")
                        .setMessage("¿Realmente deseas cerrar sesión?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }

    private void actualizarDatosUsuario(Usuario usuario) {
        if (usuario != null) {

            // Para poder cambiar el creador de las incidencias en caso de actualizar los datos
            nombreAntiguo = usuario.getNombreApellidos();

            correo = usuario.getCorreo();
            pwd = usuario.getPwd();

            // Nombre
            String[] nombreApellidos = usuario.getNombreApellidos().split(" ");
            binding.tvSaludo.setText("¡Hola, " + nombreApellidos[0] + "!");
            binding.etNombrePerfil.setText(nombreApellidos[0]);

            // Apellidos
            binding.etApellidosPerfil.setText(nombreApellidos[1]);

            // Piso y letra
            binding.etPisoLetra.setText(usuario.getPisoLetra());

            // Telefono
            binding.etTelefonoPerfil.setText(usuario.getNumTelefono());

            // Tipo de cuenta
            String esAdmin = "Tipo de cuenta : ";
            esAdmin += (usuario.getEsAdmin()) ? "admin" : "vecino";
            binding.tvTipoCuentaPerfil.setText(esAdmin);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}