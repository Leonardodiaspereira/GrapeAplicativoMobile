const express = require('express');
const sql = require('mssql');

const app = express();
const port = 3000;

// Middleware para interpretar JSON no corpo da requisição
app.use(express.json());

console.log('Iniciando configuração do servidor...');

// Configurações de conexão para o SQL Server
const dbConfig = {
  user: 'grape_user',
  password: 'SenhaForte123',
  server: 'localhost', 
  database: 'grape_fazenda',
  options: {
    encrypt: false,
    trustServerCertificate: true,
  },
};

// Conectar ao banco de dados ao iniciar o servidor
async function connectToDb() {
  try {
    await sql.connect(dbConfig);
    console.log('Conexão ao banco de dados foi bem-sucedida!');
  } catch (error) {
    console.error('Erro ao conectar ao banco de dados:', error);
  }
}
connectToDb();

// Função para remover caracteres especiais
function removerCaracteresEspeciais(valor) {
  return valor.replace(/[^a-zA-Z0-9]/g, '');
}

// Rota para cadastrar um cliente
app.post('/add-client', async (req, res) => {
  const { nome, email, telefone, senha, cpf } = req.body;

  if (!nome || !email || !telefone || !senha || !cpf) {
    console.log('Erro ao cadastrar cliente: Dados incompletos fornecidos');
    return res.status(400).json({ success: false, message: 'Por favor, forneça todos os dados do cliente.' });
  }

  try {
    console.log('Iniciando cadastro do cliente...');
    const request = new sql.Request();
    request.input('nome', sql.NVarChar(100), nome);
    request.input('email', sql.NVarChar(100), email);
    request.input('telefone', sql.VarChar(15), telefone);
    request.input('senha', sql.NVarChar(100), senha);
    request.input('cpf', sql.VarChar(14), removerCaracteresEspeciais(cpf));

    const result = await request.query(`
      INSERT INTO dbo.clientes (nome, email, telefone, senha, cpf)
      OUTPUT Inserted.id
      VALUES (@nome, @email, @telefone, @senha, @cpf)
    `);

    const clienteId = result.recordset[0].id;
    console.log(`Cliente cadastrado com sucesso. ID: ${clienteId}`);

    res.status(201).json({ success: true, message: 'Cliente cadastrado com sucesso!', id: clienteId });
  } catch (error) {
    console.error('Erro ao cadastrar cliente:', error);
    res.status(500).json({ success: false, message: 'Erro ao cadastrar cliente', error: error.message });
  }
});

// Rota para login do cliente
app.post('/login', async (req, res) => {
  const { email, senha } = req.body;

  if (!email || !senha) {
    console.log('Erro ao fazer login: email ou senha não fornecidos');
    return res.status(400).json({ success: false, message: 'Por favor, forneça o email e a senha.' });
  }

  try {
    console.log('Iniciando login do cliente...');
    const request = new sql.Request();
    request.input('email', sql.NVarChar(100), email);
    request.input('senha', sql.NVarChar(100), senha);

    const result = await request.query(`
      SELECT id, nome, email, telefone, cpf, senha FROM dbo.clientes WHERE email = @email AND senha = @senha
    `);

    if (result.recordset.length > 0) {
      const cliente = result.recordset[0];
      console.log(`Login bem-sucedido para o cliente com ID: ${cliente.id}`);
      res.status(200).json({
        success: true,
        message: 'Login bem-sucedido',
        id: cliente.id,
        cliente: cliente
      });
    } else {
      console.log('Erro ao fazer login: credenciais inválidas');
      res.status(401).json({ success: false, message: 'Credenciais inválidas' });
    }
  } catch (error) {
    console.error('Erro ao fazer login:', error);
    res.status(500).json({ success: false, message: 'Erro interno no servidor', error: error.message });
  }
});

// Rota para obter dados do cliente por ID
app.get('/client/:id', async (req, res) => {
  const { id } = req.params;

  try {
    console.log(`Obtendo dados do cliente com ID: ${id}`);
    const request = new sql.Request();
    request.input('id', sql.Int, id);

    const result = await request.query(`
      SELECT id, nome, email, telefone, cpf, senha FROM dbo.clientes WHERE id = @id
    `);

    if (result.recordset.length > 0) {
      console.log(`Dados do cliente com ID ${id} obtidos com sucesso`);
      res.status(200).json(result.recordset[0]);
    } else {
      console.log(`Cliente com ID ${id} não encontrado`);
      res.status(404).json({ success: false, message: 'Cliente não encontrado' });
    }
  } catch (error) {
    console.error('Erro ao obter dados do cliente:', error);
    res.status(500).json({ success: false, message: 'Erro ao obter dados do cliente', error: error.message });
  }
});

// Rota para atualizar os dados do cliente
app.put('/update-client/:id', async (req, res) => {
  const { id } = req.params;
  const { nome, email, telefone, senha } = req.body;

  if (!nome || !email || !telefone || !senha) {
    console.log(`Erro ao atualizar cliente com ID ${id}: dados incompletos fornecidos`);
    return res.status(400).json({ success: false, message: 'Por favor, forneça todos os dados para atualização.' });
  }

  try {
    console.log(`Atualizando cliente com ID: ${id}`);
    const request = new sql.Request();
    request.input('id', sql.Int, id);
    request.input('nome', sql.NVarChar(100), nome);
    request.input('email', sql.NVarChar(100), email);
    request.input('telefone', sql.VarChar(15), telefone);
    request.input('senha', sql.NVarChar(100), senha);

    await request.query(`
      UPDATE dbo.clientes
      SET nome = @nome, email = @email, telefone = @telefone, senha = @senha
      WHERE id = @id
    `);

    console.log(`Dados do cliente com ID ${id} atualizados com sucesso`);
    res.status(200).json({ success: true, message: 'Dados do cliente atualizados com sucesso!' });
  } catch (error) {
    console.error('Erro ao atualizar cliente:', error);
    res.status(500).json({ success: false, message: 'Erro ao atualizar cliente', error: error.message });
  }
});

// Rota para registrar um pedido
app.post('/submit-order', async (req, res) => {
  console.log('Dados da requisição recebidos:', JSON.stringify(req.body, null, 2));

  const {
    clienteId,
    valorTotal,
    formaPagamento,
    status,
    dataPedido,
    nomeCompleto,
    rua,
    numero,
    cidade,
    estado,
    cep,
    itensPedido,
  } = req.body;

  let camposFaltando = [];

  if (!clienteId) camposFaltando.push('clienteId');
  if (!valorTotal) camposFaltando.push('valorTotal');
  if (!formaPagamento) camposFaltando.push('formaPagamento');
  if (!status) camposFaltando.push('status');
  if (!dataPedido) camposFaltando.push('dataPedido');
  if (!nomeCompleto) camposFaltando.push('nomeCompleto');
  if (!rua) camposFaltando.push('rua');
  if (!numero) camposFaltando.push('numero');
  if (!cidade) camposFaltando.push('cidade');
  if (!estado) camposFaltando.push('estado');
  if (!cep) camposFaltando.push('cep');
  if (!itensPedido || itensPedido.length === 0) camposFaltando.push('itensPedido');

  if (camposFaltando.length > 0) {
    console.log('Erro ao registrar pedido: os seguintes dados estão faltando:', camposFaltando.join(', '));
    return res.status(400).json({
      success: false,
      message: 'Por favor, forneça todos os dados do pedido.',
      camposFaltando: camposFaltando,
    });
  }

  try {
    console.log('Iniciando o registro do pedido...');
    console.log('Dados recebidos para o pedido:', req.body);

    // Validação de produtos e estoque
    for (const item of itensPedido) {
      console.log(`Validando estoque do produto com ID ${item.produtoId}`);
      const requestProduto = new sql.Request();
      requestProduto.input('produtoId', sql.Int, item.produtoId);

      const produtoResult = await requestProduto.query(`
        SELECT * FROM dbo.produtos WHERE id = @produtoId
      `);

      if (produtoResult.recordset.length === 0) {
        console.error(`Produto com ID ${item.produtoId} não encontrado.`);
        return res.status(400).json({ success: false, message: `Produto com ID ${item.produtoId} não encontrado.` });
      }

      const produto = produtoResult.recordset[0];
      if (produto.quantidade_em_estoque < item.quantidade) {
        console.error(`Estoque insuficiente para o produto: ${produto.nome}. Quantidade disponível: ${produto.quantidade_em_estoque}`);
        return res.status(400).json({ success: false, message: `Estoque insuficiente para o produto: ${produto.nome}. Quantidade disponível: ${produto.quantidade_em_estoque}` });
      }
    }

    console.log('Inserindo pedido no banco de dados...');
    const request = new sql.Request();
    request.input('clienteId', sql.Int, clienteId);
    request.input('valorTotal', sql.Decimal(10, 2), valorTotal);
    request.input('formaPagamento', sql.NVarChar(50), formaPagamento);
    request.input('status', sql.NVarChar(20), status);
    request.input('dataPedido', sql.Date, dataPedido);
    request.input('nomeCompleto', sql.NVarChar(100), nomeCompleto);
    request.input('rua', sql.NVarChar(100), rua);
    request.input('numero', sql.NVarChar(11), removerCaracteresEspeciais(numero));
    request.input('cidade', sql.NVarChar(50), cidade);
    request.input('estado', sql.NVarChar(2), removerCaracteresEspeciais(estado));
    request.input('cep', sql.NVarChar(9), removerCaracteresEspeciais(cep));

    // Inserir o pedido
    const result = await request.query(`
      INSERT INTO dbo.pedidos (cliente_id, valor_total, forma_pagamento, status, data_pedido, nome_completo, rua, numero, cidade, estado, cep)
      OUTPUT Inserted.id
      VALUES (@clienteId, @valorTotal, @formaPagamento, @status, @dataPedido, @nomeCompleto, @rua, @numero, @cidade, @estado, @cep)
    `);

    const pedidoId = result.recordset[0].id;
    console.log(`Pedido registrado com sucesso. ID do pedido: ${pedidoId}`);

    // Inserir os itens do pedido e atualizar o estoque
    for (const item of itensPedido) {
      console.log(`Inserindo item do pedido para o produto ID ${item.produtoId}...`);
      const requestItem = new sql.Request();
      requestItem.input('pedidoId', sql.Int, pedidoId);
      requestItem.input('produtoId', sql.Int, item.produtoId);
      requestItem.input('quantidade', sql.Int, item.quantidade);

      await requestItem.query(`
        INSERT INTO dbo.itens_pedido (pedido_id, produto_id, quantidade)
        VALUES (@pedidoId, @produtoId, @quantidade)
      `);

      console.log(`Atualizando estoque para o produto ID ${item.produtoId}...`);
      const requestUpdateEstoque = new sql.Request();
      requestUpdateEstoque.input('produtoId', sql.Int, item.produtoId);
      requestUpdateEstoque.input('quantidade', sql.Int, item.quantidade);

      await requestUpdateEstoque.query(`
        UPDATE dbo.produtos
        SET quantidade_em_estoque = quantidade_em_estoque - @quantidade
        WHERE id = @produtoId
      `);
    }

    console.log('Pedido completo registrado com sucesso');
    res.status(201).json({ success: true, message: 'Pedido registrado com sucesso!', pedidoId: pedidoId });
  } catch (error) {
    console.error('Erro ao registrar pedido:', error);
    res.status(500).json({ success: false, message: 'Erro ao registrar pedido', error: error.message });
  }
});

// Rota para atualizar o status do pedido
app.put('/update-order-status/:id', async (req, res) => {
  const { id } = req.params;
  const { status } = req.body;

  if (!status) {
    console.log(`Erro ao atualizar status do pedido com ID ${id}: status não fornecido`);
    return res.status(400).json({ success: false, message: 'Por favor, forneça o status para atualização.' });
  }

  try {
    console.log(`Atualizando status do pedido com ID: ${id}`);
    const request = new sql.Request();
    request.input('id', sql.Int, id);
    request.input('status', sql.NVarChar(20), status);

    await request.query(`
      UPDATE dbo.pedidos
      SET status = @status
      WHERE id = @id
    `);

    console.log(`Status do pedido com ID ${id} atualizado para: ${status}`);
    res.status(200).json({ success: true, message: 'Status do pedido atualizado com sucesso!' });
  } catch (error) {
    console.error('Erro ao atualizar status do pedido:', error);
    res.status(500).json({ success: false, message: 'Erro ao atualizar status do pedido', error: error.message });
  }
});


// Rota para obter pedidos de um cliente por ID, incluindo os detalhes dos itens do pedido
app.get('/get-orders/:clienteId', async (req, res) => {
  const { clienteId } = req.params;

  if (!clienteId) {
    console.log('Erro ao obter pedidos: clienteId não fornecido');
    return res.status(400).json({ success: false, message: 'Por favor, forneça o ID do cliente.' });
  }

  try {
    console.log(`Obtendo pedidos do cliente com ID: ${clienteId}`);
    const request = new sql.Request();
    request.input('clienteId', sql.Int, clienteId);

    // Consulta para obter os pedidos do cliente
    const pedidosResult = await request.query(`
      SELECT id, cliente_id, valor_total, forma_pagamento, status, data_pedido, nome_completo, rua, numero, cidade, estado, cep
      FROM dbo.pedidos
      WHERE cliente_id = @clienteId
      ORDER BY data_pedido DESC, id DESC
    `);

    const pedidos = pedidosResult.recordset;

    // Se nenhum pedido for encontrado
    if (pedidos.length === 0) {
      console.log(`Nenhum pedido encontrado para o cliente com ID ${clienteId}`);
      return res.status(404).json({ success: false, message: 'Nenhum pedido encontrado para o cliente.' });
    }

    // Para cada pedido, buscar os itens
    for (let pedido of pedidos) {
      const requestItens = new sql.Request();
      requestItens.input('pedidoId', sql.Int, pedido.id);

      const itensResult = await requestItens.query(`
        SELECT ip.id, ip.pedido_id, ip.produto_id, ip.quantidade, prod.nome AS nomeProduto
        FROM dbo.itens_pedido ip
        INNER JOIN dbo.produtos prod ON ip.produto_id = prod.id
        WHERE ip.pedido_id = @pedidoId
      `);

      pedido.itensPedido = itensResult.recordset; // Adicionar os itens ao pedido
      pedido.valorTotal = pedido.valor_total; // Atribuir o valor total corretamente ao objeto pedido
    }

    console.log(`Pedidos do cliente com ID ${clienteId} obtidos com sucesso`);
    res.status(200).json(pedidos);
  } catch (error) {
    console.error('Erro ao obter pedidos do cliente:', error);
    res.status(500).json({ success: false, message: 'Erro ao obter pedidos do cliente', error: error.message });
  }
});


// Inicia o servidor
app.listen(port, '0.0.0.0', () => {
  console.log(`Servidor rodando em http://0.0.0.0:${port}`);
  console.log(`Aguardando requisições na porta ${port}`);
});
