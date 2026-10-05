package com.example.vangustapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdapterCupom extends RecyclerView.Adapter<AdapterCupom.ViewHolder> {

    private List<CupomModel> listaCupons;
    private Context context;

    public AdapterCupom(List<CupomModel> listaCupons, Context context) {
        this.listaCupons = listaCupons;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_cupom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CupomModel cupom = listaCupons.get(position);

        holder.tvValorCupom.setText(cupom.getCodigo());
        holder.tvDescricaoCupom.setText(cupom.getDescricao());

        // Ação ao clicar em "USAR" o cupom
        holder.btnUsarCupom.setOnClickListener(v -> {
            // Guardar o cupom ativo no SharedPreferences para aplicar no Carrinho/Checkout
            android.content.SharedPreferences prefs = context.getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
            prefs.edit().putString("cupom_ativo", cupom.getCodigo()).putFloat("desconto_ativo", (float) cupom.getValorDesconto()).apply();

            Toast.makeText(context, "Cupom " + cupom.getCodigo() + " aplicado com sucesso!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaCupons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValorCupom, tvDescricaoCupom;
        MaterialButton btnUsarCupom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValorCupom = itemView.findViewById(R.id.tvValorCupom);
            tvDescricaoCupom = itemView.findViewById(R.id.tvDescricaoCupom);
            btnUsarCupom = itemView.findViewById(R.id.btnUsarCupom);
        }
    }
}