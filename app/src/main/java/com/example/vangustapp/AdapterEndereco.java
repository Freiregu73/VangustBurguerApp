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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Ação do botão de excluir endereço
        holder.btnExcluirEndereco.setOnClickListener(v -> {
            int idEndereco = endereco.getIdEndereco();

            // Chamada à API para apagar na base de dados
            excluirEnderecoNaApi(idEndereco, position);
        });
    }

    private void excluirEnderecoNaApi(int idEndereco, int position) {
        String url = "https://vangustburguer.com.br/api_hamburgueria/excluir_endereco.php";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean sucesso = jsonObject.getBoolean("sucesso");

                        if (sucesso) {
                            // Remove da lista local do RecyclerView com segurança
                            if (position >= 0 && position < listaEnderecos.size()) {
                                listaEnderecos.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, listaEnderecos.size());
                            }

                            // Se apagou todos os endereços, limpa o SharedPreferences do checkout
                            if (listaEnderecos.isEmpty()) {
                                android.content.SharedPreferences prefs = context.getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                                prefs.edit().remove("endereco_usuario").apply();
                            }

                            Toast.makeText(context, "Endereço excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erro: " + jsonObject.getString("mensagem"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_endereco", String.valueOf(idEndereco));
                return params;
            }
        };

        VolleySingleton.getInstance(context).add(stringRequest);
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