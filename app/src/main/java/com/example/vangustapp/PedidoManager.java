package com.example.vangustapp;

import java.util.ArrayList;
import java.util.List;

public class PedidoManager {
    private static List<PedidoModel> listaPedidos = new ArrayList<>();

    public static void adicionarPedido(PedidoModel pedido) {
        listaPedidos.add(pedido);
    }

    public static List<PedidoModel> getListaPedidos() {
        return listaPedidos;
    }
}