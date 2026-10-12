package com.example.vangustapp;

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

        carregarFavoritosDaApi();
        atualizarBolinha(view);

        return view;
    }

    private void carregarFavoritosDaApi() {
        String url = "http://10.0.2.2/api_hamburgueria/produtos.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        lstFavoritos.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String nome = obj.optString("nome_produto", "Produto");
                            String descricao = obj.optString("descricao", "");
                            double preco = obj.optDouble("preco", 0.0);
                            String urlImagem = obj.optString("imagem", null);

                            // Por enquanto, carrega os produtos gerais ou filtra os favoritos
                            lstFavoritos.add(new FavoritosCard(nome, descricao, urlImagem, preco));
                        }
                        adapterFavoritos.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar favoritos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
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