package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlterarSenhaActivity extends AppCompatActivity {

    private TextInputEditText txSenhaAtual, txNovaSenha;
    private Button btnSalvarNovaSenha;
    private ImageView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alterar_senha);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_alterar_senha), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txSenhaAtual = findViewById(R.id.txSenhaAtual);
        txNovaSenha = findViewById(R.id.txNovaSenha);
        btnSalvarNovaSenha = findViewById(R.id.btnSalvarNovaSenha);
        btnVoltar = findViewById(R.id.btnVoltarAlterarSenha);

        btnVoltar.setOnClickListener(v -> finish());

        btnSalvarNovaSenha.setOnClickListener(v -> {
            String senhaAtual = txSenhaAtual.getText().toString().trim();
            String novaSenha = txNovaSenha.getText().toString().trim();

            if (senhaAtual.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else if (novaSenha.length() < 6) {
                Toast.makeText(this, "A nova senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            } else {
                requisitarAlteracaoSenha(senhaAtual, novaSenha);
            }
        });
    }

    private void requisitarAlteracaoSenha(String senhaAtual, String novaSenha) {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        if (idCliente == -1) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Atualizado com o domínio oficial da Locaweb
        String url = "https://vangustburguer.com.br/api_hamburgueria/alterar_senha.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");
                        String mensagem = jsonObject.optString("mensagem", "Operação concluída");

                        if (sucesso) {
                            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("ERRO_PHP_BRUTO", "Resposta recebida: " + response);
                        Toast.makeText(this, "Erro interno. Veja o Logcat (ERRO_PHP_BRUTO)", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("VOLLEY_ERRO", "Detalhe do erro de rede/servidor: ", error);
                    Toast.makeText(this, "Erro de conexão ou Erro 500 no servidor", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cliente", String.valueOf(idCliente));
                params.put("senha_atual", senhaAtual);
                params.put("nova_senha", novaSenha);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}