package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EnderecoFragment extends Fragment {

    private RecyclerView idRecEndereco;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_endereco, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idRecEndereco = view.findViewById(R.id.idRecEndereco);
        idRecEndereco.setLayoutManager(new LinearLayoutManager(getContext()));

        // Carregar endereços salvos
        carregarEnderecos();

        // Botão flutuante para adicionar novo endereço
        FloatingActionButton fab = view.findViewById(R.id.fabAdicionarEndereco);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NovoEndereco.class);
                startActivity(intent);
            });
        }
    }

    private void carregarEnderecos() {
        List<EnderecoModel> lista = new ArrayList<>();

        // Buscar do SharedPreferences o endereço que foi guardado no checkout/novo endereço
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String enderecoSalvo = sharedPreferences.getString("endereco_usuario", "");

        if (!enderecoSalvo.isEmpty()) {
            // Se houver um endereço guardado, adicionamos à lista para exibir no cartão
            lista.add(new EnderecoModel("Endereço Principal", enderecoSalvo));
        }

        AdapterEndereco adapter = new AdapterEndereco(lista, getContext());
        idRecEndereco.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualiza a lista sempre que voltar para este fragmento (ex: após cadastrar um novo)
        carregarEnderecos();
    }
}