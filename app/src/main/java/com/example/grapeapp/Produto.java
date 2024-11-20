package com.example.grapeapp; // Atualizei o pacote para model

import android.os.Parcel;
import android.os.Parcelable;

public class Produto implements Parcelable {
    private int id; // Adicionando um campo de ID
    private String nome;
    private String descricao;
    private double precoPorKg;
    private int imagem;
    private int quantidade;  // Campo para a quantidade

    // Construtor sem ID para criar produtos  testes 1
    public Produto(String nome, String descricao, double precoPorKg, int imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.precoPorKg = precoPorKg;
        this.imagem = imagem;
        this.quantidade = 0; // Definindo quantidade como 0 inicialmente
    }

    // Construtor com ID
    public Produto(int id, String nome, String descricao, double precoPorKg, int imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoPorKg = precoPorKg;
        this.imagem = imagem;
        this.quantidade = 0; // Definindo quantidade como 0 inicialmente
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoPorKg() {
        return precoPorKg;
    }

    public int getImagem() {
        return imagem;
    }

    // Método getQuantidade
    public int getQuantidade() {
        return quantidade;
    }

    // Método setQuantidade para alterar a quantidade
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }


    protected Produto(Parcel in) {
        id = in.readInt();
        nome = in.readString();
        descricao = in.readString();
        precoPorKg = in.readDouble();
        imagem = in.readInt();
        quantidade = in.readInt();
    }

    public static final Creator<Produto> CREATOR = new Creator<Produto>() {
        @Override
        public Produto createFromParcel(Parcel in) {
            return new Produto(in);
        }

        @Override
        public Produto[] newArray(int size) {
            return new Produto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nome);
        dest.writeString(descricao);
        dest.writeDouble(precoPorKg);
        dest.writeInt(imagem);
        dest.writeInt(quantidade); // Salvando a quantidade no parcelable
    }
}
