package com.example.grapeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapeapp.Produto;

import java.util.List;
import java.util.Locale;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ViewHolder> {

    private final List<Produto> produtos;
    private final OnTotalChangeListener onTotalChangeListener;
    private double valorTotal = 0;

    public ProdutosAdapter(List<Produto> produtos, OnTotalChangeListener listener) {
        this.produtos = produtos;
        this.onTotalChangeListener = listener;
    }

    public interface OnTotalChangeListener {
        void onTotalChange(double total);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produto produto = produtos.get(position);

        // Configura o nome, descrição e preço
        holder.tvNome.setText(produto.getNome());
        holder.tvDescricao.setText(produto.getDescricao());
        holder.tvPrecoPorKg.setText(holder.itemView.getContext().getString(
                R.string.preco_por_kg, String.format(Locale.getDefault(), "%.2f", produto.getPrecoPorKg())
        ));

        // Configura a imagem do produto com base no nome
        switch (produto.getNome()) {
            case "Autumn Crisp":
                holder.imgProduto.setImageResource(R.drawable.autumn_crisp);
                break;
            case "Melodia":
                holder.imgProduto.setImageResource(R.drawable.melodia);
                break;
            case "Uva Passa":
                holder.imgProduto.setImageResource(R.drawable.uva_passa);
                break;
            case "Arra 34":
                holder.imgProduto.setImageResource(R.drawable.arra_34);
                break;
            case "Scarlotta":
                holder.imgProduto.setImageResource(R.drawable.scarlotta);
                break;
            case "Uva Thompson":
                holder.imgProduto.setImageResource(R.drawable.uva_thompson);
                break;
        }

        // Listener para atualizar o valor total baseado na quantidade
        holder.edtQuantidade.addTextChangedListener(new TextWatcher() {
            private double valorAntigo = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    holder.edtQuantidade.removeTextChangedListener(this); // Remove temporariamente o listener para evitar loops

                    // Quantidade atual no campo de texto
                    int quantidade = s.toString().isEmpty() ? 1 : Math.max(1, Integer.parseInt(s.toString())); // Bloqueia para mínimo de 1 kg
                    double valorAtual = quantidade * produto.getPrecoPorKg();

                    // Atualiza o valor total considerando a mudança
                    valorTotal = valorTotal - valorAntigo + valorAtual;
                    valorAntigo = valorAtual; // Atualiza para o próximo cálculo

                    // Define a quantidade atual no objeto Produto
                    produto.setQuantidade(quantidade);

                    // Atualiza o campo de quantidade para garantir que o valor seja mínimo de 1
                    holder.edtQuantidade.setText(String.valueOf(quantidade));
                    holder.edtQuantidade.setSelection(holder.edtQuantidade.getText().length());

                    // Notifica o listener da mudança
                    onTotalChangeListener.onTotalChange(valorTotal);

                    holder.edtQuantidade.addTextChangedListener(this); // Adiciona novamente o listener
                } catch (NumberFormatException e) {
                    valorTotal -= valorAntigo;
                    valorAntigo = 0;
                    onTotalChangeListener.onTotalChange(valorTotal);
                    holder.edtQuantidade.addTextChangedListener(this); // Adiciona novamente o listener
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduto;
        TextView tvNome, tvDescricao, tvPrecoPorKg;
        EditText edtQuantidade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvPrecoPorKg = itemView.findViewById(R.id.tvPrecoPorKg);
            edtQuantidade = itemView.findViewById(R.id.edtQuantidade);
        }
    }
}
