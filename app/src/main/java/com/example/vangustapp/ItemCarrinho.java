package com.example.vangustapp;

import java.io.Serializable;

public class ItemCarrinho implements Serializable {
    private String titulo;
    private double preco;
    private int quantidade;
    private int imagem;

    public ItemCarrinho(String titulo, double preco, int quantidade, int imagem) {
        this.titulo = titulo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.imagem = imagem;
    }

    public String getTitulo() { return titulo; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public int getImagem() { return imagem; }
}