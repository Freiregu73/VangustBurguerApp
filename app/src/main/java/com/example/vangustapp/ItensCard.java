package com.example.vangustapp;

public class ItensCard {
    private String titulo;
    private String descricao;
    private int imgitens; // Mantido para compatibilidade se usar estático
    private String imageUrl; // Nova variável para a URL da API
    private double preco;

    // Construtor para dados vindos da API
    public ItensCard(String titulo, String descricao, String imageUrl, double preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.preco = preco;
        this.imgitens = R.drawable.brutao; // Fallback
    }

    // Construtor antigo (caso use estático em algum lugar)
    public ItensCard(String titulo, String descricao, int imgitens, double preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imgitens = imgitens;
        this.imageUrl = null;
        this.preco = preco;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public int getImgitens() { return imgitens; }
    public String getImageUrl() { return imageUrl; }
    public double getPreco() { return preco; }
}