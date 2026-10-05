package com.example.vangustapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterEndereco extends RecyclerView.Adapter<AdapterEndereco.ViewHolder> {

    private List<EnderecoModel> listaEnderecos;
    private Context context;

    public AdapterEndereco(List<EnderecoModel> listaEnderecos, Context context) {
        this.listaEnderecos = listaEnderecos;
        this.context = context;
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

        // Ação do botão de excluir endereço da lista
        holder.btnExcluirEndereco.setOnClickListener(v -> {
            listaEnderecos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listaEnderecos.size());
            Toast.makeText(context, "Endereço removido", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaEnderecos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloEndereco, tvRuaEndereco;
        ImageView btnExcluirEndereco;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloEndereco = itemView.findViewById(R.id.tvTituloEndereco);
            tvRuaEndereco = itemView.findViewById(R.id.tvRuaEndereco);
            btnExcluirEndereco = itemView.findViewById(R.id.btnExcluirEndereco);
        }
    }
}