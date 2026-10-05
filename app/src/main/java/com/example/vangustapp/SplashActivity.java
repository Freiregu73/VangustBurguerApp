package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(() -> {
            // Verificar no SharedPreferences se o utilizador já está logado
            SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
            boolean usuarioLogado = preferences.getBoolean("is_logged", false);

            Intent intent;
            if (usuarioLogado) {
                // Se já estiver logado, pula o login e vai direto para o Menu Principal
                intent = new Intent(getApplicationContext(), MenuPrincipalActivity.class);
            } else {
                // Se não estiver logado, vai para o Login
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}