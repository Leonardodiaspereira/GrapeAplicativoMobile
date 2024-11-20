package com.example.grapeapp.api;

import com.example.grapeapp.model.Cliente;
import com.example.grapeapp.model.Pedido;
import com.example.grapeapp.model.LoginRequest;
import com.example.grapeapp.model.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Endpoint para cadastrar um cliente
    @POST("add-client")
    Call<Void> cadastrarCliente(@Body Cliente cliente);

    // Endpoint para login do cliente
    @POST("login")
    Call<LoginResponse> loginCliente(@Body LoginRequest loginRequest);

    // Endpoint para obter dados do cliente por ID
    @GET("client/{id}")
    Call<Cliente> obterCliente(@Path("id") int id);

    // Endpoint para atualizar os dados do cliente
    @PUT("update-client/{id}")
    Call<Void> atualizarCliente(@Path("id") int id, @Body Cliente cliente);

    // Endpoint para registrar um pedido
    @POST("submit-order")
    Call<Pedido> enviarPedido(@Body Pedido pedido);

    // Endpoint para obter pedidos de um cliente específico
    @GET("get-orders/{clientId}")
    Call<List<Pedido>> obterPedidos(@Path("clientId") int clientId);
}
