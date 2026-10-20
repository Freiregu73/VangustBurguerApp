package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
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

        recCarnes = view.findViewById(R.id.idRecCarnes);
        recFrangos = view.findViewById(R.id.idRecFrangos);
        recVeganos = view.findViewById(R.id.idRecVeganos);
        recCombos = view.findViewById(R.id.idRecCombos);

        if (recCarnes != null) recCarnes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recFrangos != null) recFrangos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recVeganos != null) recVeganos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (recCombos != null) recCombos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        String url = "https://vangustburguer.com.br/api_hamburgueria/produtos.php?id_cliente=" + idCliente;

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

                            int idProduto = obj.optInt("id_produto", 0);
                            String nome = obj.getString("nome_produto");
                            String descricao = obj.optString("descricao", "");
                            double preco = obj.getDouble("preco");
                            String categoria = obj.optString("categoria", "").toLowerCase();
                            String imagemUrl = obj.optString("imagem", null);
                            boolean isFavorito = obj.optBoolean("is_favorito", false);

                            ItensCard item = new ItensCard(idProduto, nome, descricao, imagemUrl, preco, isFavorito);

                            boolean colocadoEmAlgumaLista = false;

                            // Condições independentes (permite múltiplas categorias separadas por vírgula no painel web)
                            if (categoria.contains("frango")) {
                                listaFrangos.add(item);
                                colocadoEmAlgumaLista = true;
                            }
                            if (categoria.contains("vegano") || categoria.contains("vegetariano")) {
                                listaVeganos.add(item);
                                colocadoEmAlgumaLista = true;
                            }
                            if (categoria.contains("combo")) {
                                listaCombos.add(item);
                                colocadoEmAlgumaLista = true;
                            }
                            if (categoria.contains("carne") || categoria.contains("carnes") || !colocadoEmAlgumaLista) {
                                // Se contiver carne ou não tiver nenhuma das categorias acima, vai para a aba de Carnes por padrão
                                listaCarnes.add(item);
                            }
                        }

                        if (recCarnes != null) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaCarnes);
                            adapter.setOnItemClickListener(this::abrirDetalhes);
                            recCarnes.setAdapter(adapter);
                        }
                        if (recFrangos != null) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaFrangos);
                            adapter.setOnItemClickListener(this::abrirDetalhes);
                            recFrangos.setAdapter(adapter);
                        }
                        if (recVeganos != null) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaVeganos);
                            adapter.setOnItemClickListener(this::abrirDetalhes);
                            recVeganos.setAdapter(adapter);
                        }
                        if (recCombos != null) {
                            ItensCardAdapter adapter = new ItensCardAdapter(listaCombos);
                            adapter.setOnItemClickListener(this::abrirDetalhes);
                            recCombos.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar dados da API", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        );

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
        carregarProdutosDaApi(); // Efeito "F5 automático" ao voltar para o cardápio
        if (getView() != null) {
            atualizarBolinha(getView());
        }
    }

    private void abrirDetalhes(ItensCard item) {
        Intent intent = new Intent(getContext(), DetalhesProdutoActivity.class);
        intent.putExtra("id_produto", item.getIdProduto());
        intent.putExtra("titulo", item.getTitulo());
        intent.putExtra("descricao", item.getDescricao());
        intent.putExtra("preco", item.getPreco());
        intent.putExtra("imagem", item.getImageUrl() != null ? item.getImageUrl() : "");
        intent.putExtra("is_favorito", item.isFavorito());
        startActivity(intent);
    }
}