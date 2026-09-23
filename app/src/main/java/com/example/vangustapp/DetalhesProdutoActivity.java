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
        int imagem = getIntent().getIntExtra("imagem", R.drawable.brutao);

        // Preencher os dados no ecrã
        txtTitulo.setText(titulo);
        txtDescricao.setText(descricao);
        txtPreco.setText(String.format("%.2f", preco));
        imgProduto.setImageResource(imagem);

        // Ação do botão "Pedir"
        btnPedir.setOnClickListener(v -> {
            // Cria o item com os dados atuais do produto e adiciona ao gestor global
            ItemCarrinho itemCarrinho = new ItemCarrinho(titulo, preco, 1, imagem);
            CarrinhoManager.adicionarItem(itemCarrinho);

            Toast.makeText(this, titulo + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a tela de detalhes após pedir
        });
    }
}