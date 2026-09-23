package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterFavoritos extends RecyclerView.Adapter<AdapterFavoritos.ViewHolder> {

    private List<FavoritosCard> lstfavoritos;
    private Context context;

    public AdapterFavoritos(List<FavoritosCard> lstfavoritos, Context context) {
        this.lstfavoritos = lstfavoritos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_favoritos_burguer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoritosCard favorito = lstfavoritos.get(position);

        holder.modeloTituloFavoritos.setText(favorito.getTitulo());
        holder.modeloDescFavoritos.setText(favorito.getDescricao());
        holder.modeloPrecoFavoritos.setText(String.format("R$ %.2f", favorito.getPreco()));
        holder.modeloImagemFavoritos.setImageResource(favorito.getImgfavoritos());

        // --- AÇÃO DO BOTÃO "PEDIR" NO CARD DE FAVORITOS ---
        holder.modeloBtnFavoritos.setOnClickListener(v -> {
            ItemCarrinho itemCarrinho = new ItemCarrinho(
                    favorito.getTitulo(),
                    favorito.getPreco(),
                    1,
                    favorito.getImgfavoritos()
            );
            CarrinhoManager.adicionarItem(itemCarrinho);

            Toast.makeText(context, favorito.getTitulo() + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
        });

        // --- CLIQUE NO CARD PARA ABRIR OS DETALHES ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesProdutoActivity.class);
            intent.putExtra("titulo", favorito.getTitulo());
            intent.putExtra("descricao", favorito.getDescricao());
            intent.putExtra("preco", favorito.getPreco());
            intent.putExtra("imagem", favorito.getImgfavoritos());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lstfavoritos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView modeloCardFavoritos;
        ImageView modeloImagemFavoritos;
        TextView modeloTituloFavoritos, modeloDescFavoritos, modeloPrecoFavoritos;
        Button modeloBtnFavoritos; // Referência correta para o botão do XML

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modeloCardFavoritos = itemView.findViewById(R.id.modeloCardFavoritos);
            modeloImagemFavoritos = itemView.findViewById(R.id.modeloImagemFavoritos);
            modeloTituloFavoritos = itemView.findViewById(R.id.modeloTituloFavoritos);
            modeloDescFavoritos = itemView.findViewById(R.id.modeloDescFavoritos);
            modeloPrecoFavoritos = itemView.findViewById(R.id.modeloPrecoFavoritos);
            modeloBtnFavoritos = itemView.findViewById(R.id.modeloBtnFavoritos); // ID exato do teu XML
        }
    }
}