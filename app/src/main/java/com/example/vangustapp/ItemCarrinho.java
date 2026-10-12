package com.example.vangustapp;

import java.io.Serializable;

public class ItemCarrinho implements Serializable {
    private String titulo;
    private double preco;
    private int quantidade;
    private String imageUrl; // Suporte para URL da API
    private int imagemLocal; // Fallback para int se necessário

    // Construtor para URL da API
    public ItemCarrinho(String titulo, double preco, int quantidade, String imageUrl) {
        this.titulo = titulo;
        this.preco = preco;
        this.quantidade = quantidade;
        this.imageUrl = imageUrl;
        this.imagemLocal = R.drawable.brutao;
    }

    public String getTitulo() { return titulo; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getImageUrl() { return imageUrl; }
    public int getImagemLocal() { return imagemLocal; }
}