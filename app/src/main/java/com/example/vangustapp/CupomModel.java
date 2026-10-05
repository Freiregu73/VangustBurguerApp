package com.example.vangustapp;

public class CupomModel {
    private String codigo;
    private String descricao;
    private double valorDesconto; // Ex: 0.15 para 15% ou valor fixo

    public CupomModel(String codigo, String descricao, double valorDesconto) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.valorDesconto = valorDesconto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }
}