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
import android.widget.ImageView;
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

        carregarEnderecoUsuario();

        // Calcular total considerando o subtotal, o desconto do cupom e a taxa de entrega
        double subtotal = CarrinhoManager.calcularTotal();
        double desconto = calcularDescontoCupom(subtotal);
        double totalGeral = (subtotal - desconto) + TAXA_ENTREGA;
        if (totalGeral < 0) totalGeral = TAXA_ENTREGA;

        tvTotalCheckout.setText(String.format("R$ %.2f", totalGeral));

        btnConfirmarPedidoFinal.setOnClickListener(v -> {
            int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor, selecione um método de pagamento!", Toast.LENGTH_SHORT).show();
            } else {
                String pagamentoEscolhido = "PIX";
                if (selectedId == R.id.rbCartao) {
                    pagamentoEscolhido = "Cartão";
                } else if (selectedId == R.id.rbDinheiro) {
                    pagamentoEscolhido = "Dinheiro";
                }

                String enderecoAtual = tvEnderecoCheckout.getText().toString();
                double totalFinalPedido = (CarrinhoManager.calcularTotal() - desconto) + TAXA_ENTREGA;

                List<ItemCarrinho> itensDoPedido = new ArrayList<>(CarrinhoManager.getListaCarrinho());
                PedidoModel novoPedido = new PedidoModel(enderecoAtual, pagamentoEscolhido, totalFinalPedido, itensDoPedido);
                PedidoManager.adicionarPedido(novoPedido);

                Toast.makeText(getContext(), "Pedido realizado com sucesso! Bom apetite!", Toast.LENGTH_LONG).show();

                // Limpa o carrinho e o cupom utilizado após finalizar
                CarrinhoManager.limparCarrinho();
                limparCupomAtivo();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PedidosFragment())
                        .commit();
            }
        });

        ImageView btnVoltarCheckout = view.findViewById(R.id.btnVoltarCheckout);
        if (btnVoltarCheckout != null) {
            btnVoltarCheckout.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

    }

    private void carregarEnderecoUsuario() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String enderecoSalvo = sharedPreferences.getString("endereco_usuario", "Rua Exemplo, 123 - Centro (Edite no seu perfil)");
        tvEnderecoCheckout.setText(enderecoSalvo);
    }

    private double calcularDescontoCupom(double subtotal) {
        if (subtotal <= 0) return 0.0;
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String cupomAtivo = prefs.getString("cupom_ativo", "");

        if (cupomAtivo.equals("VANGUST10")) {
            return subtotal * 0.10;
        } else if (cupomAtivo.equals("PRIMEIRA")) {
            return Math.min(subtotal, 10.00);
        }
        return 0.0;
    }

    private void limparCupomAtivo() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        prefs.edit().remove("cupom_ativo").remove("desconto_ativo").apply();
    }
}