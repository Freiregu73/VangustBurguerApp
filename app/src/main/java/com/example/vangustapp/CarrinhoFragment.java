package com.example.vangustapp;

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
import android.widget.TextView;
import android.widget.Toast;

public class CarrinhoFragment extends Fragment {

    private RecyclerView idRecCarrinho;
    private TextView tvTotalCarrinho, tvTaxaEntrega;
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

        // Ligar com os IDs reais do fragment_carrinho.xml
        idRecCarrinho = view.findViewById(R.id.idRecCarrinho);
        tvTotalCarrinho = view.findViewById(R.id.tvTotalCarrinho);
        tvTaxaEntrega = view.findViewById(R.id.tvTaxaEntrega);
        btnFinalizarCompra = view.findViewById(R.id.btnFinalizarCompra);

        idRecCarrinho.setLayoutManager(new LinearLayoutManager(getContext()));
        carrinhoAdapter = new CarrinhoAdapter(CarrinhoManager.getListaCarrinho());

        // Sempre que alterar a quantidade no adapter, atualiza os totais no ecrã
        carrinhoAdapter.setOnCarrinhoChangedListener(() -> atualizarTotais());
        idRecCarrinho.setAdapter(carrinhoAdapter);

        atualizarTotais();

        // Ação do botão de finalizar pedido
        btnFinalizarCompra.setOnClickListener(v -> {
            if (CarrinhoManager.getListaCarrinho().isEmpty()) {
                Toast.makeText(getContext(), "O seu carrinho está vazio!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Pedido finalizado com sucesso!", Toast.LENGTH_LONG).show();
                CarrinhoManager.limparCarrinho();
                carrinhoAdapter.notifyDataSetChanged();
                atualizarTotais();
            }
        });
    }

    private void atualizarTotais() {
        double subtotal = CarrinhoManager.calcularTotal();
        double totalGeral = subtotal > 0 ? subtotal + TAXA_ENTREGA : 0.00;

        if (tvTaxaEntrega != null) {
            tvTaxaEntrega.setText(subtotal > 0 ? String.format("R$ %.2f", TAXA_ENTREGA) : "R$ 0,00");
        }
        if (tvTotalCarrinho != null) {
            tvTotalCarrinho.setText(String.format("R$ %.2f", totalGeral));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (carrinhoAdapter != null) {
            carrinhoAdapter.notifyDataSetChanged();
            atualizarTotais();
        }
    }
}