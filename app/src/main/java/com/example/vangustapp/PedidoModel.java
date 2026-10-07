package com.example.vangustapp;

import java.io.Serializable;
import java.util.List;

public class PedidoModel implements Serializable {
    private int idPedido;
    private String dataCriacao;
    private String endereco;
    private String pagamento;
    private double total;
    private String statusPedido;
    private List<ItemCarrinho> itens;

    // Construtor completo para os dados vindos da API / Banco de Dados
    public PedidoModel(int idPedido, String dataCriacao, String endereco, String pagamento, double total, String statusPedido, List<ItemCarrinho> itens) {
        this.idPedido = idPedido;
        this.dataCriacao = dataCriacao;
        this.endereco = endereco;
        this.pagamento = pagamento;
        this.total = total;
        this.statusPedido = statusPedido;
        this.itens = itens;
    }

    // Getters
    public int getIdPedido() { return idPedido; }
    public String getDataCriacao() { return dataCriacao; }
    public String getEndereco() { return endereco; }
    public String getPagamento() { return pagamento; }
    public double getTotal() { return total; }
    public String getStatusPedido() { return statusPedido; }
    public List<ItemCarrinho> getItens() { return itens; }
}