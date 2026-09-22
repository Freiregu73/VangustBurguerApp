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

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Variáveis para controlar o movimento automático do carrossel
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    public InicioFragment() {
        // Required empty public constructor
    }

    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment (aponta para o fragment_inicio.xml)
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 0. CONFIGURAÇÃO DO CARROSSEL SUPERIOR ---
        ViewPager2 viewPagerCarrossel = view.findViewById(R.id.viewPagerCarrossel);
        if (viewPagerCarrossel != null) {
            List<Integer> listaBanners = new ArrayList<>();
            listaBanners.add(R.drawable.brutao);
            listaBanners.add(R.drawable.veggie);

            CarrosselAdapter carrosselAdapter = new CarrosselAdapter(listaBanners);
            viewPagerCarrossel.setAdapter(carrosselAdapter);

            // --- ROTAÇÃO AUTOMÁTICA DO CARROSSEL ---
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

        // 1. Encontrar os RecyclerViews pelo ID correspondente
        RecyclerView recOfertas = view.findViewById(R.id.idOfertas);
        RecyclerView recMaisPedidos = view.findViewById(R.id.idMaisPedidos);
        RecyclerView recLancamento = view.findViewById(R.id.idLançamento);

        // 2. Configurar Ofertas
        if (recOfertas != null) {
            recOfertas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaOfertas = new ArrayList<>();
            listaOfertas.add(new ItensCard("Combo Bacon Cheese", "Hambúrguer de 180g, muito cheddar derretido e bacon crocante.", R.drawable.brutao, 32.90));
            listaOfertas.add(new ItensCard("Chicken Crispy", "Frango empanado super crocante com molho especial da casa.", R.drawable.frango, 28.50));
            listaOfertas.add(new ItensCard("Combo Bacon Cheese", "Hambúrguer de 180g, muito cheddar derretido e bacon crocante.", R.drawable.brutao, 32.90));

            ItensCardAdapter adapterOfertas = new ItensCardAdapter(listaOfertas);
            adapterOfertas.setOnItemClickListener(item -> abrirDetalhes(item));
            recOfertas.setAdapter(adapterOfertas);
        }

        // 3. Configurar Mais Pedidos
        if (recMaisPedidos != null) {
            recMaisPedidos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaMaisPedidos = new ArrayList<>();
            listaMaisPedidos.add(new ItensCard("Brutão na chapa", "Parrudo de 180g, cheddar, bacon e cebola na manteiga.", R.drawable.brutao, 34.99));
            listaMaisPedidos.add(new ItensCard("Chicken Crispy", "Frango empanado super crocante com molho especial.", R.drawable.frango, 28.50));
            listaMaisPedidos.add(new ItensCard("Burger Vegano", "Blend de grão-de-bico com especiarias e maionese verde.", R.drawable.veggie, 29.00));

            ItensCardAdapter adapterMaisPedidos = new ItensCardAdapter(listaMaisPedidos);
            adapterMaisPedidos.setOnItemClickListener(item -> abrirDetalhes(item));
            recMaisPedidos.setAdapter(adapterMaisPedidos);
        }

        // 4. Configurar Lançamentos
        if (recLancamento != null) {
            recLancamento.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaLancamento = new ArrayList<>();
            listaLancamento.add(new ItensCard("Combo Fogo", "O novo combo picante com molho especial de pimenta jalapeño.", R.drawable.combo_fogo, 39.90));
            listaLancamento.add(new ItensCard("Chicken Crispy", "Frango empanado super crocante.", R.drawable.frango, 28.50));
            listaLancamento.add(new ItensCard("Burger Vegano", "Blend de grão-de-bico e salada fresca.", R.drawable.veggie, 29.00));

            ItensCardAdapter adapterLancamento = new ItensCardAdapter(listaLancamento);
            adapterLancamento.setOnItemClickListener(item -> abrirDetalhes(item));
            recLancamento.setAdapter(adapterLancamento);
        }
    }

    // Método auxiliar para abrir o ecrã de detalhes de forma limpa
    private void abrirDetalhes(ItensCard item) {
        Intent intent = new Intent(getContext(), DetalhesProdutoActivity.class);
        intent.putExtra("titulo", item.getTitulo());
        intent.putExtra("descricao", item.getDescricao());
        intent.putExtra("preco", item.getPreco());
        intent.putExtra("imagem", item.getImgitens());
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