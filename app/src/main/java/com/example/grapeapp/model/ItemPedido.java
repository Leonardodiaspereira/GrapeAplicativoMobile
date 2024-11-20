package com.example.grapeapp.model;

public class ItemPedido {
    private int id;
    private int pedidoId;
    private int produtoId;
    private int quantidade;
    private String nomeProduto;


    public ItemPedido(int produtoId, int quantidade, String nomeProduto) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.nomeProduto = nomeProduto;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }
}
