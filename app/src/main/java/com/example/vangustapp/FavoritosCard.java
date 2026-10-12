package com.example.vangustapp;

public class FavoritosCard {

    private int idProduto;
    private String titulo;
    private String descricao;
    private int imgfavoritos;
    private String imageUrl;
    private double preco;

    public FavoritosCard(int idProduto, String titulo, String descricao, String imageUrl, double preco) {
        this.idProduto = idProduto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.preco = preco;
        this.imgfavoritos = R.drawable.brutao;
    }

    public int getIdProduto() { return idProduto; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public int getImgfavoritos() { return imgfavoritos; }
    public String getImageUrl() { return imageUrl; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
}