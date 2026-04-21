package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.utils.Constantes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EquiposActivity extends AppCompatActivity {

    private LinearLayout contenedorLista;
    private TextView textoVacio;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;

    private final ActivityResultLauncher<Intent> lanzadorFormulario =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> {
                        if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {

                            Intent data = resultado.getData();

                            String codigo = data.getStringExtra(Constantes.extra_codigo);
                            String nombre = data.getStringExtra(Constantes.extra_nombre);
                            String tipo = data.getStringExtra(Constantes.extra_tipo);
                            String estado = data.getStringExtra(Constantes.extra_estado);
                            String observaciones = data.getStringExtra(Constantes.extra_observaciones);

                            agregarItemEquipo(codigo, nombre, tipo, estado, observaciones);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipos);

        contenedorLista = findViewById(R.id.contenedor_lista);
        textoVacio = findViewById(R.id.texto_vacio);
        spinnerTipo = findViewById(R.id.spinner_tipo);
        spinnerEstado = findViewById(R.id.spinner_estado);

        FloatingActionButton fab = findViewById(R.id.fab_agregar);

        configurarSpinners();
        actualizarVistaVacia();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EquiposActivity.this, FormularioEquipoActivity.class);
                lanzadorFormulario.launch(intent);
            }
        });
    }

    private void configurarSpinners() {
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this,
                R.array.filtro_tipos_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this,
                R.array.filtro_estados_equipo,
                android.R.layout.simple_spinner_item
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);
    }

    private void actualizarVistaVacia() {
        if (contenedorLista.getChildCount() == 0) {
            textoVacio.setVisibility(View.VISIBLE);
        } else {
            textoVacio.setVisibility(View.GONE);
        }
    }

    private void agregarItemEquipo(String codigo,
                                   String nombre,
                                   String tipo,
                                   String estado,
                                   String observaciones) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View item = inflater.inflate(R.layout.item_equipo, contenedorLista, false);

        TextView textoCodigoItem = item.findViewById(R.id.texto_codigo_item);
        TextView textoNombreItem = item.findViewById(R.id.texto_nombre_item);
        TextView textoTipoItem = item.findViewById(R.id.texto_tipo_item);
        TextView textoEstadoItem = item.findViewById(R.id.texto_estado_item);

        textoCodigoItem.setText("Código: " + codigo);
        textoNombreItem.setText(nombre);
        textoTipoItem.setText("Tipo: " + tipo);
        textoEstadoItem.setText("Estado: " + estado);

        aplicarColorEstado(textoEstadoItem, estado);

        item.setTag(observaciones);

        contenedorLista.addView(item);
        actualizarVistaVacia();
    }

    private void aplicarColorEstado(TextView textoEstadoItem, String estado) {
        if (estado.equals(getString(R.string.estado_operativo))) {
            textoEstadoItem.setTextColor(Color.parseColor("#4CAF50"));
        } else if (estado.equals(getString(R.string.estado_mantenimiento))) {
            textoEstadoItem.setTextColor(Color.parseColor("#FFC107"));
        } else if (estado.equals(getString(R.string.estado_fuera_servicio))) {
            textoEstadoItem.setTextColor(Color.parseColor("#F44336"));
        }
    }
}