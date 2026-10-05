package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class NovoEndereco extends AppCompatActivity {

    private TextInputEditText txNomeLocal, txCep, txRua, txNumero, txComplemento, txBairro;
    private Button btnSalvarEndereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.novo_endereco_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Ligar as variáveis aos IDs do layout novo_endereco_layout.xml
        txNomeLocal = findViewById(R.id.txNomeLocal);
        txCep = findViewById(R.id.txCep);
        txRua = findViewById(R.id.txRua);
        txNumero = findViewById(R.id.txNumero);
        txComplemento = findViewById(R.id.txComplemento);
        txBairro = findViewById(R.id.txBairro);
        btnSalvarEndereco = findViewById(R.id.btnSalvarEndereco);

        // 2. Ação do botão salvar endereço
        btnSalvarEndereco.setOnClickListener(v -> {
            String nomeLocal = txNomeLocal.getText().toString().trim();
            String cep = txCep.getText().toString().trim();
            String rua = txRua.getText().toString().trim();
            String numero = txNumero.getText().toString().trim();
            String complemento = txComplemento.getText().toString().trim();
            String bairro = txBairro.getText().toString().trim();

            if (nomeLocal.isEmpty() || rua.isEmpty() || numero.isEmpty() || bairro.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            } else {
                // Montar o formato completo do endereço para exibir no Checkout
                // Ex: "Casa - Rua das Flores, 123 (Apto 4) - Centro"
                String enderecoCompleto = nomeLocal + " - " + rua + ", " + numero;
                if (!complemento.isEmpty()) {
                    enderecoCompleto += " (" + complemento + ")";
                }
                enderecoCompleto += " - " + bairro;

                // Salvar no SharedPreferences (chave usada pelo CheckoutFragment)
                SharedPreferences preferences = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("endereco_usuario", enderecoCompleto);
                editor.apply();

                Toast.makeText(this, "Endereço guardado com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela de cadastro de endereço e volta para o anterior
            }
        });

        // Ligar o botão de voltar
        ImageView btnVoltarNovoEndereco = findViewById(R.id.btnVoltarNovoEndereco);
        if (btnVoltarNovoEndereco != null) {
            btnVoltarNovoEndereco.setOnClickListener(v -> finish());
        }
    }
}