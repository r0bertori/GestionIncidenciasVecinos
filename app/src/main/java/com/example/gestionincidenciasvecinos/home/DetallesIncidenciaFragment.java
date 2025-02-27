package com.example.gestionincidenciasvecinos.home;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
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
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestionincidenciasvecinos.LoginActivity;
import com.example.gestionincidenciasvecinos.R;
import com.example.gestionincidenciasvecinos.databinding.FragmentDetallesIncidenciaBinding;
import com.example.gestionincidenciasvecinos.databinding.FragmentHomeBinding;
import com.example.gestionincidenciasvecinos.models.Incidencia;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetallesIncidenciaFragment extends Fragment {

    private DetallesIncidenciaViewModel viewModel;
    private Incidencia incidencia;
    private FragmentDetallesIncidenciaBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private List<String> comentarios;
    private boolean userEsAdmin;

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
        mAuth = FirebaseAuth.getInstance();
        if (incidencia.getComentarios() == null) {
            comentarios = new ArrayList<>();
        } else {
            comentarios = incidencia.getComentarios();
        }

        String id = mAuth.getCurrentUser().getEmail()
                .replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");

        viewModel.getEsAdminLiveData().observe(getViewLifecycleOwner(), esAdmin -> {
            userEsAdmin = esAdmin;
            if (userEsAdmin) {

                // Configurar elementos de la pantalla
                binding.spnrEstadoDetallesIncidencia.setEnabled(true);
                binding.tvComentariosDetallesIncidencia.setVisibility(View.VISIBLE);
                binding.tvComentariosDetallesIncidencia.setText("Añadir comentarios del estado :");
                binding.btnAniadirComentarioDetallesIncidencia.setVisibility(View.VISIBLE);

                // Cargar comentarios previos
                if (incidencia.getComentarios() != null) {
                    for (int i=0; i<incidencia.getComentarios().size(); i++) {
                        agregarEditText(incidencia.getComentarios().get(i));
                    }
                }

            } else {

                // Configurar elementos de la pantalla
                binding.spnrEstadoDetallesIncidencia.setEnabled(false);
                if (incidencia.getComentarios() == null || incidencia.getComentarios().isEmpty()) {
                    binding.tvComentariosDetallesIncidencia.setVisibility(View.GONE);
                } else {
                    binding.tvComentariosDetallesIncidencia.setVisibility(View.VISIBLE);
                    binding.tvComentariosDetallesIncidencia.setText("Comentarios del estado :");
                }
                binding.btnAniadirComentarioDetallesIncidencia.setVisibility(View.GONE);

                // Cargar comentarios previos
                if (incidencia.getComentarios() != null) {
                    binding.llComentariosDetallesIncidencia.removeAllViews();

                    for (String comentario : incidencia.getComentarios()) {
                        if (!comentario.trim().isEmpty()) {
                            agregarTextView(comentario);
                        }
                    }
                }



            }
        });

        viewModel.getEsAdmin(id);

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

        binding.btnAniadirComentarioDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Si hay algún editText, comprobar que el usuario exista
                if (binding.llComentariosDetallesIncidencia.getChildCount() != 0) {

                    EditText etComentario = (EditText) binding.llComentariosDetallesIncidencia.getChildAt(binding.llComentariosDetallesIncidencia.getChildCount()-1);

                    String comentario = String.valueOf(etComentario.getText());

                    // Si el campo está vacío
                    if (comentario.isEmpty()) {
                        Toast.makeText(getContext(), "El último campo de comentario está vacío", Toast.LENGTH_SHORT).show();
                    }

                    // Sino
                    else {
                        agregarEditText("");
                    }
                }

                // Si no hay nada, crea un editText directamente
                else {
                    agregarEditText("");
                }

            }
        });


        binding.btnGuardarDatosDetallesIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.btnGuardarDatosDetallesIncidencia.setEnabled(false);

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

                if (userEsAdmin) {
                    nuevaIncidencia.setEstado(String.valueOf(binding.spnrEstadoDetallesIncidencia.getSelectedItem()));
                    for (int i = 0; i < binding.llComentariosDetallesIncidencia.getChildCount(); i++) {
                        View child = binding.llComentariosDetallesIncidencia.getChildAt(i);

                        if (child instanceof EditText) {
                            String texto = ((EditText) child).getText().toString().trim();
                            if (!texto.isEmpty()) {
                                comentarios.add(texto);
                            }
                        }
                    }

                    if (comentarios != null) {
                        nuevaIncidencia.setComentarios(comentarios);
                    }

                }

                String keyAntigua = (incidencia.getTitulo() + incidencia.getCreador()).replace(" ", "");

                viewModel.getActualizacionLiveData().observe(getViewLifecycleOwner(), actualizacion -> {
                    if (actualizacion) {
                        Toast.makeText(getContext(), "Incidencia actualizada", Toast.LENGTH_SHORT).show();
                        binding.btnGuardarDatosDetallesIncidencia.setEnabled(true);
                        cerrarVentana();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar la incidencia", Toast.LENGTH_SHORT).show();
                        binding.btnGuardarDatosDetallesIncidencia.setEnabled(true);
                    }
                });

                viewModel.actualizarIncidencia(nuevaIncidencia, keyAntigua);
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
        if (incidencia.getEstado() != null) {
            SpinnerAdapter adapter = binding.spnrEstadoDetallesIncidencia.getAdapter();

            if (adapter != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).toString().equals(incidencia.getEstado())) {
                        binding.spnrEstadoDetallesIncidencia.setSelection(i);
                        break;
                    }
                }
            }
        }

    }

    private void agregarTextView(String comentario) {
        TextView nuevoTextView = new TextView(getContext());

        // Convertir dp a píxeles
        int marginTopInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );
        int marginHorPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );

        // Crear LayoutParams con MATCH_PARENT y WRAP_CONTENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Establecer el margen superior
        params.setMargins(marginHorPx, marginTopInPx, marginHorPx, 0);

        // Aplicar los LayoutParams al TextView
        nuevoTextView.setLayoutParams(params);

        // Establecer el texto
        nuevoTextView.setText(comentario);

        // Configurar tamaño del texto y color (opcional, personaliza según tu diseño)
        float textSizePx = getResources().getDimension(R.dimen.txt_texto);
        float textSizeSp = textSizePx / getResources().getDisplayMetrics().scaledDensity;
        nuevoTextView.setTextSize(textSizeSp);

        nuevoTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        // Agregar el TextView al LinearLayout
        binding.llComentariosDetallesIncidencia.addView(nuevoTextView);
    }


    private void agregarEditText(String comentario) {
        EditText nuevoEditText = new EditText(getContext());

        // Convertir dp a píxeles
        int marginTopInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );
        int marginHorPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );

        // Crear LayoutParams con MATCH_PARENT y WRAP_CONTENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Establecer el margen superior
        params.setMargins(marginHorPx, marginTopInPx, marginHorPx, 0);

        // Aplicar los LayoutParams al EditText
        nuevoEditText.setLayoutParams(params);

        // Establecer fondo personalizado
        nuevoEditText.setBackground(getContext().getResources().getDrawable(R.drawable.et_borde_login));

        // Establecer el texto
        nuevoEditText.setText(comentario);

        // Agregar el EditText al LinearLayout
        binding.llComentariosDetallesIncidencia.addView(nuevoEditText);
    }

    private void cerrarVentana() {
//        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
//        transaction.commit();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}