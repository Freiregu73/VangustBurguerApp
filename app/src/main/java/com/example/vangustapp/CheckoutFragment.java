package com.example.vangustapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Permitir que o utilizador clique no endereço para escolher outro cadastrado
        if (tvEnderecoCheckout != null) {
            tvEnderecoCheckout.setOnClickListener(v -> mostrarDialogoSelecaoEnderecos());
        }

        // Calcular total considerando o subtotal, o desconto do cupom e a taxa de entrega
        double subtotal = CarrinhoManager.calcularTotal();
        double desconto = calcularDescontoCupom(subtotal);
        double totalGeral = (subtotal - desconto) + TAXA_ENTREGA;
        if (totalGeral < 0) totalGeral = TAXA_ENTREGA;

        tvTotalCheckout.setText(String.format("R$ %.2f", totalGeral));

        final double totalFinalPedido = totalGeral;

        btnConfirmarPedidoFinal.setOnClickListener(v -> {
            int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor, selecione um método de pagamento!", Toast.LENGTH_SHORT).show();
            } else if (CarrinhoManager.getListaCarrinho().isEmpty()) {
                Toast.makeText(getContext(), "O seu carrinho está vazio!", Toast.LENGTH_SHORT).show();
            } else {
                final String pagamentoEscolhido;
                if (selectedId == R.id.rbCartao) {
                    pagamentoEscolhido = "Cartão";
                } else if (selectedId == R.id.rbDinheiro) {
                    pagamentoEscolhido = "Dinheiro";
                } else {
                    pagamentoEscolhido = "PIX";
                }

                String enderecoAtual = tvEnderecoCheckout.getText().toString();

                SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
                String emailUsuario = prefs.getString("email_usuario", "cliente@vangustburguer.com");

                JSONArray jsonArrayItens = new JSONArray();
                try {
                    for (ItemCarrinho item : CarrinhoManager.getListaCarrinho()) {
                        JSONObject objItem = new JSONObject();
                        objItem.put("titulo", item.getTitulo());
                        objItem.put("preco", item.getPreco());
                        objItem.put("quantidade", item.getQuantidade());
                        jsonArrayItens.put(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://10.0.2.2/api_hamburgueria/salvar_pedido.php";

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean sucesso = jsonObject.getBoolean("sucesso");

                                if (sucesso) {
                                    Toast.makeText(getContext(), "Pedido realizado e enviado para o painel!", Toast.LENGTH_LONG).show();

                                    CarrinhoManager.limparCarrinho();
                                    limparCupomAtivo();

                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new PedidosFragment())
                                            .commit();
                                } else {
                                    Toast.makeText(getContext(), "Erro: " + jsonObject.getString("mensagem"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> Toast.makeText(getContext(), "Erro de conexão ao finalizar pedido", Toast.LENGTH_SHORT).show()
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", emailUsuario);
                        params.put("endereco", enderecoAtual);
                        params.put("pagamento", pagamentoEscolhido);
                        params.put("total", String.valueOf(totalFinalPedido));
                        params.put("itens", jsonArrayItens.toString());
                        return params;
                    }
                };

                Volley.newRequestQueue(requireContext()).add(stringRequest);
            }
        });

        ImageView btnVoltarCheckout = view.findViewById(R.id.btnVoltarCheckout);
        if (btnVoltarCheckout != null) {
            btnVoltarCheckout.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }
    }

    private void mostrarDialogoSelecaoEnderecos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String emailUsuario = prefs.getString("email_usuario", "");

        if (emailUsuario.isEmpty()) {
            Toast.makeText(getContext(), "Utilizador não identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/api_hamburgueria/listar_enderecos.php?email=" + emailUsuario;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("sucesso")) {
                            JSONArray jsonArray = response.getJSONArray("enderecos");

                            if (jsonArray.length() == 0) {
                                Toast.makeText(getContext(), "Nenhum endereço cadastrado. Adicione um no seu perfil!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            // Inflar o layout customizado do diálogo
                            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_selecionar_endereco, null);
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                            builder.setView(dialogView);

                            androidx.appcompat.app.AlertDialog dialog = builder.create();
                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            }

                            RecyclerView recyclerDialog = dialogView.findViewById(R.id.recyclerDialogEnderecos);
                            recyclerDialog.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));

                            List<EnderecoModel> listaEnderecos = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject endObj = jsonArray.getJSONObject(i);
                                String ruaNum = endObj.getString("rua") + ", " + endObj.getString("numero");
                                String bairroCep = "Bairro: " + endObj.getString("bairro") + " | CEP: " + endObj.getString("cep");

                                listaEnderecos.add(new EnderecoModel(ruaNum, bairroCep));
                            }

                            // Usar um adapter adaptado para clique no item do diálogo
                            AdapterEnderecoDialog adapterDialog = new AdapterEnderecoDialog(listaEnderecos, requireContext(), (enderecoEscolhido) -> {
                                String formatado = enderecoEscolhido.getTitulo() + " - " + enderecoEscolhido.getRua();
                                tvEnderecoCheckout.setText(formatado);
                                prefs.edit().putString("endereco_usuario", formatado).apply();
                                Toast.makeText(getContext(), "Endereço de entrega atualizado!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });

                            recyclerDialog.setAdapter(adapterDialog);

                            View btnFechar = dialogView.findViewById(R.id.btnFecharDialog);
                            btnFechar.setOnClickListener(v -> dialog.dismiss());

                            dialog.show();

                        } else {
                            Toast.makeText(getContext(), "Erro ao carregar endereços", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro de conexão ao buscar endereços", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
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