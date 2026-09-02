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

    // Construtor que recebe a lista de hambúrgueres
    public ItensCardAdapter(List<ItensCard> listaItens) {
        this.listaItens = listaItens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lembre-se de trocar "nome_do_seu_arquivo_xml_aqui" pelo nome real do seu layout de card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modelo_itens_burguer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItensCard item = listaItens.get(position);

        holder.textTitulo.setText(item.getTitulo());
        holder.imageItem.setImageResource(item.getImgitens());
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
            // Estes são os IDs que você configurou no seu XML
            textTitulo = itemView.findViewById(R.id.modeloTituloItens);
            imageItem = itemView.findViewById(R.id.modeloImagemItens);
        }
    }
}