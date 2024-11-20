package com.example.grapeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.grapeapp.api.ApiClient;
import com.example.grapeapp.api.ApiService;
import com.example.grapeapp.model.Pedido;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.grapeapp.model.ItemPedido;


import java.util.List;

public class PedidosActivity extends AppCompatActivity {

    private TextView tvResumoPedidos;
    private Button btnAtualizarPedidos;
    private ApiService apiService;
    private int clienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        // Inicializar componentes
        tvResumoPedidos = findViewById(R.id.tvResumoPedidos);
        btnAtualizarPedidos = findViewById(R.id.btnAtualizarPedidos);

        // Inicializar o serviço API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Obter o clienteId a partir da Intent ou do SharedPreferences
        clienteId = getIntent().getIntExtra("clienteId", -1);
        if (clienteId == -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            clienteId = sharedPreferences.getInt("clienteId", -1);
        }

        if (clienteId == -1) {
            Toast.makeText(this, "Erro: ID do cliente não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Obter pedidos do cliente inicialmente
        obterPedidos();

        // Configurar ação do botão para atualizar os pedidos
        btnAtualizarPedidos.setOnClickListener(v -> obterPedidos());
    }

    private void obterPedidos() {
        Call<List<Pedido>> call = apiService.obterPedidos(clienteId);
        call.enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pedido> pedidos = response.body();
                    StringBuilder resumo = new StringBuilder();

                    if (!pedidos.isEmpty()) {
                        for (Pedido pedido : pedidos) {
                            resumo.append("### RESUMO DO PEDIDO ###\n")
                                    .append("ID do Pedido: #")
                                    .append(pedido.getId()) // Obtém o ID do pedido
                                    .append("\nProdutos: ");

                            // Adicionar produtos do pedido
                            if (pedido.getItensPedido() != null && !pedido.getItensPedido().isEmpty()) {
                                for (ItemPedido item : pedido.getItensPedido()) {
                                    resumo.append("\n- ")
                                            .append(item.getNomeProduto())
                                            .append(" (Quantidade: ")
                                            .append(item.getQuantidade())
                                            .append(" kg)");
                                }
                            } else {
                                resumo.append("\nNenhum produto encontrado");
                            }

                            // Mostrar o valor total do pedido
                            resumo.append("\nValor Total: R$ ")
                                    .append(String.format("%.2f", pedido.getValorTotal())) // Valor total formatado
                                    .append("\nStatus: ")
                                    .append(pedido.getStatus()) // Obtém o status atual do pedido
                                    .append("\n\nContato para suporte: (11) 3333-1552\n\n");
                        }
                        tvResumoPedidos.setText(resumo.toString());
                    } else {
                        tvResumoPedidos.setText("Nenhum pedido realizado ainda, volte para a aba de produtos e realize seu pedido.");
                    }
                } else {
                    Toast.makeText(PedidosActivity.this, "Erro ao carregar pedidos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                Toast.makeText(PedidosActivity.this, "Erro ao carregar pedidos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}