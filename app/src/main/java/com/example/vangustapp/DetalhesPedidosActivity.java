package com.example.vangustapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class DetalhesPedidosActivity extends AppCompatActivity {

    private TextView tvTituloPedidoDetalhes, tvStatusDetalhes, tvEnderecoDetalhes, tvItensDetalhes, tvTaxaEntregaDetalhes, tvTotalDetalhes;
    private MaterialButton btnPedirNovamente;
    private ImageView btnVoltarDetalhes;
    private PedidoModel pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_pedidos_layout);

        tvTituloPedidoDetalhes = findViewById(R.id.tvTituloPedidoDetalhes);
        tvStatusDetalhes = findViewById(R.id.tvStatusDetalhes);
        tvEnderecoDetalhes = findViewById(R.id.tvEnderecoDetalhes);
        tvItensDetalhes = findViewById(R.id.tvItensDetalhes);
        tvTaxaEntregaDetalhes = findViewById(R.id.tvTaxaEntregaDetalhes);
        tvTotalDetalhes = findViewById(R.id.tvTotalDetalhes);
        btnPedirNovamente = findViewById(R.id.btnPedirNovamente);
        btnVoltarDetalhes = findViewById(R.id.btnVoltarDetalhes);

        if (getIntent().hasExtra("pedido_obj")) {
            pedido = (PedidoModel) getIntent().getSerializableExtra("pedido_obj");
        }

        if (pedido != null) {
            tvTituloPedidoDetalhes.setText("Pedido #" + pedido.getIdPedido());

            if (tvStatusDetalhes != null) {
                tvStatusDetalhes.setText(pedido.getStatusPedido() != null ? pedido.getStatusPedido() : "Pendente");
            }

            // Exibe o endereço corretamente na tela de detalhes
            String enderecoEntrega = (pedido.getEndereco() != null && !pedido.getEndereco().isEmpty())
                    ? pedido.getEndereco()
                    : "Endereço não especificado";

            String infoEntrega = "Endereço: " + enderecoEntrega +
                    "\nPagamento: " + (pedido.getPagamento() != null ? pedido.getPagamento() : "Não informado") +
                    "\nData: " + (pedido.getDataCriacao() != null ? pedido.getDataCriacao() : "Recente");

            tvEnderecoDetalhes.setText(infoEntrega);

            if (tvTaxaEntregaDetalhes != null) {
                tvTaxaEntregaDetalhes.setText("R$ 5,00");
            }

            tvTotalDetalhes.setText(String.format("R$ %.2f", pedido.getTotal()));

            StringBuilder sbItens = new StringBuilder();
            if (pedido.getItens() != null) {
                for (ItemCarrinho item : pedido.getItens()) {
                    sbItens.append("• ").append(item.getQuantidade()).append("x ").append(item.getTitulo())
                            .append(" - R$ ").append(String.format("%.2f", item.getPreco() * item.getQuantidade()))
                            .append("\n");
                }
            }
            tvItensDetalhes.setText(sbItens.toString().trim());
        }

        if (btnVoltarDetalhes != null) {
            btnVoltarDetalhes.setOnClickListener(v -> finish());
        }

        btnPedirNovamente.setOnClickListener(v -> {
            if (pedido != null && pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemCarrinho item : pedido.getItens()) {
                    CarrinhoManager.adicionarItem(item);
                }
                Toast.makeText(this, "Itens adicionados ao carrinho!", Toast.LENGTH_SHORT).show();

                android.content.Intent intent = new android.content.Intent(this, MenuPrincipalActivity.class);
                intent.putExtra("abrir_carrinho", true);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Não foi possível carregar os itens deste pedido.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}