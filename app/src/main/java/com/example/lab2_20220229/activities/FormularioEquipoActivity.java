package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.databinding.ActivityFormularioEquipoBinding;
import com.example.lab2_20220229.utils.Constantes;
import com.google.android.material.textfield.TextInputEditText;

public class FormularioEquipoActivity extends AppCompatActivity {

    private ActivityFormularioEquipoBinding binding;

    private String modoFormulario = Constantes.modo_crear;
    private int posicionEdicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormularioEquipoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarSpinnerTipo();
        leerModoFormulario();
        configurarEventoGuardar();
    }

    private void configurarSpinnerTipo() {
        ArrayAdapter<CharSequence> adapterTipoFormulario = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterTipoFormulario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipoFormulario.setAdapter(adapterTipoFormulario);
    }

    private void leerModoFormulario() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Constantes.extra_modo)) {
            modoFormulario = intent.getStringExtra(Constantes.extra_modo);
        }

        if (Constantes.modo_editar.equals(modoFormulario)) {
            posicionEdicion = intent.getIntExtra(Constantes.extra_posicion, -1);

            String codigo = intent.getStringExtra(Constantes.extra_codigo);
            String nombre = intent.getStringExtra(Constantes.extra_nombre);
            String tipo = intent.getStringExtra(Constantes.extra_tipo);
            String estado = intent.getStringExtra(Constantes.extra_estado);
            String observaciones = intent.getStringExtra(Constantes.extra_observaciones);

            binding.campoCodigo.setText(codigo);
            binding.campoNombre.setText(nombre);
            binding.campoObservaciones.setText(observaciones);

            seleccionarTipo(tipo);
            seleccionarEstado(estado);

            binding.campoCodigo.setEnabled(false);
            binding.spinnerTipoFormulario.setEnabled(false);

            binding.botonGuardar.setText(R.string.boton_actualizar);
            setTitle(R.string.titulo_editar);
        } else {
            setTitle(R.string.titulo_registrar);
        }
    }

    private void configurarEventoGuardar() {
        binding.botonGuardar.setOnClickListener(v -> {

            String codigo = obtenerTexto(binding.campoCodigo);
            String nombre = obtenerTexto(binding.campoNombre);
            String tipo = binding.spinnerTipoFormulario.getSelectedItem().toString();
            String estado = obtenerEstadoSeleccionado();
            String observaciones = obtenerTexto(binding.campoObservaciones);

            if (codigo.isEmpty() || nombre.isEmpty() || estado.isEmpty()) {
                Toast.makeText(
                        FormularioEquipoActivity.this,
                        "Complete los campos obligatorios",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            boolean esEdicion = Constantes.modo_editar.equals(modoFormulario);
            mostrarDialogoConfirmacion(codigo, nombre, tipo, estado, observaciones, esEdicion);
        });
    }

    private String obtenerTexto(TextInputEditText campo) {
        if (campo.getText() == null) {
            return "";
        }
        return campo.getText().toString().trim();
    }

    private String obtenerEstadoSeleccionado() {
        int idSeleccionado = binding.radioGrupoEstado.getCheckedRadioButtonId();

        if (idSeleccionado == -1) {
            return "";
        }

        RadioButton radioSeleccionado = findViewById(idSeleccionado);
        return radioSeleccionado.getText().toString();
    }

    private void seleccionarTipo(String tipoBuscado) {
        for (int i = 0; i < binding.spinnerTipoFormulario.getCount(); i++) {
            String tipoActual = binding.spinnerTipoFormulario.getItemAtPosition(i).toString();
            if (tipoActual.equals(tipoBuscado)) {
                binding.spinnerTipoFormulario.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals(getString(R.string.estado_operativo))) {
            binding.radioGrupoEstado.check(R.id.radio_operativo);
        } else if (estado.equals(getString(R.string.estado_mantenimiento))) {
            binding.radioGrupoEstado.check(R.id.radio_mantenimiento);
        } else if (estado.equals(getString(R.string.estado_fuera_servicio))) {
            binding.radioGrupoEstado.check(R.id.radio_fuera_servicio);
        }
    }

    private void mostrarDialogoConfirmacion(String codigo,
                                            String nombre,
                                            String tipo,
                                            String estado,
                                            String observaciones,
                                            boolean esEdicion) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmacion_titulo);

        if (esEdicion) {
            builder.setMessage(R.string.confirmacion_actualizar);
        } else {
            builder.setMessage(R.string.confirmacion_guardar);
        }

        builder.setPositiveButton(R.string.boton_si, (dialog, which) -> {
            Intent intentResultado = new Intent();

            intentResultado.putExtra(Constantes.extra_codigo, codigo);
            intentResultado.putExtra(Constantes.extra_nombre, nombre);
            intentResultado.putExtra(Constantes.extra_tipo, tipo);
            intentResultado.putExtra(Constantes.extra_estado, estado);
            intentResultado.putExtra(Constantes.extra_observaciones, observaciones);

            if (esEdicion) {
                intentResultado.putExtra(Constantes.extra_modo, Constantes.modo_editar);
                intentResultado.putExtra(Constantes.extra_posicion, posicionEdicion);
            } else {
                intentResultado.putExtra(Constantes.extra_modo, Constantes.modo_crear);
            }

            setResult(RESULT_OK, intentResultado);
            finish();
        });

        builder.setNegativeButton(R.string.boton_no, null);
        builder.show();
    }
}