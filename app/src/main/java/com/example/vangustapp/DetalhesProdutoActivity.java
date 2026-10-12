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
    private boolean isFavorito = false; // Controla o estado atual do favorito

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

        // Verificar se este produto já é favorito ao abrir a tela
        verificarStatusFavorito();

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

    // Método para consultar se o produto já consta nos favoritos do cliente
    private void verificarStatusFavorito() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        if (idCliente == -1 || idProduto == -1) return;

        // Endpoint PHP que verifica ou lista os favoritos do cliente (ou ajuste conforme o seu script existente)
        String url = "http://10.0.2.2/api_hamburgueria/verificar_favorito.php?id_cliente=" + idCliente + "&id_produto=" + idProduto;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        isFavorito = jsonObject.optBoolean("favorito", false);
                        atualizarVisualFavorito();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Silencia erro de rede na verificação inicial para não incomodar o utilizador
                }
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void alternarFavorito() {
        SharedPreferences prefs = getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        if (idCliente == -1) {
            Toast.makeText(this, "Erro de sessão: ID do cliente não encontrado.", Toast.LENGTH_SHORT).show();
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
                            // Inverte o estado local e atualiza o ícone do coração
                            isFavorito = !isFavorito;
                            atualizarVisualFavorito();
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

    // Altera o ícone do coração com base na variável 'isFavorito'
    private void atualizarVisualFavorito() {
        if (btnFavoritar != null) {
            if (isFavorito) {
                // Ícone do coração preenchido / ativo (vermelho ou cor de destaque)
                btnFavoritar.setImageResource(R.drawable.baseline_favorite); // Certifique-se de ter este drawable ou substitua pelo seu
            } else {
                // Ícone do coração vazio / inativo
                btnFavoritar.setImageResource(R.drawable.baseline_favorite_border); // Ou o nome do seu ícone vazio padrão
            }
        }
    }
}