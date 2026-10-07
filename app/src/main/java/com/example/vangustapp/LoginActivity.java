package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editSenha;
    private Button btnEntrar, btnCadastrar;

    private View linkcadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.txEmail);
        editSenha = findViewById(R.id.txSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        linkcadastrar = findViewById(R.id.LinkCadastrar);

        btnEntrar.setOnClickListener(v -> {
            String emailDigitado = editEmail.getText().toString().trim();
            String senhaDigitada = editSenha.getText().toString().trim();

            if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // URL da sua API PHP rodando no XAMPP (use 10.0.2.2 para o Emulador Android)
            String url = "http://10.0.2.2/api_hamburgueria/login.php";

            com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                    com.android.volley.Request.Method.POST, url,
                    response -> {
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(response);
                            boolean sucesso = jsonObject.getBoolean("sucesso");

                            if (sucesso) {
                                // Pega os dados do usuário retornado pela API
                                org.json.JSONObject usuarioObj = jsonObject.getJSONObject("usuario");
                                String nomeUsuario = usuarioObj.getString("nome");

                                // Salva no SharedPreferences que o usuário está logado e guarda o nome
                                SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("is_logged", true);
                                editor.putString("nome_usuario", nomeUsuario);
                                editor.putString("email_usuario", emailDigitado);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Toast.makeText(LoginActivity.this, "E-mail ou senha incorretos / Erro de conexão", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    params.put("email", emailDigitado);
                    params.put("senha", senhaDigitada);
                    return params;
                }
            };

            // Adiciona a requisição à fila do Volley
            com.android.volley.toolbox.Volley.newRequestQueue(this).add(stringRequest);
        });

        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        linkcadastrar.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });


    }
}