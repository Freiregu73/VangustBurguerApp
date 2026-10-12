package com.example.vangustapp;

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

import com.bumptech.glide.Glide;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ImageView imgProduto;
    private TextView txtTitulo, txtDescricao, txtPreco;
    private Button btnPedir;

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

        // Receber os dados enviados pelo Intent
        String titulo = getIntent().getStringExtra("titulo");
        String descricao = getIntent().getStringExtra("descricao");
        double preco = getIntent().getDoubleExtra("preco", 0.0);

        // A imagem agora é recebida como String (URL da API)
        String urlImagem = getIntent().getStringExtra("imagem");

        // Preencher os dados no ecrã
        txtTitulo.setText(titulo);
        txtDescricao.setText(descricao);
        txtPreco.setText(String.format("R$ %.2f", preco));

        // Carregar a imagem com o Glide se houver URL, senão usa o fallback local
        if (urlImagem != null && !urlImagem.isEmpty()) {
            Glide.with(this)
                    .load(urlImagem)
                    .placeholder(R.drawable.brutao)
                    .error(R.drawable.brutao)
                    .into(imgProduto);
        } else {
            imgProduto.setImageResource(R.drawable.brutao);
        }

        // No botão btnPedir em DetalhesProdutoActivity.java:
        btnPedir.setOnClickListener(v -> {
            // Passar a variável urlImagem que recebeu do Intent
            ItemCarrinho itemCarrinho = new ItemCarrinho(titulo, preco, 1, urlImagem);
            CarrinhoManager.adicionarItem(itemCarrinho);

            Toast.makeText(this, titulo + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            finish();
        });

        ImageView btnVoltar = findViewById(R.id.btnVoltarDetalhesProduto);
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(v -> finish()); // Fecha a activity atual
        }
    }
}