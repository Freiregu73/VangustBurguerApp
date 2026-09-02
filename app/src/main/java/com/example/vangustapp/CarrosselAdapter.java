package com.example.vangustapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarrosselAdapter extends RecyclerView.Adapter<CarrosselAdapter.CarrosselViewHolder> {

    private List<Integer> listaImagens;

    public CarrosselAdapter(List<Integer> listaImagens) {
        this.listaImagens = listaImagens;
    }

    @NonNull
    @Override
    public CarrosselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_carrossel, parent, false);
        return new CarrosselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrosselViewHolder holder, int position) {
        holder.imageView.setImageResource(listaImagens.get(position));
    }

    @Override
    public int getItemCount() {
        return listaImagens.size();
    }

    public static class CarrosselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CarrosselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgCarrossel);
        }
    }
}