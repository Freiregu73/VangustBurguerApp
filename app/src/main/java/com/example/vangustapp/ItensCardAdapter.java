package com.example.vangustapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItensCardAdapter extends RecyclerView.Adapter<ItensCardAdapter.ViewHolder> {

    private List<ItensCard> listaItens;
    private OnItemClickListener listener;

    // Interface para o clique
    public interface OnItemClickListener {
        void onItemClick(ItensCard item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ItensCardAdapter(List<ItensCard> listaItens) {
        this.listaItens = listaItens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modelo_itens_burguer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItensCard item = listaItens.get(position);

        holder.textTitulo.setText(item.getTitulo());
        holder.imageItem.setImageResource(item.getImgitens());

        // Ação ao clicar no cartão do hambúrguer
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo;
        ImageView imageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.modeloTituloItens);
            imageItem = itemView.findViewById(R.id.modeloImagemItens);
        }
    }
}