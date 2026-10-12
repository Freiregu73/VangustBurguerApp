package com.example.vangustapp;

public class FavoritosCard {

    private String titulo;
    private String descricao;
    private int imgfavoritos;
    private String imageUrl; // Suporte para URL da API
    private double preco;

    // Construtor para dados vindos da API
    public FavoritosCard(String titulo, String descricao, String imageUrl, double preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.preco = preco;
        this.imgfavoritos = R.drawable.brutao; // Fallback
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getImgfavoritos() {
        return imgfavoritos;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}