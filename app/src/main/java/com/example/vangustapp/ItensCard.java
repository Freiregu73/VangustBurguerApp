package com.example.vangustapp;

public class ItensCard {
    private int idProduto; // ID vindo da base de dados MySQL
    private String titulo;
    private String descricao;
    private String imageUrl;
    private double preco;
    private int imgitens;

    // Construtor completo com ID
    public ItensCard(int idProduto, String titulo, String descricao, String imageUrl, double preco) {
        this.idProduto = idProduto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.preco = preco;
        this.imgitens = R.drawable.brutao; // Fallback local
    }

    public int getIdProduto() { return idProduto; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getImageUrl() { return imageUrl; }
    public double getPreco() { return preco; }
    public int getImgitens() { return imgitens; }
}