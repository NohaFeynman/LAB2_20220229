package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.utils.ValidacionConexion;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Referencia al botón
        View botonIngresar = findViewById(R.id.boton_ingresar);

        // Evento click
        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validar conexión
                boolean hayConexion = ValidacionConexion.hayConexionInternet(InicioActivity.this);

                if (!hayConexion) {
                    Toast.makeText(
                            InicioActivity.this,
                            getString(R.string.error_sin_internet),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    // Ir a EquiposActivity
                    Intent intent = new Intent(InicioActivity.this, EquiposActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}