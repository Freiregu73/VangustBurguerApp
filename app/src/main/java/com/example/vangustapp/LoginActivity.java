package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    private TextView linkEsqueceuSenha;

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
        linkEsqueceuSenha = findViewById(R.id.linkEsqueceuSenha);

        btnEntrar.setOnClickListener(v -> {
            String emailDigitado = editEmail.getText().toString().trim();
            String senhaDigitada = editSenha.getText().toString().trim();

            if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2/api_hamburgueria/login.php";

            com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                    com.android.volley.Request.Method.POST, url,
                    response -> {
                        Log.d("LOGIN_RESPOSTA", response);

                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(response);
                            boolean sucesso = jsonObject.getBoolean("sucesso");

                            if (sucesso) {
                                org.json.JSONObject usuarioObj = jsonObject.getJSONObject("usuario");

                                int idCliente = usuarioObj.has("id") ? usuarioObj.getInt("id") : usuarioObj.optInt("id_usuario", -1);
                                String nomeUsuario = usuarioObj.getString("nome");
                                String fotoBase64 = jsonObject.optString("foto", "");

                                SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear(); // <--- Limpa qualquer dado residual de contas anteriores
                                editor.putBoolean("is_logged", true);
                                editor.putInt("id_cliente", idCliente);
                                editor.putString("nome_usuario", nomeUsuario);
                                editor.putString("email_usuario", emailDigitado);
                                editor.putString("foto_perfil", fotoBase64);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MenuPrincipalActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erro JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(LoginActivity.this, "E-mail ou senha incorretos / Erro de conexão", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new java.util.HashMap<>();
                    params.put("email", emailDigitado);
                    params.put("senha", senhaDigitada);
                    return params;
                }
            };

            com.android.volley.toolbox.Volley.newRequestQueue(this).add(stringRequest);
        });

        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // Configuração segura do clique para abrir a tela de Esqueceu a Senha
        if (linkEsqueceuSenha != null) {
            linkEsqueceuSenha.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, EsqueciSenhaActivity.class);
                startActivity(intent);
            });
        }
    }
}