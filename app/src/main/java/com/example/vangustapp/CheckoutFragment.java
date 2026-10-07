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

        // Declaramos a variável totalFinalPedido como final aqui para poder ser usada na classe anónima do Volley sem erros
        final double totalFinalPedido = totalGeral;

        btnConfirmarPedidoFinal.setOnClickListener(v -> {
            int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor, selecione um método de pagamento!", Toast.LENGTH_SHORT).show();
            } else if (CarrinhoManager.getListaCarrinho().isEmpty()) {
                Toast.makeText(getContext(), "O seu carrinho está vazio!", Toast.LENGTH_SHORT).show();
            } else {
                // Atribuição única para ser considerada efetivamente final pelo Java
                final String pagamentoEscolhido;
                if (selectedId == R.id.rbCartao) {
                    pagamentoEscolhido = "Cartão";
                } else if (selectedId == R.id.rbDinheiro) {
                    pagamentoEscolhido = "Dinheiro";
                } else {
                    pagamentoEscolhido = "PIX";
                }

                String enderecoAtual = tvEnderecoCheckout.getText().toString();

                // Recuperar o e-mail do utilizador logado no SharedPreferences
                android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                String emailUsuario = prefs.getString("email_usuario", "cliente@vangustburguer.com");

                // Converter a lista de itens do carrinho num array JSON para enviar ao PHP
                org.json.JSONArray jsonArrayItens = new org.json.JSONArray();
                try {
                    for (ItemCarrinho item : CarrinhoManager.getListaCarrinho()) {
                        org.json.JSONObject objItem = new org.json.JSONObject();
                        objItem.put("titulo", item.getTitulo());
                        objItem.put("preco", item.getPreco());
                        objItem.put("quantidade", item.getQuantidade());
                        jsonArrayItens.put(objItem);
                    }
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }

                // URL da API para salvar o pedido
                String url = "http://10.0.2.2/api_hamburgueria/salvar_pedido.php";

                com.android.volley.toolbox.StringRequest stringRequest = new com.android.volley.toolbox.StringRequest(
                        com.android.volley.Request.Method.POST, url,
                        response -> {
                            try {
                                org.json.JSONObject jsonObject = new org.json.JSONObject(response);
                                boolean sucesso = jsonObject.getBoolean("sucesso");

                                if (sucesso) {
                                    Toast.makeText(getContext(), "Pedido realizado e enviado para o painel!", Toast.LENGTH_LONG).show();

                                    // Limpa o carrinho e o cupom ativo
                                    CarrinhoManager.limparCarrinho();
                                    limparCupomAtivo();

                                    // Redireciona para a tela de histórico de pedidos
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new PedidosFragment())
                                            .commit();
                                } else {
                                    Toast.makeText(getContext(), "Erro: " + jsonObject.getString("mensagem"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Toast.makeText(getContext(), "Erro de conexão ao finalizar pedido", Toast.LENGTH_SHORT).show();
                        }
                ) {
                    @Override
                    protected java.util.Map<String, String> getParams() {
                        java.util.Map<String, String> params = new java.util.HashMap<>();
                        params.put("email", emailUsuario);
                        params.put("endereco", enderecoAtual);
                        params.put("pagamento", pagamentoEscolhido);
                        params.put("total", String.valueOf(totalFinalPedido));
                        params.put("itens", jsonArrayItens.toString());
                        return params;
                    }
                };

                com.android.volley.toolbox.Volley.newRequestQueue(requireContext()).add(stringRequest);
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