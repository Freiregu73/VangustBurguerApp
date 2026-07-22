package com.example.vangustapp;

public class FavoritosCard {

    private String titulo;
    private String descricao;
    private int imgfavoritos;
    private double preco;

    public FavoritosCard(String titulo, String descricao, int imgfavoritos, double preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imgfavoritos = imgfavoritos;
        this.preco = preco;
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

    public void setImgfavoritos(int imgfavoritos) {
        this.imgfavoritos = imgfavoritos;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}
