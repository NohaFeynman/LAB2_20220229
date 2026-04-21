package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import android.widget.ArrayAdapter;

public class FormularioEquipoActivity extends AppCompatActivity {

    private TextInputEditText campoCodigo;
    private TextInputEditText campoNombre;
    private Spinner spinnerTipoFormulario;
    private RadioGroup radioGrupoEstado;
    private TextInputEditText campoObservaciones;
    private MaterialButton botonGuardar;

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
        ArrayAdapter<CharSequence> adapterTipoFormulario = ArrayAdapter.createFromResource(
                this,
                R.array.tipos_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterTipoFormulario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoFormulario.setAdapter(adapterTipoFormulario);

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

                mostrarDialogoConfirmacion(codigo, nombre, tipo, estado, observaciones);
            }
        });
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

    private void mostrarDialogoConfirmacion(String codigo,
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

            setResult(RESULT_OK, intentResultado);
            finish();
        });

        builder.setNegativeButton(R.string.boton_no, null);
        builder.show();
    }
}