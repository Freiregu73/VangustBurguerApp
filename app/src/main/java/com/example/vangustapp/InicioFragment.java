package com.example.vangustapp;

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
            listaBanners.add(R.drawable.brutao); // Adicione as imagens dos banners aqui
            listaBanners.add(R.drawable.veggie);

            CarrosselAdapter carrosselAdapter = new CarrosselAdapter(listaBanners);
            viewPagerCarrossel.setAdapter(carrosselAdapter);

            // --- ROTAÇÃO AUTOMÁTICA DO CARROSSEL ---
            sliderRunnable = new Runnable() {
                @Override
                public void run() {
                    int proximaPosicao = viewPagerCarrossel.getCurrentItem() + 1;
                    if (proximaPosicao >= listaBanners.size()) {
                        proximaPosicao = 0; // Volta para o primeiro item ao chegar no fim
                    }
                    viewPagerCarrossel.setCurrentItem(proximaPosicao, true);

                    // Repete a cada 3 segundos (3000ms)
                    sliderHandler.postDelayed(this, 3000);
                }
            };

            // Inicia o carrossel automático após 3 segundos
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }

        // 1. Encontrar os RecyclerViews pelo ID correspondente no fragment_inicio.xml
        RecyclerView recOfertas = view.findViewById(R.id.idOfertas);
        RecyclerView recMaisPedidos = view.findViewById(R.id.idMaisPedidos);
        RecyclerView recLancamento = view.findViewById(R.id.idLançamento);

        // 2. Configurar o LayoutManager para horizontal em cada um
        if (recOfertas != null) {
            recOfertas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaOfertas = new ArrayList<>();
            listaOfertas.add(new ItensCard("Combo Bacon Cheese", R.drawable.brutao));
            listaOfertas.add(new ItensCard("Combo Bacon Cheese", R.drawable.frango));
            listaOfertas.add(new ItensCard("Combo Bacon Cheese", R.drawable.brutao));
            recOfertas.setAdapter(new ItensCardAdapter(listaOfertas));
        }

        if (recMaisPedidos != null) {
            recMaisPedidos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaMaisPedidos = new ArrayList<>();
            listaMaisPedidos.add(new ItensCard("Brutão na chapa", R.drawable.frango));
            listaMaisPedidos.add(new ItensCard("Brutão na chapa", R.drawable.brutao));
            listaMaisPedidos.add(new ItensCard("Brutão na chapa", R.drawable.veggie));
            recMaisPedidos.setAdapter(new ItensCardAdapter(listaMaisPedidos));
        }

        if (recLancamento != null) {
            recLancamento.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaLancamento = new ArrayList<>();
            listaLancamento.add(new ItensCard("Novo Burger", R.drawable.combo_fogo));
            listaLancamento.add(new ItensCard("Novo Burger", R.drawable.frango));
            listaLancamento.add(new ItensCard("Novo Burger", R.drawable.veggie));
            recLancamento.setAdapter(new ItensCardAdapter(listaLancamento));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Para a rotação automática ao sair da tela para evitar vazamento de memória
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }
}