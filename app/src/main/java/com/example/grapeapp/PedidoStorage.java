package com.example.grapeapp;

import com.example.grapeapp.model.Pedido; // Importação correta da classe Pedido
import java.util.ArrayList;
import java.util.List;

public class PedidoStorage {
    // Lista estática para armazenar todos os pedidos concluídos
    private static List<Pedido> pedidosConcluidos = new ArrayList<>();

    // Adiciona um novo pedido à lista de pedidos concluídos
    public static void adicionarPedido(Pedido pedido) {
        pedidosConcluidos.add(pedido);  // Cada novo pedido é adicionado ao final da lista
    }

    // Retorna a lista completa de pedidos concluídos
    public static List<Pedido> getPedidosConcluidos() {
        return new ArrayList<>(pedidosConcluidos);  // Retorna uma cópia para evitar alterações externas diretas
    }
}
