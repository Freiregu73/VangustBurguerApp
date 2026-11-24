package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EnderecoFragment extends Fragment {

    private RecyclerView idRecEndereco;
    private List<EnderecoModel> listaEnderecos;
    private AdapterEndereco adapterEndereco;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_endereco, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idRecEndereco = view.findViewById(R.id.idRecEndereco);
        idRecEndereco.setLayoutManager(new LinearLayoutManager(getContext()));

        listaEnderecos = new ArrayList<>();
        adapterEndereco = new AdapterEndereco(listaEnderecos, getContext());
        idRecEndereco.setAdapter(adapterEndereco);

        // Carregar endereços da API MySQL
        carregarEnderecosDaApi();

        // Botão flutuante para adicionar novo endereço
        FloatingActionButton fab = view.findViewById(R.id.fabAdicionarEndereco);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NovoEndereco.class);
                startActivity(intent);
            });
        }
    }

    private void carregarEnderecosDaApi() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String emailUsuario = prefs.getString("email_usuario", "");

        if (emailUsuario.isEmpty()) {
            Toast.makeText(getContext(), "Usuário não identificado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://vangustburguer.com.br/api_hamburgueria/listar_enderecos.php?email=" + emailUsuario;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean sucesso = response.getBoolean("sucesso");
                        if (sucesso) {
                            JSONArray jsonArray = response.getJSONArray("enderecos");
                            listaEnderecos.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject endObj = jsonArray.getJSONObject(i);

                                // Captura o ID real do endereço vindo do banco de dados
                                int idEndereco = endObj.optInt("id_endereco", 0);
                                String rua = endObj.getString("rua");
                                String numero = endObj.getString("numero");
                                String bairro = endObj.getString("bairro");
                                String cep = endObj.getString("cep");

                                String ruaNum = rua + ", " + numero;
                                String bairroCep = "Bairro: " + bairro + " | CEP: " + cep;

                                // Passa o idEndereco para o construtor do EnderecoModel
                                listaEnderecos.add(new EnderecoModel(idEndereco, ruaNum, bairroCep));

                                // Se o utilizador já tem endereços na base de dados,
                                // guardamos o primeiro automaticamente no SharedPreferences para liberar a compra
                                if (i == 0) {
                                    String enderecoCompleto = ruaNum + " - " + bairro;
                                    prefs.edit().putString("endereco_usuario", enderecoCompleto).apply();
                                }
                            }

                            // Se por acaso a lista estiver vazia no servidor, removemos o endereço local
                            if (jsonArray.length() == 0) {
                                prefs.edit().remove("endereco_usuario").apply();
                            }

                            adapterEndereco.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Erro ao carregar endereços", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar dados de endereços", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarEnderecosDaApi();
    }
}