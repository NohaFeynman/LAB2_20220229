package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.utils.Constantes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class FormularioEquipoActivity extends AppCompatActivity {

    private TextInputEditText campoCodigo;
    private TextInputEditText campoNombre;
    private Spinner spinnerTipoFormulario;
    private RadioGroup radioGrupoEstado;
    private TextInputEditText campoObservaciones;
    private MaterialButton botonGuardar;

    private String modoFormulario = Constantes.modo_crear;
    private int posicionEdicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_equipo);

        campoCodigo = findViewById(R.id.campo_codigo);
        campoNombre = findViewById(R.id.campo_nombre);
        spinnerTipoFormulario = findViewById(R.id.spinner_tipo_formulario);
        radioGrupoEstado = findViewById(R.id.radio_grupo_estado);
        campoObservaciones = findViewById(R.id.campo_observaciones);
        botonGuardar = findViewById(R.id.boton_guardar);

        configurarSpinnerTipo();
        leerModoFormulario();

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codigo = obtenerTexto(campoCodigo);
                String nombre = obtenerTexto(campoNombre);
                String tipo = spinnerTipoFormulario.getSelectedItem().toString();
                String estado = obtenerEstadoSeleccionado();
                String observaciones = obtenerTexto(campoObservaciones);

                if (codigo.isEmpty() || nombre.isEmpty() || estado.isEmpty()) {
                    Toast.makeText(
                            FormularioEquipoActivity.this,
                            "Complete los campos obligatorios",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if (modoFormulario.equals(Constantes.modo_editar)) {
                    mostrarDialogoConfirmacionActualizar(codigo, nombre, tipo, estado, observaciones);
                } else {
                    mostrarDialogoConfirmacionGuardar(codigo, nombre, tipo, estado, observaciones);
                }
            }
        });
    }

    private void configurarSpinnerTipo() {
        ArrayAdapter<CharSequence> adapterTipoFormulario = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterTipoFormulario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoFormulario.setAdapter(adapterTipoFormulario);
    }

    private void leerModoFormulario() {
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Constantes.extra_modo)) {
            modoFormulario = intent.getStringExtra(Constantes.extra_modo);
        }

        if (modoFormulario != null && modoFormulario.equals(Constantes.modo_editar)) {
            posicionEdicion = intent.getIntExtra(Constantes.extra_posicion, -1);

            String codigo = intent.getStringExtra(Constantes.extra_codigo);
            String nombre = intent.getStringExtra(Constantes.extra_nombre);
            String tipo = intent.getStringExtra(Constantes.extra_tipo);
            String estado = intent.getStringExtra(Constantes.extra_estado);
            String observaciones = intent.getStringExtra(Constantes.extra_observaciones);

            campoCodigo.setText(codigo);
            campoNombre.setText(nombre);
            campoObservaciones.setText(observaciones);

            seleccionarTipo(tipo);
            seleccionarEstado(estado);

            campoCodigo.setEnabled(false);
            spinnerTipoFormulario.setEnabled(false);

            botonGuardar.setText(R.string.boton_actualizar);
            setTitle(R.string.titulo_editar);
        } else {
            setTitle(R.string.titulo_registrar);
        }
    }

    private void seleccionarTipo(String tipoBuscado) {
        for (int i = 0; i < spinnerTipoFormulario.getCount(); i++) {
            String tipoActual = spinnerTipoFormulario.getItemAtPosition(i).toString();
            if (tipoActual.equals(tipoBuscado)) {
                spinnerTipoFormulario.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarEstado(String estado) {
        if (estado.equals(getString(R.string.estado_operativo))) {
            radioGrupoEstado.check(R.id.radio_operativo);
        } else if (estado.equals(getString(R.string.estado_mantenimiento))) {
            radioGrupoEstado.check(R.id.radio_mantenimiento);
        } else if (estado.equals(getString(R.string.estado_fuera_servicio))) {
            radioGrupoEstado.check(R.id.radio_fuera_servicio);
        }
    }

    private String obtenerTexto(TextInputEditText campo) {
        if (campo.getText() == null) {
            return "";
        }
        return campo.getText().toString().trim();
    }

    private String obtenerEstadoSeleccionado() {
        int idSeleccionado = radioGrupoEstado.getCheckedRadioButtonId();

        if (idSeleccionado == -1) {
            return "";
        }

        RadioButton radioSeleccionado = findViewById(idSeleccionado);
        return radioSeleccionado.getText().toString();
    }

    private void mostrarDialogoConfirmacionGuardar(String codigo,
                                                   String nombre,
                                                   String tipo,
                                                   String estado,
                                                   String observaciones) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmacion_titulo);
        builder.setMessage(R.string.confirmacion_guardar);

        builder.setPositiveButton(R.string.boton_si, (dialog, which) -> {
            Intent intentResultado = new Intent();
            intentResultado.putExtra(Constantes.extra_codigo, codigo);
            intentResultado.putExtra(Constantes.extra_nombre, nombre);
            intentResultado.putExtra(Constantes.extra_tipo, tipo);
            intentResultado.putExtra(Constantes.extra_estado, estado);
            intentResultado.putExtra(Constantes.extra_observaciones, observaciones);
            intentResultado.putExtra(Constantes.extra_modo, Constantes.modo_crear);

            setResult(RESULT_OK, intentResultado);
            finish();
        });

        builder.setNegativeButton(R.string.boton_no, null);
        builder.show();
    }

    private void mostrarDialogoConfirmacionActualizar(String codigo,
                                                      String nombre,
                                                      String tipo,
                                                      String estado,
                                                      String observaciones) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmacion_titulo);
        builder.setMessage(R.string.confirmacion_actualizar);

        builder.setPositiveButton(R.string.boton_si, (dialog, which) -> {
            Intent intentResultado = new Intent();
            intentResultado.putExtra(Constantes.extra_codigo, codigo);
            intentResultado.putExtra(Constantes.extra_nombre, nombre);
            intentResultado.putExtra(Constantes.extra_tipo, tipo);
            intentResultado.putExtra(Constantes.extra_estado, estado);
            intentResultado.putExtra(Constantes.extra_observaciones, observaciones);
            intentResultado.putExtra(Constantes.extra_modo, Constantes.modo_editar);
            intentResultado.putExtra(Constantes.extra_posicion, posicionEdicion);

            setResult(RESULT_OK, intentResultado);
            finish();
        });

        builder.setNegativeButton(R.string.boton_no, null);
        builder.show();
    }
}