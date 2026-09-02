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

import java.util.ArrayList;
import java.util.List;

public class CardapioFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CardapioFragment() {
        // Required empty public constructor
    }

    public static CardapioFragment newInstance(String param1, String param2) {
        CardapioFragment fragment = new CardapioFragment();
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
        // Inflate the layout for this fragment (aqui já aponta certinho para o fragment_cardapio.xml)
        return inflater.inflate(R.layout.fragment_cardapio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Encontrar os RecyclerViews pelo ID correspondente no fragment_cardapio.xml
        RecyclerView recCarnes = view.findViewById(R.id.idRecCarnes);
        RecyclerView recFrangos = view.findViewById(R.id.idRecFrangos);
        RecyclerView recVeganos = view.findViewById(R.id.idRecVeganos);
        RecyclerView recCombos = view.findViewById(R.id.idRecCombos);

        // 2. Configurar o LayoutManager para horizontal em cada um
        if (recCarnes != null) {
            recCarnes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaCarnes = new ArrayList<>();
            listaCarnes.add(new ItensCard("Brutão na chapa", R.drawable.brutao));
            listaCarnes.add(new ItensCard("Brutão na chapa", R.drawable.brutao));
            listaCarnes.add(new ItensCard("Brutão na chapa", R.drawable.brutao));
            listaCarnes.add(new ItensCard("Brutão na chapa", R.drawable.brutao));
            recCarnes.setAdapter(new ItensCardAdapter(listaCarnes));
        }

        if (recFrangos != null) {
            recFrangos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaFrangos = new ArrayList<>();
            listaFrangos.add(new ItensCard("Chicken Crispy", R.drawable.frango));
            listaFrangos.add(new ItensCard("Chicken Crispy", R.drawable.frango));
            listaFrangos.add(new ItensCard("Chicken Crispy", R.drawable.frango));
            listaFrangos.add(new ItensCard("Chicken Crispy", R.drawable.frango));
            recFrangos.setAdapter(new ItensCardAdapter(listaFrangos));
        }

        if (recVeganos != null) {
            recVeganos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaVeganos = new ArrayList<>();
            listaVeganos.add(new ItensCard("Burger Vegano", R.drawable.veggie));
            listaVeganos.add(new ItensCard("Burger Vegano", R.drawable.veggie));
            listaVeganos.add(new ItensCard("Burger Vegano", R.drawable.veggie));
            listaVeganos.add(new ItensCard("Burger Vegano", R.drawable.veggie));
            recVeganos.setAdapter(new ItensCardAdapter(listaVeganos));
        }

        if (recCombos != null) {
            recCombos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaCombos = new ArrayList<>();
            listaCombos.add(new ItensCard("Combo Família", R.drawable.combo_fogo));
            listaCombos.add(new ItensCard("Combo Família", R.drawable.combo_fogo));
            listaCombos.add(new ItensCard("Combo Família", R.drawable.combo_fogo));
            listaCombos.add(new ItensCard("Combo Família", R.drawable.combo_fogo));
            recCombos.setAdapter(new ItensCardAdapter(listaCombos));
        }
    }
}