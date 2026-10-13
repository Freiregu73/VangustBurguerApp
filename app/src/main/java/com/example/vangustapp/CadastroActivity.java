package com.example.vangustapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                Toast.makeText(CadastroActivity.this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            } else {
                salvarCadastroNaApi(nome, email, senha);
            }
        });
    }

    private void salvarCadastroNaApi(String nome, String email, String senha) {
        // Atualizado com o domínio oficial da Locaweb
        String url = "https://vangustburguer.com.br/api_hamburgueria/cadastrar.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");

                        if (sucesso) {
                            Toast.makeText(CadastroActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String erro = jsonObject.optString("erro", "Erro ao cadastrar");
                            Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CadastroActivity.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(CadastroActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("email", email);
                params.put("senha", senha);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}