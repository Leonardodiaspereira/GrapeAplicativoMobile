package com.example.grapeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ConfirmacaoPedidoActivity extends AppCompatActivity {

    private TextView tvMensagem, tvEstimativaEntrega, tvContatoSuporte;
    private Button btnAcompanhePedido, btnVoltarInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pedido);

        // Inicializando componentes
        tvMensagem = findViewById(R.id.tvMensagem);
        tvEstimativaEntrega = findViewById(R.id.tvEstimativaEntrega);
        tvContatoSuporte = findViewById(R.id.tvContatoSuporte);
        btnAcompanhePedido = findViewById(R.id.btnMeusPedidos);
        btnVoltarInicio = findViewById(R.id.btnVoltarInicio);

        configurarTelaPedidoConfirmado();

        // Ação do botão "Acompanhe o Pedido" para enviar dados à PedidosActivity
        btnAcompanhePedido.setText("Acompanhe o Pedido");
        btnAcompanhePedido.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacaoPedidoActivity.this, PedidosActivity.class);
            int clienteId = getIntent().getIntExtra("clienteId", -1);
            if (clienteId != -1) {
                intent.putExtra("clienteId", clienteId); // Enviando o clienteId para a tela de pedidos
            }
            startActivity(intent);
            finish();
        });

        // Ação do botão "Continuar Comprando"
        btnVoltarInicio.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacaoPedidoActivity.this, ProdutosActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void configurarTelaPedidoConfirmado() {
        // Mensagem de agradecimento e outras informações
        tvMensagem.setText("Obrigado por seu pedido!");
        tvEstimativaEntrega.setText("Entrega em até 24 horas.");
        tvContatoSuporte.setText("Contato para suporte: (11) 3333-1552");
    }
}
