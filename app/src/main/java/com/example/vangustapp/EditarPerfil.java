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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditarPerfil extends AppCompatActivity {

    private TextInputEditText txEditarNome, txEditarEmail, txEditarTelefone;
    private MaterialButton btnSalvarPerfil;
    private ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar_perfil_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ligar componentes
        txEditarNome = findViewById(R.id.txEditarNome);
        txEditarEmail = findViewById(R.id.txEditarEmail);
        txEditarTelefone = findViewById(R.id.txEditarTelefone);
        btnSalvarPerfil = findViewById(R.id.btnSalvarPerfil);

        // Carregar dados atuais do SharedPreferences nos campos
        SharedPreferences preferences = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String nomeAtual = preferences.getString("nome_usuario", "");
        String emailAtual = preferences.getString("email_usuario", "");
        String telefoneAtual = preferences.getString("telefone_usuario", "");

        if (txEditarNome != null) txEditarNome.setText(nomeAtual);
        if (txEditarEmail != null) txEditarEmail.setText(emailAtual);
        if (txEditarTelefone != null) txEditarTelefone.setText(telefoneAtual);

        // Botão para salvar alterações
        btnSalvarPerfil.setOnClickListener(v -> {
            String novoNome = txEditarNome.getText().toString().trim();
            String novoEmail = txEditarEmail.getText().toString().trim();
            String novoTelefone = txEditarTelefone.getText().toString().trim();

            if (novoNome.isEmpty() || novoEmail.isEmpty()) {
                Toast.makeText(this, "O nome e o e-mail não podem estar vazios!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nome_usuario", novoNome);
                editor.putString("email_usuario", novoEmail);
                editor.putString("telefone_usuario", novoTelefone);
                editor.apply();

                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela de edição e regressa ao perfil
            }
        });
    }
}