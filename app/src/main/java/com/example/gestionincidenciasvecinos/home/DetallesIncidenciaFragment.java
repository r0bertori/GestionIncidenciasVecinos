package com.example.gestionincidenciasvecinos.home;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gestionincidenciasvecinos.LoginActivity;
import com.example.gestionincidenciasvecinos.R;
import com.example.gestionincidenciasvecinos.databinding.FragmentDetallesIncidenciaBinding;
import com.example.gestionincidenciasvecinos.databinding.FragmentHomeBinding;
import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetallesIncidenciaFragment extends Fragment {

    private DetallesIncidenciaViewModel viewModel;
    private Incidencia incidencia;
    private FragmentDetallesIncidenciaBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;

    public static DetallesIncidenciaFragment newInstance(Incidencia inc) {
        DetallesIncidenciaFragment fragment = new DetallesIncidenciaFragment();
        Bundle args = new Bundle();
        args.putSerializable("incidencia", inc);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetallesIncidenciaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DetallesIncidenciaViewModel.class);
        incidencia = (Incidencia) getArguments().getSerializable("incidencia");

        // Gestionar el botón de volver atrás del movil
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarVentana();
            }
        });

        binding.btnCerrarDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarVentana();
            }
        });

        binding.btnBorrarDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Confirmación")
                        .setMessage("¿Estás seguro de que quieres borrar la incidencia?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String key = (incidencia.getTitulo() + incidencia.getCreador()).replace(" ", "");
                                
                                viewModel.getEliminarLiveData().observe(getViewLifecycleOwner(), valor -> {
                                    if (valor) {
                                        Toast.makeText(getContext(), "Incidencia eliminada con éxito", Toast.LENGTH_SHORT).show();
                                        cerrarVentana();
                                    } else {
                                        Toast.makeText(getContext(), "Error al eliminar la incidencia", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                
                                viewModel.eliminarIncidencia(key);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        binding.btnCompartirDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mensaje = incidencia.toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mensaje);

                Intent chooser = Intent.createChooser(intent, "Compartir con :");
                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(getContext(), "No hay aplicaciones disponibles para compartir", Toast.LENGTH_SHORT).show();
                }

            }

        });

        binding.btnGuardarDatosDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Incidencia nuevaIncidencia;

                if (imageUri != null) {
                    nuevaIncidencia = new Incidencia(
                            String.valueOf(binding.etTituloDetallesIncidencia.getText()),
                            String.valueOf(binding.etDescripcionDetallesIncidencia.getText()),
                            String.valueOf(binding.tvUsuarioDetallesIncidencia.getText()),
                            imageUri
                    );
                } else {
                    nuevaIncidencia = new Incidencia(
                            String.valueOf(binding.etTituloDetallesIncidencia.getText()),
                            String.valueOf(binding.etDescripcionDetallesIncidencia.getText()),
                            String.valueOf(binding.tvUsuarioDetallesIncidencia.getText()),
                            incidencia.getImageURL()
                    );
                }



                String keyAntigua = (incidencia.getTitulo() + incidencia.getCreador()).replace(" ", "");

                viewModel.actualizarIncidencia(nuevaIncidencia, keyAntigua);

                Toast.makeText(getContext(), "Incidencia actualizada", Toast.LENGTH_SHORT).show();

                cerrarVentana();
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(binding.ibImagenDetallesIncidencia);
                    }
                }
        );

        binding.ibImagenDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setPackage("com.google.android.apps.photos");
                imagePickerLauncher.launch(i);
            }
        });

        binding.etTituloDetallesIncidencia.setText(incidencia.getTitulo());
        Picasso.get()
                .load(incidencia.getImageURL())
                .into(binding.ibImagenDetallesIncidencia);
        binding.etDescripcionDetallesIncidencia.setText(incidencia.getDescripcion());
        binding.tvUsuarioDetallesIncidencia.setText(incidencia.getCreador());

    }

    private void cerrarVentana() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment()); // Por ejemplo, mostrar el HomeFragment
        transaction.commit();
    }
}