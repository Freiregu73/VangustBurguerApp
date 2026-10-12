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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritoFragment extends Fragment {

    private RecyclerView idRecFavoritos;
    private List<FavoritosCard> lstFavoritos;
    private AdapterFavoritos adapterFavoritos;

    public FavoritoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorito, container, false);

        // --- BOTÃO FLUTUANTE DO CARRINHO ---
        FloatingActionButton fabCarrinho = view.findViewById(R.id.fabCarrinhoFavoritos);
        if (fabCarrinho != null) {
            fabCarrinho.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CarrinhoFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        idRecFavoritos = view.findViewById(R.id.idRecFavoritos);
        idRecFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));

        lstFavoritos = new ArrayList<>();
        adapterFavoritos = new AdapterFavoritos(lstFavoritos, getContext());
        idRecFavoritos.setAdapter(adapterFavoritos);

        // Carregar os favoritos diretamente da API PHP passando o ID do cliente
        carregarFavoritosDaApi();

        atualizarBolinha(view);

        return view;
    }

    private void carregarFavoritosDaApi() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        int idCliente = prefs.getInt("id_cliente", -1);

        String url = "http://10.0.2.2/api_hamburgueria/listar_favoritos.php?id_cliente=" + idCliente;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean sucesso = response.optBoolean("sucesso", false);
                        if (sucesso) {
                            JSONArray jsonArray = response.getJSONArray("favoritos");
                            lstFavoritos.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                // Captura o id_produto retornado pelo PHP
                                int idProduto = obj.optInt("id_produto", 0);
                                String nome = obj.optString("nome_produto", "Produto");
                                String descricao = obj.optString("descricao", "");
                                double preco = obj.optDouble("preco", 0.0);
                                String urlImagem = obj.optString("imagem", null);

                                // Passa o idProduto como primeiro argumento
                                lstFavoritos.add(new FavoritosCard(idProduto, nome, descricao, urlImagem, preco));
                            }
                            adapterFavoritos.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Nenhum favorito encontrado", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar dados dos favoritos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void atualizarBolinha(View view) {
        View dot = view.findViewById(R.id.dotCarrinhoFavoritos);
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
}