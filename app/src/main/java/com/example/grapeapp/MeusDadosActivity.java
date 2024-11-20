package com.example.grapeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grapeapp.api.ApiClient;
import com.example.grapeapp.api.ApiService;
import com.example.grapeapp.model.Cliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtTelefone, edtCpf, edtSenha, edtNovaSenha, edtConfirmarNovaSenha;
    private Button btnSalvarAlteracoes;
    private ApiService apiService;
    private int clienteId; // ID do cliente logado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados);

        // Inicializar os campos
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtCpf = findViewById(R.id.edtCpf);
        edtSenha = findViewById(R.id.edtSenha);
        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarNovaSenha = findViewById(R.id.edtConfirmarNovaSenha);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarAlteracoes);

        // Inicializar o serviço API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Obter o ID do cliente do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        clienteId = sharedPreferences.getInt("clienteId", 0);

        if (clienteId == 0) {
            Toast.makeText(MeusDadosActivity.this, "Erro: ID do cliente não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Carregar os dados do cliente
        carregarDadosCliente();

        // Configuração do botão para salvar alterações
        btnSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAlteracoes();
            }
        });
    }

    private void carregarDadosCliente() {
        apiService.obterCliente(clienteId).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cliente cliente = response.body();
                    // Preencher os campos com os dados do cliente
                    edtNome.setText(cliente.getNome());
                    edtEmail.setText(cliente.getEmail());
                    edtTelefone.setText(cliente.getTelefone());
                    edtCpf.setText(cliente.getCpf());
                    edtSenha.setText(cliente.getSenha());
                } else {
                    Toast.makeText(MeusDadosActivity.this, "Erro ao carregar dados do cliente: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(MeusDadosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarAlteracoes() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String telefone = edtTelefone.getText().toString().trim();
        String cpf = edtCpf.getText().toString().trim();
        String senhaAtual = edtSenha.getText().toString().trim();
        String novaSenha = edtNovaSenha.getText().toString().trim();
        String confirmarNovaSenha = edtConfirmarNovaSenha.getText().toString().trim();

        if (!novaSenha.isEmpty() && !novaSenha.equals(confirmarNovaSenha)) {
            Toast.makeText(MeusDadosActivity.this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Definir a senha a ser atualizada
        String senhaParaAtualizar = novaSenha.isEmpty() ? senhaAtual : novaSenha;

        // Criar objeto Cliente com as alterações
        Cliente clienteAtualizado = new Cliente(clienteId, nome, email, telefone, senhaParaAtualizar, cpf);

        // Chamada para atualizar dados do cliente
        apiService.atualizarCliente(clienteId, clienteAtualizado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MeusDadosActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    // Atualizar senha atual para refletir a alteração, se necessário
                    if (!novaSenha.isEmpty()) {
                        edtSenha.setText(novaSenha);
                    }
                } else {
                    Toast.makeText(MeusDadosActivity.this, "Erro ao atualizar os dados.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MeusDadosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
