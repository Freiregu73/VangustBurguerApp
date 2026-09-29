package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {

    private TextView tvEnderecoCheckout, tvTotalCheckout;
    private RadioGroup radioGroupPagamento;
    private Button btnConfirmarPedidoFinal;

    private static final double TAXA_ENTREGA = 5.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEnderecoCheckout = view.findViewById(R.id.tvEnderecoCheckout);
        tvTotalCheckout = view.findViewById(R.id.tvTotalCheckout);
        radioGroupPagamento = view.findViewById(R.id.radioGroupPagamento);
        btnConfirmarPedidoFinal = view.findViewById(R.id.btnConfirmarPedidoFinal);

        // 1. Carregar o endereço guardado no SharedPreferences (ajusta a chave "usuario_logado" ou "endereco" conforme usaste no teu projeto)
        carregarEnderecoUsuario();

        // 2. Calcular e mostrar o total (Subtotal dos itens + Taxa)
        double totalGeral = CarrinhoManager.calcularTotal() + TAXA_ENTREGA;
        tvTotalCheckout.setText(String.format("R$ %.2f", totalGeral));

        // 3. Ação do botão de confirmação definitiva
        btnConfirmarPedidoFinal.setOnClickListener(v -> {
            int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor, selecione um método de pagamento!", Toast.LENGTH_SHORT).show();
            } else {
                // Identificar o texto do pagamento selecionado
                String pagamentoEscolhido = "PIX";
                if (selectedId == R.id.rbCartao) {
                    pagamentoEscolhido = "Cartão";
                } else if (selectedId == R.id.rbDinheiro) {
                    pagamentoEscolhido = "Dinheiro";
                }

                String enderecoAtual = tvEnderecoCheckout.getText().toString();
                double totalAtual = CarrinhoManager.calcularTotal() + TAXA_ENTREGA;

                // Criar e salvar o pedido na lista global (copiando os itens do carrinho atual)
                List<ItemCarrinho> itensDoPedido = new ArrayList<>(CarrinhoManager.getListaCarrinho());
                PedidoModel novoPedido = new PedidoModel(enderecoAtual, pagamentoEscolhido, totalAtual, itensDoPedido);
                PedidoManager.adicionarPedido(novoPedido);

                Toast.makeText(getContext(), "Pedido realizado com sucesso! Bom apetite!", Toast.LENGTH_LONG).show();

                // Limpa o carrinho
                CarrinhoManager.limparCarrinho();

                // Vai para o ecrã de Pedidos
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PedidosFragment())
                        .commit();
            }
        });
    }

    private void carregarEnderecoUsuario() {
        // Exemplo a ler do SharedPreferences (podes adaptar caso tenhas guardado com outra chave)
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        // Se guardaste o endereço no registo/perfil, busca-o aqui. Colocamos um texto predefinido caso venha vazio:
        String enderecoSalvo = sharedPreferences.getString("endereco_usuario", "Rua Exemplo, 123 - Centro (Edite no seu perfil)");

        tvEnderecoCheckout.setText(enderecoSalvo);
    }
}