package com.example.vangustapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterEnderecoDialog extends RecyclerView.Adapter<AdapterEnderecoDialog.ViewHolder> {

    private List<EnderecoModel> listaEnderecos;
    private Context context;
    private OnEnderecoClickListener listener;

    public interface OnEnderecoClickListener {
        void onEnderecoClick(EnderecoModel endereco);
    }

    public AdapterEnderecoDialog(List<EnderecoModel> listaEnderecos, Context context, OnEnderecoClickListener listener) {
        this.listaEnderecos = listaEnderecos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_endereco, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EnderecoModel endereco = listaEnderecos.get(position);

        holder.tvTituloEndereco.setText(endereco.getTitulo());
        holder.tvRuaEndereco.setText(endereco.getRua());

        // Esconder o botão de excluir dentro do diálogo de seleção
        if (holder.btnExcluirEndereco != null) {
            holder.btnExcluirEndereco.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEnderecoClick(endereco);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEnderecos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloEndereco, tvRuaEndereco;
        android.widget.ImageView btnExcluirEndereco;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloEndereco = itemView.findViewById(R.id.tvTituloEndereco);
            tvRuaEndereco = itemView.findViewById(R.id.tvRuaEndereco);
            btnExcluirEndereco = itemView.findViewById(R.id.btnExcluirEndereco);
        }
    }
}