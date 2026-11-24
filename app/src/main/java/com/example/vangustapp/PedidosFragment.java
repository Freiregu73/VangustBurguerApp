package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PedidosFragment extends Fragment {

    // De quanto em quanto tempo a lista se atualiza sozinha enquanto esta tela está aberta
    private static final long INTERVALO_ATUALIZACAO_MS = 10000;

    private RecyclerView idRecPedidos;
    private List<PedidoModel> listaPedidos;
    private AdapterPedidos adapterPedidos;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean primeiraCarga = true;
    private String ultimaAssinatura = null;

    private final Runnable atualizador = new Runnable() {
        @Override
        public void run() {
            carregarPedidosDaApi();
            handler.postDelayed(this, INTERVALO_ATUALIZACAO_MS);
        }
    };

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
        // A primeira carga é feita no onResume, que também liga a atualização automática
    }

    @Override
    public void onResume() {
        super.onResume();
        // Carrega agora e repete a cada 10 segundos enquanto a tela estiver visível
        handler.removeCallbacks(atualizador);
        handler.post(atualizador);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(atualizador);
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(atualizador);
        super.onDestroyView();
    }

    private void avisar(String mensagem) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarPedidosDaApi() {
        if (!isAdded()) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        if (idCliente == -1) {
            if (primeiraCarga) {
                avisar("Sessão expirada. Faça login novamente.");
                primeiraCarga = false;
            }
            return;
        }

        String url = "https://vangustburguer.com.br/api_hamburgueria/listar_pedidos_cliente.php?id_cliente=" + idCliente;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    // A tela pode ter sido fechada enquanto a resposta chegava
                    if (!isAdded() || adapterPedidos == null) return;

                    boolean eraPrimeiraCarga = primeiraCarga;
                    primeiraCarga = false;

                    try {
                        boolean sucesso = response.getBoolean("sucesso");
                        if (!sucesso) {
                            if (eraPrimeiraCarga) avisar("Erro ao carregar histórico");
                            return;
                        }

                        JSONArray jsonArrayPedidos = response.getJSONArray("pedidos");
                        List<PedidoModel> novaLista = new ArrayList<>();
                        StringBuilder assinatura = new StringBuilder();

                        for (int i = 0; i < jsonArrayPedidos.length(); i++) {
                            JSONObject obj = jsonArrayPedidos.getJSONObject(i);

                            int idPedido = obj.optInt("numero_exibicao", obj.optInt("id_pedido", 0));
                            String dataCriacao = obj.optString("criado_em", "");
                            double valorTotal = obj.getDouble("valor_total_pedido");
                            String status = obj.optString("status_pedido", "Pendente");
                            String pagamento = obj.optString("metodo_pagamento", "");

                            // O servidor manda o nome em "nome_entregador" (vazio quando ainda não há entregador)
                            String nomeEntregador = "";
                            if (!obj.isNull("nome_entregador")) {
                                nomeEntregador = obj.optString("nome_entregador", "").trim();
                            }
                            if (nomeEntregador.isEmpty() && !obj.isNull("entregador")) {
                                nomeEntregador = obj.optString("entregador", "").trim();
                            }
                            if (nomeEntregador.isEmpty()) {
                                nomeEntregador = "Aguardando atribuição";
                            }

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

                            novaLista.add(new PedidoModel(idPedido, dataCriacao, enderecoCompleto, pagamento, valorTotal, status, nomeEntregador, itensPedido));

                            // Resumo do que o cliente enxerga: se nada mudou, a tela nem é redesenhada
                            assinatura.append(idPedido).append('|').append(status).append('|')
                                    .append(nomeEntregador).append('|').append(valorTotal).append('|')
                                    .append(itensPedido.size()).append(';');
                        }

                        String assinaturaAtual = assinatura.toString();
                        if (assinaturaAtual.equals(ultimaAssinatura)) {
                            return; // nada mudou desde a última atualização
                        }
                        ultimaAssinatura = assinaturaAtual;

                        listaPedidos.clear();
                        listaPedidos.addAll(novaLista);
                        adapterPedidos.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (eraPrimeiraCarga) avisar("Erro ao processar dados dos pedidos");
                    }
                },
                error -> {
                    // Nas atualizações automáticas, falhas de conexão passam em silêncio
                    if (primeiraCarga) {
                        primeiraCarga = false;
                        avisar("Erro de conexão com o servidor");
                    }
                }
        );

        VolleySingleton.getInstance(requireContext()).add(jsonObjectRequest);
    }
}
