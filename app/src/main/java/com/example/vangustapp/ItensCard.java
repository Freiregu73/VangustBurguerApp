package com.example.vangustapp;

public class ItensCard {

    private String titulo;
    private String descricao;
    private int imgitens;
    private double preco;

    // Construtor completo
    public ItensCard(String titulo, String descricao, int imgitens, double preco) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.imgitens = imgitens;
        this.preco = preco;
    }

    // Getters e Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getImgitens() { return imgitens; }
    public void setImgitens(int imgitens) { this.imgitens = imgitens; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
}