package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private Button btnCadastrarFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cadastro_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_cadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editNome = findViewById(R.id.txNomeCadastro);
        editEmail = findViewById(R.id.txEmailCadastro);
        editSenha = findViewById(R.id.txSenhaCadastro);
        btnCadastrarFinal = findViewById(R.id.btnFinalizarCadastro);

        btnCadastrarFinal.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String senha = editSenha.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else if (senha.length() < 6) {
                Toast.makeText(CadastroActivity.this, "A palavra-passe deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            } else {
                // GUARDAR OS DADOS LOCALMENTE
                SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nome_usuario", nome);
                editor.putString("email_usuario", email);
                editor.putString("senha_usuario", senha);
                editor.apply(); // Salva de forma assíncrona

                Toast.makeText(CadastroActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

                // Redireciona para o login ou menu principal
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}