package com.example.vangustapp;

public class EnderecoModel {
    private int idEndereco; // Novo campo para o ID real da base de dados
    private String titulo;
    private String rua;

    // Construtor atualizado
    public EnderecoModel(int idEndereco, String titulo, String rua) {
        this.idEndereco = idEndereco;
        this.titulo = titulo;
        this.rua = rua;
    }

    public int getIdEndereco() { return idEndereco; }
    public String getTitulo() { return titulo; }
    public String getRua() { return rua; }
}