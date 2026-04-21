package com.example.lab2_20220229.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20220229.R;
import com.example.lab2_20220229.databinding.ActivityInicioBinding;
import com.example.lab2_20220229.utils.ValidacionConexion;

public class InicioActivity extends AppCompatActivity {

    private ActivityInicioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.botonIngresar.setOnClickListener(v -> {
            boolean hayConexion = ValidacionConexion.hayConexionInternet(InicioActivity.this);

            if (!hayConexion) {
                Toast.makeText(
                        InicioActivity.this,
                        getString(R.string.error_sin_internet),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Intent intent = new Intent(InicioActivity.this, EquiposActivity.class);
                startActivity(intent);
            }
        });
    }
}