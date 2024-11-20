package com.example.grapeapp.model;

import java.util.List;

public class Pedido {
    private int id;
    private int clienteId;
    private String nomeCompleto;
    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
    private String formaPagamento;
    private String dataPedido;
    private double valorTotal;
    private String status;
    private List<ItemPedido> itensPedido;

    // Construtor completo para a classe Pedido, incluindo o id
    public Pedido(int id, int clienteId, String nomeCompleto, String rua, String numero, String cidade, String estado, String cep, String formaPagamento, String dataPedido, double valorTotal, String status, List<ItemPedido> itensPedido) {
        this.id = id;
        this.clienteId = clienteId;
        this.nomeCompleto = nomeCompleto;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.formaPagamento = formaPagamento;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.status = status;
        this.itensPedido = itensPedido;
    }

    // Construtor alternativo (sem o id)
    public Pedido(int clienteId, String nomeCompleto, String rua, String numero, String cidade, String estado, String cep, String formaPagamento, String dataPedido, double valorTotal, String status, List<ItemPedido> itensPedido) {
        this.clienteId = clienteId;
        this.nomeCompleto = nomeCompleto;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.formaPagamento = formaPagamento;
        this.dataPedido = dataPedido;
        this.valorTotal = valorTotal;
        this.status = status;
        this.itensPedido = itensPedido;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemPedido> getItensPedido() {
        return itensPedido;
    }

    public void setItensPedido(List<ItemPedido> itensPedido) {
        this.itensPedido = itensPedido;
    }
}
