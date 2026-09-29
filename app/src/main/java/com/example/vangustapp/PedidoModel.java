package com.example.vangustapp;

import java.io.Serializable;
import java.util.List;

public class PedidoModel implements Serializable {
    private String endereco;
    private String pagamento;
    private double total;
    private List<ItemCarrinho> itens;

    public PedidoModel(String endereco, String pagamento, double total, List<ItemCarrinho> itens) {
        this.endereco = endereco;
        this.pagamento = pagamento;
        this.total = total;
        this.itens = itens;
    }

    public String getEndereco() { return endereco; }
    public String getPagamento() { return pagamento; }
    public double getTotal() { return total; }
    public List<ItemCarrinho> getItens() { return itens; }
}