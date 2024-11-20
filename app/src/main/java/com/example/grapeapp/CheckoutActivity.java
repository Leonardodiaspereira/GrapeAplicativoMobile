package com.example.grapeapp;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.content.Intent;

import com.example.grapeapp.api.ApiClient;
import com.example.grapeapp.api.ApiService;
import com.example.grapeapp.model.Pedido;
import com.example.grapeapp.model.ItemPedido;
import com.example.grapeapp.Produto;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvProdutos, tvTotalCompra, tvPixObservacao;
    private EditText edtNome, edtRua, edtNumero, edtCidade, edtEstado, edtCep;
    private EditText edtNumeroCartao, edtNomeCartao, edtValidadeCartao, edtCvvCartao;
    private Button btnConfirmarCompra;
    private RadioGroup radioGroupPagamento;
    private RadioButton radioCartaoCredito, radioCartaoDebito, radioPIX;

    private ApiService apiService;
    private List<Produto> produtosSelecionados;
    private double valorTotalCompra;
    private int clienteId;

    private AlertDialog alertDialogPix; // Referência para o AlertDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Inicializando componentes
        tvProdutos = findViewById(R.id.tvProdutos);
        tvTotalCompra = findViewById(R.id.tvTotalCompra);
        edtNome = findViewById(R.id.edtNome);
        edtRua = findViewById(R.id.edtRua);
        edtNumero = findViewById(R.id.edtNumero);
        edtCidade = findViewById(R.id.edtCidade);
        edtEstado = findViewById(R.id.edtEstado);
        edtCep = findViewById(R.id.edtCep);
        edtNumeroCartao = findViewById(R.id.edtNumeroCartao);
        edtNomeCartao = findViewById(R.id.edtNomeCartao);
        edtValidadeCartao = findViewById(R.id.edtValidadeCartao);
        edtCvvCartao = findViewById(R.id.edtCvvCartao);
        btnConfirmarCompra = findViewById(R.id.btnConfirmarCompra);
        radioGroupPagamento = findViewById(R.id.radioGroupPagamento);
        radioCartaoCredito = findViewById(R.id.radioCartaoCredito);
        radioCartaoDebito = findViewById(R.id.radioCartaoDebito);
        radioPIX = findViewById(R.id.radioPIX);
        tvPixObservacao = findViewById(R.id.tvPixObservacao);

        // Inicializando o serviço API
        apiService = ApiClient.getClient().create(ApiService.class);

        configurarFiltrosCampos();

        // Obtendo produtos selecionados e valor total da intent
        produtosSelecionados = getIntent().getParcelableArrayListExtra("produtos_selecionados");

        // Verificando se produtosSelecionados está vazio
        if (produtosSelecionados == null || produtosSelecionados.isEmpty()) {
            System.err.println("Erro: Nenhum produto foi selecionado.");
            Toast.makeText(this, "Erro: Nenhum produto foi selecionado.", Toast.LENGTH_SHORT).show();
            return; // Finaliza o processo caso não haja produtos selecionados
        } else {
            System.out.println("Número de produtos selecionados: " + produtosSelecionados.size());
        }

        valorTotalCompra = getIntent().getDoubleExtra("valor_total_compra", 0.00);

        // Obtendo clienteId da intent ou do SharedPreferences
        clienteId = getIntent().getIntExtra("cliente_id", -1);
        if (clienteId == -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            clienteId = sharedPreferences.getInt("clienteId", -1);
        }

        if (clienteId == -1) {
            Toast.makeText(this, "Erro: ID do cliente não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        exibirProdutosComprados(produtosSelecionados);
        tvTotalCompra.setText(String.format("Valor Total: R$%.2f", valorTotalCompra));

        btnConfirmarCompra.setOnClickListener(v -> confirmarCompra());

        radioGroupPagamento.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == radioCartaoCredito.getId() || checkedId == radioCartaoDebito.getId()) {
                mostrarCamposCartao(true);
                tvPixObservacao.setVisibility(View.GONE);
            } else if (checkedId == radioPIX.getId()) {
                mostrarCamposCartao(false);
                tvPixObservacao.setVisibility(View.VISIBLE);
            } else {
                mostrarCamposCartao(false);
                tvPixObservacao.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Garantir que o AlertDialog seja descartado quando a Activity for destruída
        if (alertDialogPix != null && alertDialogPix.isShowing()) {
            alertDialogPix.dismiss();
        }
    }

    private void configurarFiltrosCampos() {
        edtNumeroCartao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        edtNumeroCartao.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtNumeroCartao.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;

                StringBuilder formatted = new StringBuilder();
                String digitsOnly = s.toString().replaceAll("\\.", "");
                for (int i = 0; i < digitsOnly.length(); i++) {
                    formatted.append(digitsOnly.charAt(i));
                    if ((i + 1) % 4 == 0 && i + 1 < digitsOnly.length()) {
                        formatted.append(".");
                    }
                }

                edtNumeroCartao.setText(formatted.toString());
                edtNumeroCartao.setSelection(formatted.length());
                isUpdating = false;
            }
        });

        edtCvvCartao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        edtCvvCartao.setInputType(InputType.TYPE_CLASS_NUMBER);

        edtValidadeCartao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        edtValidadeCartao.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                isUpdating = true;

                String val = s.toString().replace("/", "");
                if (val.length() >= 2) {
                    val = val.substring(0, 2) + "/" + val.substring(2);
                }
                edtValidadeCartao.setText(val);
                edtValidadeCartao.setSelection(val.length());

                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void exibirProdutosComprados(List<Produto> produtos) {
        StringBuilder produtosStr = new StringBuilder();
        for (Produto produto : produtos) {
            produtosStr.append(produto.getNome())
                    .append(" - Quantidade: ")
                    .append(produto.getQuantidade())
                    .append(" kg\n");
        }
        tvProdutos.setText(produtosStr.toString());
    }

    private void confirmarCompra() {
        // Verificar se todos os campos necessários estão preenchidos
        if (edtNome.getText().toString().isEmpty() ||
                edtRua.getText().toString().isEmpty() ||
                edtNumero.getText().toString().isEmpty() ||
                edtCidade.getText().toString().isEmpty() ||
                edtEstado.getText().toString().isEmpty() ||
                edtCep.getText().toString().isEmpty()) {

            Toast.makeText(this, "Por favor, preencha todos os campos de endereço.", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPaymentId = radioGroupPagamento.getCheckedRadioButtonId();

        if (selectedPaymentId == radioCartaoCredito.getId() || selectedPaymentId == radioCartaoDebito.getId()) {
            if (edtNumeroCartao.getText().toString().isEmpty() ||
                    edtNomeCartao.getText().toString().isEmpty() ||
                    edtValidadeCartao.getText().toString().isEmpty() ||
                    edtCvvCartao.getText().toString().isEmpty()) {

                Toast.makeText(this, "Por favor, preencha todos os campos do cartão.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Enviar o pedido para o backend diretamente
            enviarPedidoParaApi();
        } else if (selectedPaymentId == radioPIX.getId()) {
            mostrarChavePix();
        } else {
            Toast.makeText(this, "Por favor, selecione um método de pagamento.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void enviarPedidoParaApi() {
        String nomeCompleto = edtNome.getText().toString();
        String rua = edtRua.getText().toString();
        String numero = edtNumero.getText().toString();
        String cidade = edtCidade.getText().toString();
        String estado = edtEstado.getText().toString();
        String cep = edtCep.getText().toString();
        String formaPagamento = getFormaPagamentoSelecionada();
        String dataPedido = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Remover caracteres especiais antes de enviar ao backend
        numero = numero.replaceAll("[^\\d]", "");
        cep = cep.replaceAll("[^\\d]", "");
        estado = estado.replaceAll("[^a-zA-Z]", "");

        // Adicionando logs para verificar os valores obtidos
        System.out.println("Nome Completo: " + nomeCompleto);
        System.out.println("Rua: " + rua);
        System.out.println("Numero: " + numero);
        System.out.println("Cidade: " + cidade);
        System.out.println("Estado: " + estado);
        System.out.println("CEP: " + cep);
        System.out.println("Forma de Pagamento: " + formaPagamento);
        System.out.println("Data do Pedido: " + dataPedido);
        System.out.println("Cliente ID: " + clienteId);

        // Verificar se algum campo está vazio
        if (nomeCompleto.isEmpty() || rua.isEmpty() || numero.isEmpty() || cidade.isEmpty() || estado.isEmpty() || cep.isEmpty() || formaPagamento == null || clienteId == -1) {
            System.err.println("Erro: Dados incompletos fornecidos para o pedido.");
            Toast.makeText(CheckoutActivity.this, "Erro: Dados incompletos fornecidos para o pedido.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ItemPedido> itensPedido = new ArrayList<>();
        for (Produto produto : produtosSelecionados) {
            if (produto.getQuantidade() <= 0) {
                System.err.println("Erro: Quantidade do produto " + produto.getNome() + " é inválida.");
                Toast.makeText(CheckoutActivity.this, "Erro: Quantidade do produto " + produto.getNome() + " inválida.", Toast.LENGTH_SHORT).show();
                return;
            }
            ItemPedido item = new ItemPedido(produto.getId(), produto.getQuantidade(), produto.getNome());



            itensPedido.add(item);
        }

        if (itensPedido.isEmpty()) {
            System.err.println("Erro: Nenhum item do pedido foi selecionado.");
            Toast.makeText(CheckoutActivity.this, "Erro: Nenhum item do pedido foi selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("Itens do Pedido: ");
        for (ItemPedido item : itensPedido) {
            System.out.println("Produto ID: " + item.getProdutoId() + ", Quantidade: " + item.getQuantidade());
        }

        Pedido pedido = new Pedido(clienteId, nomeCompleto, rua, numero, cidade, estado, cep, formaPagamento, dataPedido, valorTotalCompra, "Preparando Pedido", itensPedido);

        // Serializando o pedido para garantir que todos os dados estão sendo enviados corretamente
        Gson gson = new Gson();
        String pedidoJson = gson.toJson(pedido);
        System.out.println("Pedido serializado antes de enviar: " + pedidoJson);

        // Enviando o pedido para o backend
        Call<Pedido> call = apiService.enviarPedido(pedido);
        call.enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful()) {
                    Pedido pedidoResposta = response.body();
                    if (pedidoResposta != null) {
                        int pedidoId = pedidoResposta.getId();
                        System.out.println("Pedido enviado com sucesso! Pedido ID: " + pedidoId);

                        // Exibir mensagem de sucesso e abrir a tela de confirmação de pedido
                        Toast.makeText(CheckoutActivity.this, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        abrirConfirmacaoPedidoActivity(pedidoId);
                    }
                } else {
                    System.err.println("Erro ao processar o pedido: " + response.message());
                    Toast.makeText(CheckoutActivity.this, "Erro ao processar o pedido.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                System.err.println("Falha na conexão: " + t.getMessage());
                Toast.makeText(CheckoutActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirConfirmacaoPedidoActivity(int pedidoId) {
        Intent intent = new Intent(CheckoutActivity.this, ConfirmacaoPedidoActivity.class);
        intent.putExtra("pedidoId", pedidoId);
        intent.putParcelableArrayListExtra("produtos", new ArrayList<>(produtosSelecionados));
        intent.putExtra("valorTotal", valorTotalCompra);
        intent.putExtra("clienteId", clienteId);
        startActivity(intent);
        finish();
    }

    private void mostrarCamposCartao(boolean mostrar) {
        edtNumeroCartao.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        edtNomeCartao.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        edtValidadeCartao.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        edtCvvCartao.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    private void mostrarChavePix() {
        if (isFinishing() || isDestroyed()) {
            return; // Se a Activity está sendo finalizada ou destruída, não mostre o diálogo
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pix, null);

        TextView tvPixKey = dialogView.findViewById(R.id.tvPixKey);
        Button btnOk = dialogView.findViewById(R.id.btnOk);

        String chavePix = "pix.recebimentos@grape.com";
        tvPixKey.setText(chavePix);

        tvPixKey.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Chave PIX", chavePix);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Chave PIX copiada para a área de transferência", Toast.LENGTH_SHORT).show();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialogPix = builder.create();

        btnOk.setOnClickListener(v -> {
            if (alertDialogPix != null && alertDialogPix.isShowing()) {
                alertDialogPix.dismiss();
            }
            enviarPedidoParaApi();
        });

        alertDialogPix.show();
    }

    private String getFormaPagamentoSelecionada() {
        int selectedId = radioGroupPagamento.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return null;
        }
        RadioButton radioButton = findViewById(selectedId);
        return radioButton.getText().toString();
    }
}
