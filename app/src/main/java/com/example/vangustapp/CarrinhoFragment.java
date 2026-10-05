package com.example.vangustapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CarrinhoFragment extends Fragment {

    private RecyclerView idRecCarrinho;
    private TextView tvTotalCarrinho, tvTaxaEntrega, tvStatusCupomCarrinho;
    private LinearLayout layoutAtalhoCupom;
    private Button btnFinalizarCompra;
    private CarrinhoAdapter carrinhoAdapter;

    private static final double TAXA_ENTREGA = 5.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carrinho, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idRecCarrinho = view.findViewById(R.id.idRecCarrinho);
        tvTotalCarrinho = view.findViewById(R.id.tvTotalCarrinho);
        tvTaxaEntrega = view.findViewById(R.id.tvTaxaEntrega);
        btnFinalizarCompra = view.findViewById(R.id.btnFinalizarCompra);
        tvStatusCupomCarrinho = view.findViewById(R.id.tvStatusCupomCarrinho);
        layoutAtalhoCupom = view.findViewById(R.id.layoutAtalhoCupom);

        idRecCarrinho.setLayoutManager(new LinearLayoutManager(getContext()));
        carrinhoAdapter = new CarrinhoAdapter(CarrinhoManager.getListaCarrinho());

        carrinhoAdapter.setOnCarrinhoChangedListener(() -> atualizarTotais());
        idRecCarrinho.setAdapter(carrinhoAdapter);

        // Ação para abrir a tela de cupons diretamente do carrinho
        if (layoutAtalhoCupom != null) {
            layoutAtalhoCupom.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CupomActivity.class);
                startActivity(intent);
            });
        }

        atualizarTotais();

        btnFinalizarCompra.setOnClickListener(v -> {
            if (CarrinhoManager.getListaCarrinho().isEmpty()) {
                Toast.makeText(getContext(), "O seu carrinho está vazio!", Toast.LENGTH_SHORT).show();
            } else {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CheckoutFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView btnVoltarCarrinho = view.findViewById(R.id.btnVoltarCarrinho);
        if (btnVoltarCarrinho != null) {
            btnVoltarCarrinho.setOnClickListener(v -> {
                // Volta para o fragmento anterior (ex: Cardápio ou Início)
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }
    }

    private void atualizarTotais() {
        double subtotal = CarrinhoManager.calcularTotal();
        double desconto = calcularDescontoCupom(subtotal);
        double totalGeral = subtotal > 0 ? (subtotal - desconto) + TAXA_ENTREGA : 0.00;

        if (totalGeral < 0) totalGeral = TAXA_ENTREGA;

        // Atualizar o texto do cupom ativo na UI do carrinho
        SharedPreferences prefs = requireActivity().getSharedPreferences("VangustPrefs", Context.MODE_PRIVATE);
        String cupomAtivo = prefs.getString("cupom_ativo", "");
        if (tvStatusCupomCarrinho != null) {
            if (!cupomAtivo.isEmpty()) {
                tvStatusCupomCarrinho.setText("Cupom aplicado: " + cupomAtivo);
            } else {
                tvStatusCupomCarrinho.setText("Toque aqui para adicionar um cupom");
            }
        }

        if (tvTaxaEntrega != null) {
            tvTaxaEntrega.setText(subtotal > 0 ? String.format("R$ %.2f", TAXA_ENTREGA) : "R$ 0,00");
        }
        if (tvTotalCarrinho != null) {
            tvTotalCarrinho.setText(String.format("R$ %.2f", totalGeral));
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (carrinhoAdapter != null) {
            carrinhoAdapter.notifyDataSetChanged();
            atualizarTotais(); // Atualiza caso tenha escolhido um cupom e voltado para o carrinho
        }
    }
}