package com.example.vangustapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    private RecyclerView recOfertas, recMaisPedidos, recLancamento;
    private ItensCardAdapter adapterOfertas, adapterMaisPedidos, adapterLancamento;
    private List<ItensCard> listaOfertas, listaMaisPedidos, listaLancamento;

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabCarrinho = view.findViewById(R.id.fabCarrinho);
        if (fabCarrinho != null) {
            fabCarrinho.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CarrinhoFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        atualizarBolinhaCarrinho(view);

        ViewPager2 viewPagerCarrossel = view.findViewById(R.id.viewPagerCarrossel);
        if (viewPagerCarrossel != null) {
            List<Integer> listaBanners = new ArrayList<>();
            listaBanners.add(R.drawable.logo_vangust_burguer);
            listaBanners.add(R.drawable.logo_vangust_burguer);

            CarrosselAdapter carrosselAdapter = new CarrosselAdapter(listaBanners);
            viewPagerCarrossel.setAdapter(carrosselAdapter);

            sliderRunnable = new Runnable() {
                @Override
                public void run() {
                    int proximaPosicao = viewPagerCarrossel.getCurrentItem() + 1;
                    if (proximaPosicao >= listaBanners.size()) {
                        proximaPosicao = 0;
                    }
                    viewPagerCarrossel.setCurrentItem(proximaPosicao, true);
                    sliderHandler.postDelayed(this, 3000);
                }
            };
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }

        recOfertas = view.findViewById(R.id.idOfertas);
        recMaisPedidos = view.findViewById(R.id.idMaisPedidos);
        recLancamento = view.findViewById(R.id.idLançamento);

        listaOfertas = new ArrayList<>();
        listaMaisPedidos = new ArrayList<>();
        listaLancamento = new ArrayList<>();

        adapterOfertas = new ItensCardAdapter(listaOfertas);
        adapterOfertas.setOnItemClickListener(this::abrirDetalhes);
        if (recOfertas != null) {
            recOfertas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recOfertas.setAdapter(adapterOfertas);
        }

        adapterMaisPedidos = new ItensCardAdapter(listaMaisPedidos);
        adapterMaisPedidos.setOnItemClickListener(this::abrirDetalhes);
        if (recMaisPedidos != null) {
            recMaisPedidos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recMaisPedidos.setAdapter(adapterMaisPedidos);
        }

        adapterLancamento = new ItensCardAdapter(listaLancamento);
        adapterLancamento.setOnItemClickListener(this::abrirDetalhes);
        if (recLancamento != null) {
            recLancamento.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recLancamento.setAdapter(adapterLancamento);
        }

        carregarProdutosDaApi();
    }

    private void carregarProdutosDaApi() {
        String url = "http://10.0.2.2/api_hamburgueria/produtos.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        listaOfertas.clear();
                        listaMaisPedidos.clear();
                        listaLancamento.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            int idProduto = obj.optInt("id_produto", 0);
                            String nome = obj.optString("nome_produto", "Produto");
                            String descricao = obj.optString("descricao", "Delicioso hambúrguer da casa.");
                            double preco = obj.optDouble("preco", 0.0);
                            String urlImagem = obj.optString("imagem", null);
                            String categoria = obj.optString("categoria", "").trim().toLowerCase();

                            ItensCard item = new ItensCard(idProduto, nome, descricao, urlImagem, preco);

                            if (categoria.contains("oferta") || categoria.contains("ofertas")) {
                                listaOfertas.add(item);
                            } else if (categoria.contains("pedido") || categoria.contains("mais pedidos")) {
                                listaMaisPedidos.add(item);
                            } else if (categoria.contains("lancamento") || categoria.contains("lançamento")) {
                                listaLancamento.add(item);
                            } else {
                                listaMaisPedidos.add(item);
                            }
                        }

                        adapterOfertas.notifyDataSetChanged();
                        adapterMaisPedidos.notifyDataSetChanged();
                        adapterLancamento.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Erro ao processar produtos do servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão ao carregar produtos", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(jsonArrayRequest);
    }

    private void atualizarBolinhaCarrinho(View view) {
        View dotCarrinho = view.findViewById(R.id.dotCarrinho);
        if (dotCarrinho != null) {
            boolean temItens = !CarrinhoManager.getListaCarrinho().isEmpty();
            dotCarrinho.setVisibility(temItens ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            atualizarBolinhaCarrinho(getView());
        }
    }

    private void abrirDetalhes(ItensCard item) {
        Intent intent = new Intent(getContext(), DetalhesProdutoActivity.class);
        intent.putExtra("id_produto", item.getIdProduto()); // Envia o ID para a tela de detalhes
        intent.putExtra("titulo", item.getTitulo());
        intent.putExtra("descricao", item.getDescricao());
        intent.putExtra("preco", item.getPreco());
        intent.putExtra("imagem", item.getImageUrl() != null ? item.getImageUrl() : "");
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }
}