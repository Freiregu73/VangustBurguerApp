package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.ViewHolder> {

    private List<PedidoModel> listaPedidos;
    private Context context;

    public AdapterPedidos(List<PedidoModel> listaPedidos, Context context) {
        this.listaPedidos = listaPedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PedidoModel pedido = listaPedidos.get(position);

        // Número do pedido real vindo do banco de dados
        holder.tvNumeroPedido.setText("Pedido #" + pedido.getIdPedido());

        // Exibindo o Status junto com a data ou substituindo para ver o andamento do painel
        String statusOuData = (pedido.getStatusPedido() != null && !pedido.getStatusPedido().isEmpty())
                ? pedido.getStatusPedido()
                : (pedido.getDataCriacao() != null ? pedido.getDataCriacao() : "Recente");
        holder.tvDataPedido.setText(statusOuData);

        // Montar o texto com o resumo dos itens comprados
        StringBuilder resumoItens = new StringBuilder();
        if (pedido.getItens() != null) {
            for (int i = 0; i < pedido.getItens().size(); i++) {
                ItemCarrinho item = pedido.getItens().get(i);
                resumoItens.append(item.getQuantidade()).append("x ").append(item.getTitulo());
                if (i < pedido.getItens().size() - 1) {
                    resumoItens.append(", ");
                }
            }
        }
        holder.tvResumoItens.setText(resumoItens.toString());

        // Preço total formatado exatamente como no layout
        holder.tvTotalPedido.setText(String.format("%.2f", pedido.getTotal()));

        // --- CLIQUE NO CARD: Abre os detalhes passando o objeto completo ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesPedidosActivity.class);
            intent.putExtra("pedido_obj", pedido);
            context.startActivity(intent);
        });

        // --- AÇÃO DO BOTÃO "REPETIR" NA LISTA ---
        holder.btnRepetirPedido.setOnClickListener(v -> {
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemCarrinho item : pedido.getItens()) {
                    CarrinhoManager.adicionarItem(item);
                }
                Toast.makeText(context, "Itens adicionados ao carrinho!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, MenuPrincipalActivity.class);
                intent.putExtra("abrir_carrinho", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Não foi possível carregar os itens deste pedido.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroPedido, tvDataPedido, tvResumoItens, tvTotalPedido;
        MaterialButton btnRepetirPedido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroPedido = itemView.findViewById(R.id.tvNumeroPedido);
            tvDataPedido = itemView.findViewById(R.id.tvDataPedido);
            tvResumoItens = itemView.findViewById(R.id.tvResumoItens);
            tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            btnRepetirPedido = itemView.findViewById(R.id.btnRepetirPedido);
        }
    }
}