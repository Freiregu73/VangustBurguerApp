package com.example.vangustapp;

import android.content.Intent;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CardapioFragment extends Fragment {

    private RecyclerView recCarnes, recFrangos, recVeganos, recCombos;

    public CardapioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cardapio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Encontrar os RecyclerViews pelo ID correspondente
        recCarnes = view.findViewById(R.id.idRecCarnes);
        recFrangos = view.findViewById(R.id.idRecFrangos);
        recVeganos = view.findViewById(R.id.idRecVeganos);
        recCombos = view.findViewById(R.id.idRecCombos);

        // Configurar o LayoutManager horizontal para cada lista
        if (recCarnes != null) recCarnes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recFrangos != null) recFrangos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recVeganos != null) recVeganos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recCombos != null) recCombos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 2. Carregar os produtos diretamente da API PHP
        carregarProdutosDaApi();

        com.google.android.material.floatingactionbutton.FloatingActionButton fabCarrinho = view.findViewById(R.id.fabCarrinhoCardapio);
        if (fabCarrinho != null) {
            fabCarrinho.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CarrinhoFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        atualizarBolinha(view);
    }

    private void carregarProdutosDaApi() {
        // URL da API de produtos (10.0.2.2 aponta para o localhost do XAMPP no emulador)
        String url = "http://10.0.2.2/api_hamburgueria/produtos.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<ItensCard> listaCarnes = new ArrayList<>();
                    List<ItensCard> listaFrangos = new ArrayList<>();
                    List<ItensCard> listaVeganos = new ArrayList<>();
                    List<ItensCard> listaCombos = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String nome = obj.getString("nome_produto");
                            String descricao = obj.optString("descricao", "");
                            double preco = obj.getDouble("preco");
                            String categoria = obj.optString("categoria", "").toLowerCase();

                            // Captura a URL real da imagem gerada pelo PHP (produtos.php + visualizar_imagem.php)
                            String imagemUrl = obj.optString("imagem", null);

                            // Cria o objeto do item passando a URL da imagem da API
                            ItensCard item = new ItensCard(nome, descricao, imagemUrl, preco);

                            // Distribui nas categorias correspondentes
                            if (categoria.contains("frango")) {
                                listaFrangos.add(item);
                            } else if (categoria.contains("vegano") || categoria.contains("vegetariano")) {
                                listaVeganos.add(item);
                            } else if (categoria.contains("combo")) {
                                listaCombos.add(item);
                            } else {
                                listaCarnes.add(item); // Categoria padrão ou carne
                            }
                        }

                        // Preencher os RecyclerViews com as listas preenchidas pela API
                        if (recCarnes != null && !listaCarnes.isEmpty()) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaCarnes);
                            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
                            recCarnes.setAdapter(adapter);
                        }
                        if (recFrangos != null && !listaFrangos.isEmpty()) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaFrangos);
                            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
                            recFrangos.setAdapter(adapter);
                        }
                        if (recVeganos != null && !listaVeganos.isEmpty()) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaVeganos);
                            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
                            recVeganos.setAdapter(adapter);
                        }
                        if (recCombos != null && !listaCombos.isEmpty()) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaCombos);
                            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
                            recCombos.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar dados da API", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                }
        );

        // Adiciona à fila de requisições do Volley
        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
    }

    private void atualizarBolinha(View view) {
        View dot = view.findViewById(R.id.dotCarrinhoCardapio);
        if (dot != null) {
            boolean temItens = !CarrinhoManager.getListaCarrinho().isEmpty();
            dot.setVisibility(temItens ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            atualizarBolinha(getView());
        }
    }

    private void abrirDetalhes(ItensCard item) {
        Intent intent = new Intent(getContext(), DetalhesProdutoActivity.class);
        intent.putExtra("titulo", item.getTitulo());
        intent.putExtra("descricao", item.getDescricao());
        intent.putExtra("preco", item.getPreco());

        // Passa a URL da imagem ou o identificador local para a tela de detalhes
        intent.putExtra("imagem_url", item.getImageUrl());
        intent.putExtra("imagem", item.getImgitens());

        startActivity(intent);
    }
}