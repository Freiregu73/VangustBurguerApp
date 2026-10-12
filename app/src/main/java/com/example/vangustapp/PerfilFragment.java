package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class PerfilFragment extends Fragment {

    private TextView tvNomePerfil, tvEmailPerfil;
    private ImageView imgPerfil;
    private MaterialButton btnEditarPerfil, btnMeusPedidos, btnEnderecos, btnSairConta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        tvNomePerfil = view.findViewById(R.id.tvNomePerfil);
        tvEmailPerfil = view.findViewById(R.id.tvEmailPerfil);
        imgPerfil = view.findViewById(R.id.imgPerfil);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnMeusPedidos = view.findViewById(R.id.btnMeusPedidos);
        btnEnderecos = view.findViewById(R.id.btnEnderecos);
        btnSairConta = view.findViewById(R.id.btnSairConta);

        carregarDadosPerfil();

        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditarPerfil.class);
            startActivity(intent);
        });

        btnMeusPedidos.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PedidosFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnEnderecos.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EnderecoFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnSairConta.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean("is_logged", false);
            editor.remove("foto_perfil");
            editor.apply();

            CarrinhoManager.limparCarrinho();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void carregarDadosPerfil() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String nome = preferences.getString("nome_usuario", "Nome do Cliente");
        String email = preferences.getString("email_usuario", "cliente@vangustburguer.com");
        String fotoBase64 = preferences.getString("foto_perfil", "");

        if (tvNomePerfil != null) tvNomePerfil.setText(nome);
        if (tvEmailPerfil != null) tvEmailPerfil.setText(email);

        if (imgPerfil != null && !fotoBase64.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgPerfil.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDadosPerfil();
    }
}