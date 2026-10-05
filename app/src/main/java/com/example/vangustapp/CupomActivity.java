package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CupomActivity extends AppCompatActivity {

    private RecyclerView idRecCupons;
    private TextInputEditText txInserirCupom;
    private MaterialButton btnAplicarCupom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_cupom); // Mantém o layout XML que já tens

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idRecCupons = findViewById(R.id.idRecCupons);
        txInserirCupom = findViewById(R.id.txInserirCupom);
        btnAplicarCupom = findViewById(R.id.btnAplicarCupom);

        idRecCupons.setLayoutManager(new LinearLayoutManager(this));

        // Criar lista de cupons disponíveis
        List<CupomModel> listaCupons = new ArrayList<>();
        listaCupons.add(new CupomModel("VANGUST10", "10% de desconto em todo o cardápio", 0.10));
        listaCupons.add(new CupomModel("PRIMEIRA", "R$ 10,00 de desconto na primeira compra", 10.00));

        AdapterCupom adapter = new AdapterCupom(listaCupons, this);
        idRecCupons.setAdapter(adapter);

        // Ação do botão "Aplicar" cupom digitado manualmente
        btnAplicarCupom.setOnClickListener(v -> {
            String codigoDigitado = txInserirCupom.getText().toString().trim().toUpperCase();

            if (codigoDigitado.isEmpty()) {
                Toast.makeText(this, "Digite um código de cupom!", Toast.LENGTH_SHORT).show();
            } else if (codigoDigitado.equals("VANGUST10") || codigoDigitado.equals("PRIMEIRA")) {
                SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("cupom_ativo", codigoDigitado).apply();
                Toast.makeText(this, "Cupom " + codigoDigitado + " aplicado!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cupom inválido ou expirado!", Toast.LENGTH_LONG).show();
            }
        });

        ImageView btnVoltarCupom = findViewById(R.id.btnVoltarCupom);
        if (btnVoltarCupom != null) {
            btnVoltarCupom.setOnClickListener(v -> finish());
        }
    }
}