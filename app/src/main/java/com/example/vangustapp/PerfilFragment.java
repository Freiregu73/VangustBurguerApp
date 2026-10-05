package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class PerfilFragment extends Fragment {

    private TextView tvNomePerfil, tvEmailPerfil;
    private MaterialButton btnEditarPerfil, btnMeusPedidos, btnEnderecos, btnSairConta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // 1. Ligar aos IDs do fragment_perfil.xml
        tvNomePerfil = view.findViewById(R.id.tvNomePerfil);
        tvEmailPerfil = view.findViewById(R.id.tvEmailPerfil);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnMeusPedidos = view.findViewById(R.id.btnMeusPedidos);
        btnEnderecos = view.findViewById(R.id.btnEnderecos);
        btnSairConta = view.findViewById(R.id.btnSairConta);

        // 2. Carregar os dados salvos no SharedPreferences
        carregarDadosPerfil();

        // 3. Ação para ir para a tela de Edição de Perfil
        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditarPerfil.class);
            startActivity(intent);
        });

        // 4. Ação para ir para os Pedidos
        btnMeusPedidos.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PedidosFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // 5. Ação para ir para os Endereços
        btnEnderecos.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EnderecoFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // 6. Ação de Sair da Conta (Logout)
        btnSairConta.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            // Se quiser limpar dados de sessão (mantendo ou não o registo, aqui limpamos a sessão ativa)
            editor.remove("usuario_logado");
            editor.apply();

            // Limpa o carrinho também por segurança
            CarrinhoManager.limparCarrinho();

            // Volta para a tela de Login
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

        if (tvNomePerfil != null) tvNomePerfil.setText(nome);
        if (tvEmailPerfil != null) tvEmailPerfil.setText(email);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Atualiza caso o utilizador volte da tela de edição com dados novos
        carregarDadosPerfil();
    }
}