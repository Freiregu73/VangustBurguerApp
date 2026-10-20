package com.example.vangustapp;

public class ItensCard {
    private int idProduto;
    private String titulo;
    private String descricao;
    private String imageUrl;
    private double preco;
    private boolean isFavorito;

    public ItensCard(int idProduto, String titulo, String descricao, String imageUrl, double preco, boolean isFavorito) {
        this.idProduto = idProduto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.preco = preco;
        this.isFavorito = isFavorito;
    }

    public int getIdProduto() { return idProduto; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getImageUrl() { return imageUrl; }
    public double getPreco() { return preco; }
    public boolean isFavorito() { return isFavorito; }
    public void setFavorito(boolean favorito) { isFavorito = favorito; }
}