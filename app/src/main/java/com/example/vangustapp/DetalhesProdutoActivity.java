package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ImageView imgProduto, btnFavoritar;
    private TextView txtTitulo, txtDescricao, txtPreco;
    private Button btnPedir;
    private int idProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.modelo_detalhes_produtos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modeloCardProdutos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes visuais
        imgProduto = findViewById(R.id.modeloImagemProdutos);
        txtTitulo = findViewById(R.id.modeloTituloProdutos);
        txtDescricao = findViewById(R.id.modeloDescricaoProdutos);
        txtPreco = findViewById(R.id.modeloPrecoProdutos);
        btnPedir = findViewById(R.id.btnPedirProduto);
        btnFavoritar = findViewById(R.id.btnFavoritarDetalhes);

        // Receber os dados enviados pelo Intent
        idProduto = getIntent().getIntExtra("id_produto", -1);
        String titulo = getIntent().getStringExtra("titulo");
        String descricao = getIntent().getStringExtra("descricao");
        double preco = getIntent().getDoubleExtra("preco", 0.0);
        String urlImagem = getIntent().getStringExtra("imagem");

        // Preencher os dados no ecrã
        txtTitulo.setText(titulo);
        txtDescricao.setText(descricao);
        txtPreco.setText(String.format("R$ %.2f", preco));

        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(this)
                    .load(urlImagem)
                    .placeholder(R.drawable.brutao)
                    .error(R.drawable.brutao)
                    .into(imgProduto);
        } else {
            imgProduto.setImageResource(R.drawable.brutao);
        }

        // Ação do Botão Favoritar
        if (btnFavoritar != null) {
            btnFavoritar.setOnClickListener(v -> alternarFavorito());
        }

        // Ação do botão "Pedir"
        btnPedir.setOnClickListener(v -> {
            ItemCarrinho itemCarrinho = new ItemCarrinho(titulo, preco, 1, urlImagem != null ? urlImagem : "");
            CarrinhoManager.adicionarItem(itemCarrinho);

            Toast.makeText(this, titulo + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            finish();
        });

        ImageView btnVoltar = findViewById(R.id.btnVoltarDetalhesProduto);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish());
        }
    }

    private void alternarFavorito() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        // Fallback: se por acaso o id_cliente estiver como -1, mas houver um e-mail salvo,
        // podemos avisar ou tentar tratar para não bloquear o utilizador
        if (idCliente == -1) {
            String emailSalvo = prefs.getString("email_usuario", "");
            Toast.makeText(this, "Erro de sessão: ID do cliente não encontrado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            return;
        }

        if (idProduto == -1) {
            Toast.makeText(this, "Erro: ID do produto não identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/api_hamburgueria/favoritar.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.optBoolean("sucesso", false);
                        String mensagem = jsonObject.optString("mensagem", "Ação realizada");

                        if (sucesso) {
                            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Erro: " + mensagem, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_cliente", String.valueOf(idCliente));
                params.put("id_produto", String.valueOf(idProduto));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}