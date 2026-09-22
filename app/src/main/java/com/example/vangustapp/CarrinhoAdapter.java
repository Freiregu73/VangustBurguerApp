package com.example.vangustapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.ViewHolder> {

    private List<ItemCarrinho> listaItens;
    private OnCarrinhoChangedListener listener;

    public interface OnCarrinhoChangedListener {
        void onCarrinhoChanged();
    }

    public void setOnCarrinhoChangedListener(OnCarrinhoChangedListener listener) {
        this.listener = listener;
    }

    public CarrinhoAdapter(List<ItemCarrinho> listaItens) {
        this.listaItens = listaItens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modelo_carrinho, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCarrinho item = listaItens.get(position);

        holder.tvTitulo.setText(item.getTitulo());
        holder.tvPreco.setText(String.format("R$ %.2f", item.getPreco() * item.getQuantidade()));
        holder.tvQuantidade.setText(String.valueOf(item.getQuantidade()));
        holder.imgItem.setImageResource(item.getImagem());

        // Botão para aumentar quantidade
        holder.btnAumentar.setOnClickListener(v -> {
            item.setQuantidade(item.getQuantidade() + 1);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onCarrinhoChanged();
            }
        });

        // Botão para diminuir quantidade
        holder.btnDiminuir.setOnClickListener(v -> {
            if (item.getQuantidade() > 1) {
                item.setQuantidade(item.getQuantidade() - 1);
                notifyItemChanged(position);
            } else {
                listaItens.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listaItens.size());
            }
            if (listener != null) {
                listener.onCarrinhoChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem, btnAumentar, btnDiminuir;
        TextView tvTitulo, tvPreco, tvQuantidade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItemCarrinho);
            tvTitulo = itemView.findViewById(R.id.tvTituloCarrinho);
            tvPreco = itemView.findViewById(R.id.tvPrecoCarrinho);
            tvQuantidade = itemView.findViewById(R.id.tvQuantidadeCarrinho);
            btnAumentar = itemView.findViewById(R.id.btnAumentarQtd);
            btnDiminuir = itemView.findViewById(R.id.btnDiminuirQtd);
        }
    }
}