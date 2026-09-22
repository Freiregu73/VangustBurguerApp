package com.example.vangustapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EnderecoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Apenas infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_endereco, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // O botão flutuante é inicializado aqui, onde a "view" já está disponível
        FloatingActionButton fab = view.findViewById(R.id.fabAdicionarEndereco);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), NovoEndereco.class);
                startActivity(intent);
            });
        }
    }
}