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
        // Inflate the layout for this fragment
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

        // 2. Configurar Carnes
        if (recCarnes != null) {
            recCarnes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaCarnes = new ArrayList<>();
            listaCarnes.add(new ItensCard("Brutão na chapa", "Hambúrguer parrudo de 180g, cheddar derretido e bacon crocante.", R.drawable.brutao, 34.99));
            listaCarnes.add(new ItensCard("Brutão na chapa", "Hambúrguer parrudo de 180g, cheddar derretido e bacon crocante.", R.drawable.brutao, 34.99));

            ItensCardAdapter adapter = new ItensCardAdapter(listaCarnes);
            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
            recCarnes.setAdapter(adapter);
        }

        // 3. Configurar Frangos
        if (recFrangos != null) {
            recFrangos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaFrangos = new ArrayList<>();
            listaFrangos.add(new ItensCard("Chicken Crispy", "Frango empanado super crocante com molho especial da casa.", R.drawable.frango, 28.50));
            listaFrangos.add(new ItensCard("Chicken Crispy", "Frango empanado super crocante com molho especial da casa.", R.drawable.frango, 28.50));

            ItensCardAdapter adapter = new ItensCardAdapter(listaFrangos);
            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
            recFrangos.setAdapter(adapter);
        }

        // 4. Configurar Veganos
        if (recVeganos != null) {
            recVeganos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaVeganos = new ArrayList<>();
            listaVeganos.add(new ItensCard("Burger Vegano", "Blend exclusivo de grão-de-bico com especiarias e maionese verde.", R.drawable.veggie, 29.00));
            listaVeganos.add(new ItensCard("Burger Vegano", "Blend exclusivo de grão-de-bico com especiarias e maionese verde.", R.drawable.veggie, 29.00));

            ItensCardAdapter adapter = new ItensCardAdapter(listaVeganos);
            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
            recVeganos.setAdapter(adapter);
        }

        // 5. Configurar Combos
        if (recCombos != null) {
            recCombos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<ItensCard> listaCombos = new ArrayList<>();
            listaCombos.add(new ItensCard("Combo Família", "Inclui hambúrgueres grandes, batata frita generosa e bebida.", R.drawable.combo_fogo, 59.90));
            listaCombos.add(new ItensCard("Combo Família", "Inclui hambúrgueres grandes, batata frita generosa e bebida.", R.drawable.combo_fogo, 59.90));

            ItensCardAdapter adapter = new ItensCardAdapter(listaCombos);
            adapter.setOnItemClickListener(item -> abrirDetalhes(item));
            recCombos.setAdapter(adapter);
        }
    }

    // Método auxiliar para abrir os detalhes de qualquer item selecionado
    private void abrirDetalhes(ItensCard item) {
        Intent intent = new Intent(getContext(), DetalhesProdutoActivity.class);
        intent.putExtra("titulo", item.getTitulo());
        intent.putExtra("descricao", item.getDescricao());
        intent.putExtra("preco", item.getPreco());
        intent.putExtra("imagem", item.getImgitens());
        startActivity(intent);
    }
}