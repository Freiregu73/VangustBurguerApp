package com.example.vangustapp;

public class EnderecoModel {
    private String titulo;
    private String rua;

    public EnderecoModel(String titulo, String rua) {
        this.titulo = titulo;
        this.rua = rua;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getRua() {
        return rua;
    }
}