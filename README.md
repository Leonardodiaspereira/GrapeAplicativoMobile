# Aplicativo Mobile - Grape Fazenda

Este é um aplicativo mobile desenvolvido em Java utilizando o Android Studio, criado para dispositivos Android, e faz parte do sistema integrado da Grape Fazenda, um projeto para o PIM 4 do curso de Análise e Desenvolvimento de Sistemas.

# Descrição do Projeto

O aplicativo móvel é utilizado para a gestão e aquisição de produtos da Grape Fazenda, oferecendo funcionalidades como:
- Cadastro e login de clientes
- Seleção e compra de produtos (como diversos tipos de uvas premium)
- Visualização e gerenciamento de pedidos realizados
- Atualização de dados dos usuários

# Tecnologias Utilizadas

- Java: linguagem de programação principal utilizada para desenvolvimento do aplicativo.
- Android Studio: ambiente de desenvolvimento integrado (IDE) utilizado para criar a interface e funcionalidades do aplicativo.
- Node.js: servidor utilizado para gerenciar a conexão com o banco de dados.
- SQL Server: banco de dados utilizado para armazenar as informações dos clientes, produtos, pedidos, etc.

# Conexão com o Banco de Dados

A conexão com o banco de dados foi realizada através de um servidor Node.js, que atua como intermediário entre o aplicativo e o banco de dados SQL Server. O Node.js é responsável por processar as requisições do aplicativo, garantindo que as informações sejam transmitidas de forma segura e eficiente.

# Integração com o Aplicativo Desktop

O aplicativo móvel também está integrado ao sistema desktop, desenvolvido em .NET Framework Windows Forms. Essa integração permite que a gestão dos pedidos seja feita por um administrador no aplicativo desktop, atualizando o status do pedido diretamente no banco de dados compartilhado.
Os status do pedido são sincronizados entre as plataformas móvel e desktop, possibilitando que o cliente acompanhe o status da entrega em tempo real através do aplicativo.

# Funcionalidades do Aplicativo

1. Cadastro e Login: O usuário pode se cadastrar no aplicativo e efetuar login para acessar as funcionalidades.
2. Seleção de Produtos: Visualização e seleção de produtos (tipos de uvas) com informações detalhadas sobre cada um deles.
3. Realização de Pedidos: Permite selecionar os produtos desejados, inserir a quantidade e finalizar o pedido.
4. Pagamentos: É possível escolher entre diferentes formas de pagamento.
5. Meus Pedidos: Permite acompanhar o status dos pedidos realizados.
6. Editar Dados do Usuário: O cliente pode atualizar informações pessoais diretamente pelo aplicativo.

# Colaboração do Projeto

Este projeto foi desenvolvido em grupo como parte do curso de Análise e Desenvolvimento de Sistemas, visando proporcionar experiência prática em projetos de desenvolvimento de software integrados. O projeto tem como objetivo aplicar conhecimentos em desenvolvimento de aplicativos móveis, integração de sistemas, e conexão com banco de dados.

