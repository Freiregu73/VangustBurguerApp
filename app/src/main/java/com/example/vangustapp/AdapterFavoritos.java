package com.example.vangustapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        holder.modeloPrecoFavoritos.setText(String.valueOf(favorito.getPreco()));
        holder.modeloImagemFavoritos.setImageResource(favorito.getImgfavoritos());
    }

    @Override
    public int getItemCount() {
        return lstfavoritos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView modeloCardFavoritos;
        ImageView modeloImagemFavoritos;
        TextView modeloTituloFavoritos, modeloDescFavoritos, modeloPrecoFavoritos;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modeloImagemFavoritos = itemView.findViewById(R.id.modeloImagemFavoritos);
            modeloTituloFavoritos = itemView.findViewById(R.id.modeloTituloFavoritos);
            modeloDescFavoritos = itemView.findViewById(R.id.modeloDescFavoritos);
            modeloPrecoFavoritos = itemView.findViewById(R.id.modeloPrecoFavoritos);
        }
    }

}
