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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        // 1. Ligar as variáveis aos IDs do layout
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

            if (nomeLocal.isEmpty() || rua.isEmpty() || numero.isEmpty() || bairro.isEmpty() || cep.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            } else {
                salvarEnderecoNaApi(nomeLocal, cep, rua, numero, complemento, bairro);
            }
        });

        ImageView btnVoltarNovoEndereco = findViewById(R.id.btnVoltarNovoEndereco);
        if (btnVoltarNovoEndereco != null) {
            btnVoltarNovoEndereco.setOnClickListener(v -> finish());
        }
    }

    private void salvarEnderecoNaApi(String nomeLocal, String cep, String rua, String numero, String complemento, String bairro) {
        // Recuperar o e-mail do utilizador logado
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String emailUsuario = prefs.getString("email_usuario", "");

        if (emailUsuario.isEmpty()) {
            Toast.makeText(this, "Erro: Utilizador não identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/api_hamburgueria/salvar_endereco.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");

                        if (sucesso) {
                            Toast.makeText(this, "Endereço guardado na base de dados!", Toast.LENGTH_SHORT).show();

                            // Também atualizamos o SharedPreferences local para manter a agilidade no Checkout
                            String enderecoCompleto = nomeLocal + " - " + rua + ", " + numero;
                            if (!complemento.isEmpty()) {
                                enderecoCompleto += " (" + complemento + ")";
                            }
                            enderecoCompleto += " - " + bairro;

                            prefs.edit().putString("endereco_usuario", enderecoCompleto).apply();

                            finish(); // Fecha a tela e retorna
                        } else {
                            Toast.makeText(this, "Erro: " + jsonObject.getString("mensagem"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailUsuario);
                params.put("rua", rua);
                params.put("numero", numero);
                params.put("bairro", bairro);
                params.put("cep", cep);
                params.put("complemento", complemento);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}