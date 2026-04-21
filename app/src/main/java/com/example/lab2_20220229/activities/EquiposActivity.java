package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.models.Equipo;
import com.example.lab2_20220229.utils.Constantes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EquiposActivity extends AppCompatActivity {

    private LinearLayout contenedorLista;
    private TextView textoVacio;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;

    private final List<Equipo> listaEquipos = new ArrayList<>();

    private int posicionSeleccionada = -1;
    private ActionMode actionModeActual;

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
                                actualizarEquipo(posicion, codigo, nombre, tipo, estado, observaciones);
                            } else {
                                agregarEquipo(codigo, nombre, tipo, estado, observaciones);
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

        FloatingActionButton fabAgregar = findViewById(R.id.fab_agregar);

        configurarSpinners();
        configurarEventosFiltros();
        actualizarVistaVacia();

        fabAgregar.setOnClickListener(new View.OnClickListener() {
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

    private void configurarEventosFiltros() {
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderizarLista();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderizarLista();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void agregarEquipo(String codigo,
                               String nombre,
                               String tipo,
                               String estado,
                               String observaciones) {

        Equipo equipo = new Equipo(codigo, nombre, tipo, estado, observaciones);
        listaEquipos.add(equipo);
        renderizarLista();
    }

    private void actualizarEquipo(int posicion,
                                  String codigo,
                                  String nombre,
                                  String tipo,
                                  String estado,
                                  String observaciones) {

        if (posicion >= 0 && posicion < listaEquipos.size()) {
            listaEquipos.set(posicion, new Equipo(codigo, nombre, tipo, estado, observaciones));
            renderizarLista();
        }
    }

    private void eliminarEquipo(int posicion) {
        if (posicion >= 0 && posicion < listaEquipos.size()) {
            listaEquipos.remove(posicion);
            renderizarLista();
        }
    }

    private void renderizarLista() {
        contenedorLista.removeAllViews();

        String filtroTipo = spinnerTipo.getSelectedItem() != null
                ? spinnerTipo.getSelectedItem().toString()
                : "Todos los tipos";

        String filtroEstado = spinnerEstado.getSelectedItem() != null
                ? spinnerEstado.getSelectedItem().toString()
                : "Todos los estados";

        for (int i = 0; i < listaEquipos.size(); i++) {
            Equipo equipo = listaEquipos.get(i);

            boolean cumpleTipo = filtroTipo.equals("Todos los tipos") || equipo.tipo.equals(filtroTipo);
            boolean cumpleEstado = filtroEstado.equals("Todos los estados") || equipo.estado.equals(filtroEstado);

            if (cumpleTipo && cumpleEstado) {
                View item = LayoutInflater.from(this).inflate(R.layout.item_equipo, contenedorLista, false);

                llenarDatosItem(item, equipo);
                configurarLongClick(item, i);

                contenedorLista.addView(item);
            }
        }

        actualizarVistaVacia();
    }

    private void llenarDatosItem(View item, Equipo equipo) {
        TextView textoCodigoItem = item.findViewById(R.id.texto_codigo_item);
        TextView textoNombreItem = item.findViewById(R.id.texto_nombre_item);
        TextView textoTipoItem = item.findViewById(R.id.texto_tipo_item);
        TextView textoEstadoItem = item.findViewById(R.id.texto_estado_item);

        textoCodigoItem.setText("Código: " + equipo.codigo);
        textoNombreItem.setText(equipo.nombre);
        textoTipoItem.setText("Tipo: " + equipo.tipo);
        textoEstadoItem.setText("Estado: " + equipo.estado);

        aplicarColorEstado(textoEstadoItem, equipo.estado);
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

    private void configurarLongClick(View item, int posicionReal) {
        item.setTag(posicionReal);

        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Object tag = v.getTag();

                if (tag instanceof Integer) {
                    posicionSeleccionada = (Integer) tag;
                } else {
                    posicionSeleccionada = -1;
                }

                if (actionModeActual != null) {
                    actionModeActual.finish();
                }

                actionModeActual = startSupportActionMode(actionModeCallback);
                return true;
            }
        });
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contextual_equipo, menu);
            mode.setTitle(getString(R.string.titulo_equipos));
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
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionModeActual = null;
        }
    };

    private void abrirFormularioEdicion() {
        if (posicionSeleccionada < 0 || posicionSeleccionada >= listaEquipos.size()) {
            return;
        }

        Equipo equipo = listaEquipos.get(posicionSeleccionada);

        Intent intent = new Intent(EquiposActivity.this, FormularioEquipoActivity.class);
        intent.putExtra(Constantes.extra_modo, Constantes.modo_editar);
        intent.putExtra(Constantes.extra_posicion, posicionSeleccionada);
        intent.putExtra(Constantes.extra_codigo, equipo.codigo);
        intent.putExtra(Constantes.extra_nombre, equipo.nombre);
        intent.putExtra(Constantes.extra_tipo, equipo.tipo);
        intent.putExtra(Constantes.extra_estado, equipo.estado);
        intent.putExtra(Constantes.extra_observaciones, equipo.observaciones);

        lanzadorFormulario.launch(intent);
    }

    private void mostrarDialogoEliminar() {
        if (posicionSeleccionada < 0 || posicionSeleccionada >= listaEquipos.size()) {
            return;
        }

        final int posicionAEliminar = posicionSeleccionada;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmacion_titulo);
        builder.setMessage(R.string.confirmacion_eliminar);

        builder.setPositiveButton(R.string.boton_si, (dialog, which) -> {
            eliminarEquipo(posicionAEliminar);
            posicionSeleccionada = -1;

            if (actionModeActual != null) {
                actionModeActual.finish();
            }
        });

        builder.setNegativeButton(R.string.boton_no, (dialog, which) -> {
            if (actionModeActual != null) {
                actionModeActual.finish();
            }
        });

        builder.show();
    }

    private void actualizarVistaVacia() {
        if (contenedorLista.getChildCount() == 0) {
            textoVacio.setVisibility(View.VISIBLE);
        } else {
            textoVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_equipos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opcion_refresh) {
            spinnerTipo.setSelection(0);
            spinnerEstado.setSelection(0);
            renderizarLista();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}