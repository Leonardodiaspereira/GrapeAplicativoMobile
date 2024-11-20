package com.example.grapeapp;

import android.os.Bundle;
import android.text.TextUtils;
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

public class CadastroActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtTelefone, edtCPF, edtSenha, edtConfirmarSenha;
    private Button btnCadastrar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicializar os campos
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmailCadastro);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtCPF = findViewById(R.id.edtCpf);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Inicializar o serviço API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Defini a ação do botão "Cadastrar"
        btnCadastrar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString();
            String email = edtEmail.getText().toString();
            String telefone = edtTelefone.getText().toString();
            String cpf = edtCPF.getText().toString();
            String senha = edtSenha.getText().toString();
            String confirmarSenha = edtConfirmarSenha.getText().toString();

            // Verificação simples para ver se os campos estão preenchidos
            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(telefone) || TextUtils.isEmpty(cpf) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(confirmarSenha)) {
                Toast.makeText(CadastroActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificação se as senhas coincidem
            if (!senha.equals(confirmarSenha)) {
                Toast.makeText(CadastroActivity.this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            } else {
                // Criar objeto Cliente
                Cliente cliente = new Cliente(nome, email, telefone, senha, cpf);

                // Chamada para cadastrar cliente
                apiService.cadastrarCliente(cliente).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish(); // Fecha a tela de cadastro e retorna para a anterior
                        } else {
                            Toast.makeText(CadastroActivity.this, "Erro ao cadastrar cliente.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CadastroActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
