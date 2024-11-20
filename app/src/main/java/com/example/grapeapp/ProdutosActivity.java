package com.example.grapeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapeapp.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutosActivity extends AppCompatActivity {

    private TextView tvValorTotal;
    private double valorTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        RecyclerView recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos);
        tvValorTotal = findViewById(R.id.tvValorTotal);
        Button btnProximo = findViewById(R.id.btnProximo);

        // o RecyclerView
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));

        // Criando a lista de produtos (dados fictícios)
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto(4, "Autumn Crisp", "Crocante, suculenta, saborosa, doce", 15.00, R.drawable.autumn_crisp));
        produtos.add(new Produto(5, "Melodia", "Levemente adocicada, suave, aromática", 12.00, R.drawable.melodia));
        produtos.add(new Produto(2, "Uva Passa", "Doce, macia, concentrada, nutritiva", 10.00, R.drawable.uva_passa));
        produtos.add(new Produto(6, "Arra 34", "Firme, crocante, equilibrada, sem sementes", 14.00, R.drawable.arra_34));
        produtos.add(new Produto(3, "Scarlotta", "Vermelha, doce, encorpada, vibrante", 18.00, R.drawable.scarlotta));
        produtos.add(new Produto(7, "Uva Thompson", "Verde, sem sementes, crocante, refrescante", 13.00, R.drawable.uva_thompson));


        // Configurando o adaptador
        ProdutosAdapter adapter = new ProdutosAdapter(produtos, total -> {
            valorTotal = total;
            tvValorTotal.setText(getString(R.string.valor_total, String.format("%.2f", valorTotal)));
        });
        recyclerViewProdutos.setAdapter(adapter);

        btnProximo.setOnClickListener(v -> {
            ArrayList<Produto> produtosSelecionados = new ArrayList<>();
            for (Produto produto : produtos) {
                if (produto.getQuantidade() > 0) { // Adiciona apenas produtos com quantidade > 0
                    produtosSelecionados.add(produto);
                }
            }
            Intent intent = new Intent(ProdutosActivity.this, CheckoutActivity.class);
            intent.putParcelableArrayListExtra("produtos_selecionados", produtosSelecionados);
            intent.putExtra("valor_total_compra", valorTotal); // Envia o valor total
            startActivity(intent);
        });

        Button btnPedidos = findViewById(R.id.btnPedidos); // Suponha que o botão de pedidos tenha esse ID
        btnPedidos.setOnClickListener(v -> {
            Intent intent = new Intent(ProdutosActivity.this, PedidosActivity.class);
            startActivity(intent);
        });

        // Novo botão "Meus Dados"
        Button btnMeusDados = findViewById(R.id.btnMeusDados);
        btnMeusDados.setOnClickListener(v -> {
            Intent intent = new Intent(ProdutosActivity.this, MeusDadosActivity.class);
            startActivity(intent);
        });
    }
}
