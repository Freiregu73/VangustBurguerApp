package com.example.vangustapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalhesPedidosActivity extends AppCompatActivity {

    private TextView tvTituloPedidoDetalhes, tvStatusDetalhes, tvEnderecoDetalhes, tvItensDetalhes, tvTaxaEntregaDetalhes, tvTotalDetalhes;
    private MaterialButton btnPedirNovamente, btnCancelarPedidoDetalhes;
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
        btnCancelarPedidoDetalhes = findViewById(R.id.btnCancelarPedidoDetalhes);
        btnVoltarDetalhes = findViewById(R.id.btnVoltarDetalhes);

        if (getIntent().hasExtra("pedido_obj")) {
            pedido = (PedidoModel) getIntent().getSerializableExtra("pedido_obj");
        }

        if (pedido != null) {
            Log.d("DEBUG_PEDIDO", "ID do Pedido carregado com sucesso: " + pedido.getIdPedido());
            tvTituloPedidoDetalhes.setText("Pedido #" + pedido.getIdPedido());

            String statusAtual = pedido.getStatusPedido() != null ? pedido.getStatusPedido() : "Pendente";
            tvStatusDetalhes.setText(statusAtual);

            if (btnCancelarPedidoDetalhes != null) {
                if (statusAtual.equalsIgnoreCase("Entregue") || statusAtual.equalsIgnoreCase("Cancelado")) {
                    btnCancelarPedidoDetalhes.setVisibility(android.view.View.GONE);
                } else {
                    btnCancelarPedidoDetalhes.setVisibility(android.view.View.VISIBLE);
                }
            }

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
        } else {
            Log.e("DEBUG_PEDIDO", "Nenhum objeto de pedido foi recebido via Intent!");
        }

        if (btnVoltarDetalhes != null) {
            btnVoltarDetalhes.setOnClickListener(v -> finish());
        }

        if (btnCancelarPedidoDetalhes != null) {
            btnCancelarPedidoDetalhes.setOnClickListener(v -> {
                if (pedido != null && pedido.getIdPedido() > 0) {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Cancelar Pedido")
                            .setMessage("Tem certeza de que deseja cancelar este pedido?")
                            .setPositiveButton("Sim", (dialog, which) -> executarCancelamentoServidor(pedido.getIdPedido()))
                            .setNegativeButton("Não", null)
                            .show();
                } else {
                    Toast.makeText(this, "Erro: ID do pedido inválido.", Toast.LENGTH_SHORT).show();
                }
            });
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
                Toast.makeText(this, "Não foi possível carregar os itens.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executarCancelamentoServidor(int idPedido) {
        String url = "http://10.0.2.2/api_hamburgueria/cancelar_pedido.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");
                        if (sucesso) {
                            Toast.makeText(this, "Pedido cancelado com sucesso.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Erro: " + jsonObject.getString("mensagem"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao processar resposta.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão ao cancelar.", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_pedido", String.valueOf(idPedido));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}