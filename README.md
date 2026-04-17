# 📦 Sistema de Gerenciamento de Estoque e Vendas

## 📌 Objetivo
Este projeto tem como objetivo fornecer um sistema completo para **gestão de estoque e vendas**, incluindo funcionalidades essenciais como:
- **Cadastro de produtos e fornecedores**
- **Atualização automática do estoque após vendas**
- **Geração de notas fiscais**
- **Integração com meios de pagamento**
- **Alertas de estoque baixo**
- **Relatórios e dashboards para análise de vendas**
- **Mensageria para eventos críticos do sistema via Kafka**

----

## 🚀 Stacks Utilizadas

- **Java 17**
- **Spring Boot 3+** (Spring Web, Spring Data JPA, Spring Cache, Spring Mail)
- **Quarkus** (para alguns módulos específicos)
- **PostgreSQL** (Banco de dados relacional)
- **Redis** (Cache e fila de eventos)
- **Apache Kafka** (Mensageria)
- **Testcontainers** (Testes de integração)
- **Lombok** (Redução de boilerplate code)
- **MapStruct** (Conversão entre entidades e DTOs)
- **Docker** (Para conteinerização da aplicação)

---

## 🏛 Arquitetura

Este projeto segue a **Arquitetura Hexagonal (Ports & Adapters)**, garantindo desacoplamento e facilitando a manutenção.

### 📂 Estrutura de Pacotes

```
📦 com.kavex.xtoke.controle_estoque
 ┣ 📂 domain
 ┃ ┣ 📂 model
 ┃ ┃ ┣ 📜 Produto.java
 ┃ ┃ ┣ 📜 Venda.java
 ┃ ┃ ┣ 📜 ItemVenda.java
 ┃ ┃ ┣ 📜 Fornecedor.java
 ┃ ┃ ┣ 📜 Cliente.java
 ┃ ┃ ┣ 📜 Usuario.java
 ┃ ┃ ┣ 📜 Role.java 
 ┃ ┃ ┣ 📜 Pedido.java
 ┃ ┃ ┣ 📜 NotaFiscal.java 
 ┃ ┃ ┗ 📜 StatusVenda.java
 ┃ ┗ 📂 exception
 ┃   ┣ 📜 BaseException.java
 ┃   ┣ 📜 BadRequestException.java
 ┃   ┣ 📜 NotFoundException.java
 ┃   ┣ 📜 ForbiddenException.java 
 ┃   ┗ 📜 ErroMensagem.java
 ┣ 📂 application
 ┃ ┣ 📂 mapper
 ┃ ┃ ┣ 📜 ClienteMapper.java
 ┃ ┃ ┣ 📜 FornecedorMapper.java
 ┃ ┃ ┣ 📜 ItemVendaMapper.java
 ┃ ┃ ┣ 📜 ProdutoMapper.java
 ┃ ┃ ┗ 📜 VendaMapper.java 
 ┃ ┣ 📂 service
 ┃ ┃ ┣ 📜 ClienteService.java
 ┃ ┃ ┣ 📜 FornecedorService.java
 ┃ ┃ ┣ 📜 NotaFiscalService.java
 ┃ ┃ ┣ 📜 PedidoService.java
 ┃ ┃ ┣ 📜 ProdutoService.java
 ┃ ┃ ┣ 📜 UserDetailsServiceImpl.java  
 ┃ ┃ ┗ 📜 VendaService.java
 ┃ ┗ 📂 port
 ┃   ┣ 📂 in
 ┃   ┃ ┣ 📜 ProdutoUseCase.java
 ┃   ┃ ┣ 📜 PedidoUseCase.java
 ┃   ┃ ┣ 📜 NotaFiscalUseCase.java
 ┃   ┃ ┣ 📜 VendaUseCase.java 
 ┃   ┃ ┗ 📜 FornecedorUseCase.java
 ┃   ┗ 📂 out
 ┃     ┣ 📜 ProdutoRepositoryPort.java
 ┃     ┣ 📜 VendaRepositoryPort.java
 ┃     ┣ 📜 FornecedorRepositoryPort.java
 ┃     ┣ 📜 NotaFiscalRepositoryPort.java
 ┃     ┣ 📜 PedidoRepositoryPort.java
 ┃     ┣ 📜 UsuarioRepositoryPort.java 
 ┃     ┗ 📜 NotificacaoServicePort.java
 ┣ 📂 infrastructure
 ┃ ┣ 📂 adapter
 ┃ ┃ ┣ 📂 persistence
 ┃ ┃ ┃ ┣ 📜 FornecedorRepositoryAdapter.java
 ┃ ┃ ┃ ┣ 📜 NotaFiscalRepositoryAdapter.java
 ┃ ┃ ┃ ┣ 📜 NotificacaoRepositoryAdapter.java
 ┃ ┃ ┃ ┣ 📜 PedidoRepositoryAdapter.java
 ┃ ┃ ┃ ┣ 📜 ProdutoRepositoryAdapter.java
 ┃ ┃ ┃ ┣ 📜 VendaRepositoryAdapter.java  
 ┃ ┃ ┃ ┗ 📜 UsuarioRepositoryAdapter.java
 ┃ ┃ ┣ 📂 messaging
 ┃ ┃ ┃ ┣ 📜 EventEstoqueBaixo.java
 ┃ ┃ ┃ ┣ 📜 EventNotaFiscalGerada.java
 ┃ ┃ ┃ ┣ 📜 EventPedidoCriado.java
 ┃ ┃ ┃ ┣ 📜 EventVendaRealizada.java
 ┃ ┃ ┃ ┣ 📜 KafkaEventPublisherAdapter.java
 ┃ ┃ ┃ ┣ 📜 ListenerEstoqueEvent.java
 ┃ ┃ ┃ ┣ 📜 ListenerNotaFiscal.java
 ┃ ┃ ┃ ┣ 📜 ListenerPedidoEvent.java   
 ┃ ┃ ┃ ┗ 📜 ListenerVendaEvent.java
 ┃ ┃ ┗ 📂 notification
 ┃ ┃   ┣ 📜 EmailNotificationAdapter.java
 ┃ ┃   ┗ 📜 WhatsAppNotificationAdapter.java
 ┃ ┣ 📂 config
 ┃ ┃ ┣ 📜 BeanConfiguration.java
 ┃ ┃ ┣ 📜 RedisCacheConfig.java
 ┃ ┃ ┗ 📜 RedisTokenConfig.java
 ┃ ┣ 📂 queue
 ┃ ┃ ┗ 📜 RedisEventQueueService.java
 ┃ ┗ 📂 security
 ┃   ┣ 📜 CustomAuthenticationProvider.java
 ┃   ┣ 📜 JwtAuthenticationFilter.java
 ┃   ┣ 📜 RedisTokenService.java
 ┃   ┣ 📜 SecurityConfig.java 
 ┃   ┗ 📜 JwtService.java  
 ┣ 📂 web
 ┃ ┣ 📂 dto
 ┃ ┃ ┣ 📜 ClienteDTO.java
 ┃ ┃ ┣ 📜 FornecedorDTO.java
 ┃ ┃ ┣ 📜 ItemVendaDTO.java
 ┃ ┃ ┣ 📜 ProdutoDTO.java 
 ┃ ┃ ┗ 📜 VendaDTO.java  
 ┃ ┣ 📜 ProdutoController.java
 ┃ ┣ 📜 VendaController.java
 ┃ ┗ 📜 FornecedorController.java
 ┗ 📜 Application.java
```

---

## 🔥 Principais Funcionalidades

### ✅ **Gestão de Estoque**
- Cadastro e edição de produtos e fornecedores
- Atualização automática do estoque após vendas
- Alertas automáticos quando o estoque está abaixo do mínimo
- Disparo de eventos via **Kafka** para reposição automática

### ✅ **Controle de Vendas**
- Registro de novas vendas
- Integração com gateway de pagamento
- Geração automática de notas fiscais
- Envio de confirmação de compra por e-mail

### ✅ **Mensageria com Kafka**
- Eventos assíncronos para evitar gargalos na aplicação
- **Tópicos Kafka:**
  - `ESTOQUE-BAIXO-TOPIC` → Notifica estoque baixo
  - `PEDIDO-CRIADO-TOPIC` → Processamento de pedidos
  - `VENDA-REALIZADA-TOPIC` → Confirmação de vendas
  - `NOTA-FISCAL-GERADA-TOPIC` → Geração de nota fiscal

### ✅ **Relatórios e Dashboards**
- Análise de vendas diárias
- Previsão de demanda com base em histórico
- Relatório de custos e faturamento

---

## 📢 Como Rodar o Projeto

### 📌 **Pré-requisitos**
- Java 17+
- Docker e Docker Compose
- PostgreSQL
- Kafka e Redis (pode ser iniciado via Docker Compose)

### 🚀 **Passos para Execução**

1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-usuario/estoque-vendas.git
   cd estoque-vendas
   ```

2. Configure as variáveis de ambiente no `application.yml` ou `application.properties`

3. Suba os containers necessários:
   ```sh
   docker-compose up -d
   ```

4. Compile e execute a aplicação:
   ```sh
   ./mvnw spring-boot:run
   ```

5. Acesse a API:
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## 🧪 Testes
Para rodar os testes de integração e unitários:
```sh
./mvnw test
```

Os testes de integração usam **Testcontainers**, garantindo que sejam executados em um ambiente isolado.

---

## 🛠 Melhorias Futuras
- Implementação de um sistema de recomendação de produtos
- Melhoria na previsão de demanda com Machine Learning
- Integração com ERPs externos

---

## 🤝 Contribuição
Pull requests são bem-vindos! Para grandes mudanças, abra uma issue primeiro para discutir o que você gostaria de modificar.


