package com.example.vangustapp; // Lembre-se de alterar para o pacote do seu projeto (ex: com.example.vangustburguer)

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EnderecoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_endereco, container, false);
    }
}