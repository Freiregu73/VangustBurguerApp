package com.example.vangustapp;

public class ItensCard {

    private String titulo;

    private int imgitens;


    public ItensCard(String titulo, int imgitens) {
        this.titulo = titulo;
        this.imgitens = imgitens;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getImgitens() {
        return imgitens;
    }

    public void setImgitens(int imgitens) {
        this.imgitens = imgitens;
    }
}
