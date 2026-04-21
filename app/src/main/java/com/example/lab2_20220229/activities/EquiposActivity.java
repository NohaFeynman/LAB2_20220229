package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.utils.Constantes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EquiposActivity extends AppCompatActivity {

    private LinearLayout contenedorLista;
    private TextView textoVacio;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;

    private View itemSeleccionado;
    private int posicionSeleccionada = -1;

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
                            String modo = data.getStringExtra(Constantes.extra_modo);

                            if (modo != null && modo.equals(Constantes.modo_editar)) {
                                int posicion = data.getIntExtra(Constantes.extra_posicion, -1);
                                actualizarItemEquipo(posicion, codigo, nombre, tipo, estado, observaciones);
                            } else {
                                agregarItemEquipo(codigo, nombre, tipo, estado, observaciones);
                            }
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
                intent.putExtra(Constantes.extra_modo, Constantes.modo_crear);
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

        llenarDatosItem(item, codigo, nombre, tipo, estado, observaciones);
        configurarLongClick(item);

        contenedorLista.addView(item);
        actualizarVistaVacia();
    }

    private void actualizarItemEquipo(int posicion,
                                      String codigo,
                                      String nombre,
                                      String tipo,
                                      String estado,
                                      String observaciones) {

        if (posicion >= 0 && posicion < contenedorLista.getChildCount()) {
            View item = contenedorLista.getChildAt(posicion);
            llenarDatosItem(item, codigo, nombre, tipo, estado, observaciones);
        }
    }

    private void llenarDatosItem(View item,
                                 String codigo,
                                 String nombre,
                                 String tipo,
                                 String estado,
                                 String observaciones) {

        TextView textoCodigoItem = item.findViewById(R.id.texto_codigo_item);
        TextView textoNombreItem = item.findViewById(R.id.texto_nombre_item);
        TextView textoTipoItem = item.findViewById(R.id.texto_tipo_item);
        TextView textoEstadoItem = item.findViewById(R.id.texto_estado_item);

        textoCodigoItem.setText("Código: " + codigo);
        textoNombreItem.setText(nombre);
        textoTipoItem.setText("Tipo: " + tipo);
        textoEstadoItem.setText("Estado: " + estado);

        aplicarColorEstado(textoEstadoItem, estado);

        item.setTag(R.id.texto_codigo_item, codigo);
        item.setTag(R.id.texto_nombre_item, nombre);
        item.setTag(R.id.texto_tipo_item, tipo);
        item.setTag(R.id.texto_estado_item, estado);
        item.setTag(R.id.texto_vacio, observaciones);
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

    private void configurarLongClick(View item) {
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemSeleccionado = v;
                posicionSeleccionada = contenedorLista.indexOfChild(v);
                startActionMode(actionModeCallback);
                return true;
            }
        });
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual_equipo, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.opcion_editar) {
                abrirFormularioEdicion();
                mode.finish();
                return true;
            }

            if (item.getItemId() == R.id.opcion_eliminar) {
                mostrarDialogoEliminar();
                mode.finish();
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            itemSeleccionado = null;
            posicionSeleccionada = -1;
        }
    };

    private void abrirFormularioEdicion() {
        if (itemSeleccionado == null) {
            return;
        }

        String codigo = (String) itemSeleccionado.getTag(R.id.texto_codigo_item);
        String nombre = (String) itemSeleccionado.getTag(R.id.texto_nombre_item);
        String tipo = (String) itemSeleccionado.getTag(R.id.texto_tipo_item);
        String estado = (String) itemSeleccionado.getTag(R.id.texto_estado_item);
        String observaciones = (String) itemSeleccionado.getTag(R.id.texto_vacio);

        Intent intent = new Intent(EquiposActivity.this, FormularioEquipoActivity.class);
        intent.putExtra(Constantes.extra_modo, Constantes.modo_editar);
        intent.putExtra(Constantes.extra_posicion, posicionSeleccionada);
        intent.putExtra(Constantes.extra_codigo, codigo);
        intent.putExtra(Constantes.extra_nombre, nombre);
        intent.putExtra(Constantes.extra_tipo, tipo);
        intent.putExtra(Constantes.extra_estado, estado);
        intent.putExtra(Constantes.extra_observaciones, observaciones);

        lanzadorFormulario.launch(intent);
    }

    private void mostrarDialogoEliminar() {
        if (itemSeleccionado == null || posicionSeleccionada == -1) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmacion_titulo);
        builder.setMessage(R.string.confirmacion_eliminar);

        builder.setPositiveButton(R.string.boton_si, (dialog, which) -> {
            contenedorLista.removeViewAt(posicionSeleccionada);
            actualizarVistaVacia();
            itemSeleccionado = null;
            posicionSeleccionada = -1;
        });

        builder.setNegativeButton(R.string.boton_no, null);
        builder.show();
    }
}