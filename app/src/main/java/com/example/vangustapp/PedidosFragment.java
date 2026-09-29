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

import java.util.List;

public class PedidosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView idRecPedidos;

    public PedidosFragment() {
        // Required empty public constructor
    }

    public static PedidosFragment newInstance(String param1, String param2) {
        PedidosFragment fragment = new PedidosFragment();
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
        return inflater.inflate(R.layout.fragment_pedidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Encontrar o RecyclerView no layout (certifica-te de que este ID existe no teu fragment_pedidos.xml)
        idRecPedidos = view.findViewById(R.id.idRecPedidos);
        idRecPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Buscar a lista de pedidos guardados no gestor global
        List<PedidoModel> listaPedidos = PedidoManager.getListaPedidos();

        // 3. Configurar o Adapter para exibir os pedidos
        // Nota: Podes criar um AdapterPedidos semelhante aos outros adaptadores que já tens no projeto
        AdapterPedidos adapterPedidos = new AdapterPedidos(listaPedidos, getContext());
        idRecPedidos.setAdapter(adapterPedidos);
    }
}