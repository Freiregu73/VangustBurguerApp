package com.example.vangustapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class DetalhesPedidosActivity extends AppCompatActivity {

    private TextView tvTituloPedidoDetalhes, tvEnderecoDetalhes, tvItensDetalhes, tvTotalDetalhes;
    private MaterialButton btnPedirNovamente;
    private ImageView btnVoltarDetalhes;
    private PedidoModel pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_pedidos_layout);

        // 1. Ligar aos IDs do XML
        tvTituloPedidoDetalhes = findViewById(R.id.tvTituloPedidoDetalhes);
        tvEnderecoDetalhes = findViewById(R.id.tvEnderecoDetalhes);
        tvItensDetalhes = findViewById(R.id.tvItensDetalhes);
        tvTotalDetalhes = findViewById(R.id.tvTotalDetalhes);
        btnPedirNovamente = findViewById(R.id.btnPedirNovamente);
        btnVoltarDetalhes = findViewById(R.id.btnVoltarDetalhes);

        // 2. Recuperar o objeto PedidoModel enviado pelo Adapter
        if (getIntent().hasExtra("pedido_obj")) {
            pedido = (PedidoModel) getIntent().getSerializableExtra("pedido_obj");
        }

        // 3. Preencher os campos no ecrã com os dados reais
        if (pedido != null) {
            tvEnderecoDetalhes.setText(pedido.getEndereco() + "\nPagamento: " + pedido.getPagamento());
            tvTotalDetalhes.setText(String.format("R$ %.2f", pedido.getTotal()));
            tvTituloPedidoDetalhes.setText("Detalhes do Pedido");

            // Listar os itens detalhadamente
            StringBuilder sbItens = new StringBuilder();
            for (ItemCarrinho item : pedido.getItens()) {
                sbItens.append(item.getQuantidade()).append("x ").append(item.getTitulo())
                        .append(" - R$ ").append(String.format("%.2f", item.getPreco() * item.getQuantidade()))
                        .append("\n");
            }
            tvItensDetalhes.setText(sbItens.toString().trim());
        }

        // 4. Botão de voltar atrás
        if (btnVoltarDetalhes != null) {
            btnVoltarDetalhes.setOnClickListener(v -> finish());
        }

        // 5. Ação do botão "Adicionar ao Carrinho"
        btnPedirNovamente.setOnClickListener(v -> {
            if (pedido != null && pedido.getItens() != null) {
                for (ItemCarrinho item : pedido.getItens()) {
                    CarrinhoManager.adicionarItem(item);
                }
                Toast.makeText(this, "Itens adicionados ao carrinho!", Toast.LENGTH_SHORT).show();

                // Abre a MainActivity indicando para abrir o CarrinhoFragment
                android.content.Intent intent = new android.content.Intent(this, MenuPrincipalActivity.class);
                intent.putExtra("abrir_carrinho", true);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}