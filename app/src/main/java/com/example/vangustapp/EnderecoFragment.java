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
            Toast.makeText(getContext(), "Utilizador não identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/api_hamburgueria/listar_enderecos.php?email=" + emailUsuario;

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
                                String ruaNum = endObj.getString("rua") + ", " + endObj.getString("numero");
                                String bairroCep = "Bairro: " + endObj.getString("bairro") + " | CEP: " + endObj.getString("cep");

                                listaEnderecos.add(new EnderecoModel(ruaNum, bairroCep));
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
        // Atualiza a lista sempre que voltar para este fragmento (ex: após cadastrar um novo)
        carregarEnderecosDaApi();
    }
}