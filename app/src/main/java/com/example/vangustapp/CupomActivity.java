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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CupomActivity extends AppCompatActivity {

    private RecyclerView idRecCupons;
    private TextInputEditText txInserirCupom;
    private MaterialButton btnAplicarCupom;
    private AdapterCupom adapter;
    private List<CupomModel> listaCupons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_cupom);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        idRecCupons = findViewById(R.id.idRecCupons);
        txInserirCupom = findViewById(R.id.txInserirCupom);
        btnAplicarCupom = findViewById(R.id.btnAplicarCupom);

        idRecCupons.setLayoutManager(new LinearLayoutManager(this));
        listaCupons = new ArrayList<>();

        // Carregar cupons disponíveis da base de dados MySQL
        carregarCuponsDaApi();

        // Botão voltar
        ImageView btnVoltarCupom = findViewById(R.id.btnVoltarCupom);
        if (btnVoltarCupom != null) {
            btnVoltarCupom.setOnClickListener(v -> finish());
        }

        // Ação do botão "Aplicar" cupom digitado manualmente
        btnAplicarCupom.setOnClickListener(v -> {
            String codigoDigitado = txInserirCupom.getText().toString().trim().toUpperCase();

            if (codigoDigitado.isEmpty()) {
                Toast.makeText(this, "Digite um código de cupom!", Toast.LENGTH_SHORT).show();
            } else {
                validarCupomNaApi(codigoDigitado);
            }
        });
    }

    private void carregarCuponsDaApi() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", 0);

        // Passa o id_cliente como parâmetro GET para o PHP filtrar
        String url = "http://10.0.2.2/api_hamburgueria/listar_cupons.php?id_cliente=" + idCliente;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("sucesso")) {
                            JSONArray jsonArray = response.getJSONArray("cupons");
                            listaCupons.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String codigo = obj.getString("codigo_cupom");
                                String descricao = obj.optString("descricao_cupom", "Desconto especial");
                                double valorDesconto = obj.getDouble("valor_desconto");

                                // Adiciona à lista local apenas os que o PHP permitiu
                                listaCupons.add(new CupomModel(codigo, descricao, valorDesconto));
                            }

                            adapter = new AdapterCupom(listaCupons, this);
                            idRecCupons.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar cupons", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void validarCupomNaApi(String codigo) {
        String url = "http://10.0.2.2/api_hamburgueria/validar_cupom.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");

                        if (sucesso) {
                            String codigoValido = jsonObject.getString("codigo");
                            double valorDesconto = jsonObject.getDouble("valor_desconto");

                            SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                            boolean jaFoiUsado = prefs.getBoolean("usado_" + codigoValido, false);

                            if (jaFoiUsado) {
                                Toast.makeText(this, "Este cupom já foi utilizado por si!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Guarda o cupom ativo e o valor do desconto no SharedPreferences para o Carrinho/Checkout usarem
                            prefs.edit()
                                    .putString("cupom_ativo", codigoValido)
                                    .putFloat("desconto_ativo", (float) valorDesconto)
                                    .apply();

                            Toast.makeText(this, "Cupom " + codigoValido + " aplicado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, jsonObject.optString("mensagem", "Cupom inválido"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão ao validar cupom", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", codigo);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarCuponsDaApi();
    }
}