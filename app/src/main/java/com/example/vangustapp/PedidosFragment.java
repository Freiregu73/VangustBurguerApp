package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PedidosFragment extends Fragment {

    private RecyclerView idRecPedidos;
    private List<PedidoModel> listaPedidos;
    private AdapterPedidos adapterPedidos;

    public PedidosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idRecPedidos = view.findViewById(R.id.idRecPedidos);
        idRecPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPedidos = new ArrayList<>();
        adapterPedidos = new AdapterPedidos(listaPedidos, getContext());
        idRecPedidos.setAdapter(adapterPedidos);

        carregarPedidosDaApi();
    }

    private void carregarPedidosDaApi() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        if (idCliente == -1) {
            Toast.makeText(getContext(), "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Atualizado com o domínio oficial da Locaweb
        String url = "https://vangustburguer.com.br/api_hamburgueria/listar_pedidos_cliente.php?id_cliente=" + idCliente;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean sucesso = response.getBoolean("sucesso");
                        if (sucesso) {
                            JSONArray jsonArrayPedidos = response.getJSONArray("pedidos");
                            listaPedidos.clear();

                            for (int i = 0; i < jsonArrayPedidos.length(); i++) {
                                JSONObject obj = jsonArrayPedidos.getJSONObject(i);

                                int idPedido = obj.optInt("id_pedido", obj.optInt("numero_exibicao", 0));
                                String dataCriacao = obj.optString("criado_em", "");
                                double valorTotal = obj.getDouble("valor_total_pedido");
                                String status = obj.optString("status_pedido", "Pendente");
                                String pagamento = obj.optString("metodo_pagamento", "");

                                String enderecoCompleto = obj.optString("endereco_completo",
                                        obj.optString("endereco", "Endereço não especificado"));

                                List<ItemCarrinho> itensPedido = new ArrayList<>();
                                if (obj.has("itens")) {
                                    JSONArray jsonArrayItens = obj.getJSONArray("itens");
                                    for (int j = 0; j < jsonArrayItens.length(); j++) {
                                        JSONObject itemObj = jsonArrayItens.getJSONObject(j);
                                        String nomeProduto = itemObj.getString("nome_produto");
                                        double precoUnitario = itemObj.getDouble("preco_unitario_cobrado");
                                        int quantidade = itemObj.getInt("quantidade");

                                        itensPedido.add(new ItemCarrinho(nomeProduto, precoUnitario, quantidade, ""));
                                    }
                                }

                                PedidoModel pedido = new PedidoModel(idPedido, dataCriacao, enderecoCompleto, pagamento, valorTotal, status, itensPedido);
                                listaPedidos.add(pedido);
                            }
                            adapterPedidos.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Erro ao carregar histórico", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar dados dos pedidos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
}