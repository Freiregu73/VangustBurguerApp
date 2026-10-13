package com.example.vangustapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private TextInputEditText txEmailEsqueci;
    private Button btnEnviarRecuperacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        txEmailEsqueci = findViewById(R.id.txEmailEsqueci);
        btnEnviarRecuperacao = findViewById(R.id.btnEnviarRecuperacao);

        btnEnviarRecuperacao.setOnClickListener(v -> {
            String email = txEmailEsqueci.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, digite o seu e-mail", Toast.LENGTH_SHORT).show();
                return;
            }

            solicitarNovaSenha(email);
        });
    }

    private void solicitarNovaSenha(String email) {
        // Atualizado com o domínio oficial da Locaweb
        String url = "https://vangustburguer.com.br/api_hamburgueria/esqueci_senha.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");
                        String mensagem = jsonObject.optString("mensagem", "Processo concluído.");

                        if (sucesso) {
                            String senhaTemp = jsonObject.optString("senha_temporaria", "");
                            if (!senhaTemp.isEmpty()) {
                                Toast.makeText(this, "Senha temporária: " + senhaTemp, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
                            }
                            finish(); // Retorna à tela de login
                        } else {
                            String erro = jsonObject.optString("erro", "Erro ao recuperar senha");
                            Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}