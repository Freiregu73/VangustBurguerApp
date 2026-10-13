package com.example.vangustapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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

            // Atualizado com o domínio oficial da Locaweb
            String url = "https://vangustburguer.com.br/api_hamburgueria/login.php";

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST, url,
                    response -> {
                        Log.e("HTML_ERRO_SERVIDOR", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean sucesso = jsonObject.getBoolean("sucesso");

                            if (sucesso) {
                                JSONObject usuarioObj = jsonObject.getJSONObject("usuario");

                                int idCliente = usuarioObj.has("id") ? usuarioObj.getInt("id") : usuarioObj.optInt("id_usuario", -1);
                                String nomeUsuario = usuarioObj.getString("nome");
                                String fotoBase64 = jsonObject.optString("foto", "");

                                SharedPreferences preferences = getSharedPreferences("VangustPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear(); // Limpa dados residuais de contas anteriores
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
                            } else {
                                Toast.makeText(LoginActivity.this, jsonObject.optString("erro", "E-mail ou senha incorretos"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erro JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(LoginActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", emailDigitado);
                    params.put("senha", senhaDigitada);
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
        });

        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        if (linkEsqueceuSenha != null) {
            linkEsqueceuSenha.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, EsqueciSenhaActivity.class);
                startActivity(intent);
            });
        }
    }
}