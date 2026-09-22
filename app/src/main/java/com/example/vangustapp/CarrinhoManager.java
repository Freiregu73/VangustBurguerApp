package com.example.vangustapp;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoManager {
    private static List<ItemCarrinho> listaCarrinho = new ArrayList<>();

    public static void adicionarItem(ItemCarrinho item) {
        // Verifica se o item já existe no carrinho para aumentar a quantidade
        boolean encontrado = false;
        for (ItemCarrinho i : listaCarrinho) {
            if (i.getTitulo().equals(item.getTitulo())) {
                i.setQuantidade(i.getQuantidade() + item.getQuantidade());
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            listaCarrinho.add(item);
        }
    }

    public static List<ItemCarrinho> getListaCarrinho() {
        return listaCarrinho;
    }

    public static double calcularTotal() {
        double total = 0;
        for (ItemCarrinho i : listaCarrinho) {
            total += i.getPreco() * i.getQuantidade();
        }
        return total;
    }

    public static void limparCarrinho() {
        listaCarrinho.clear();
    }
}