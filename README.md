# OrcaGov
**Sistema de Gestão de Despesas Públicas**

## 📚 Índice
- [Sobre](#sobre)
- [Tecnologias](#tecnologias)
- [Instalação](#instalação)
- [Uso](#uso)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## 🧾 Sobre

**OrcaGov** é um sistema de gestão de **despesas**, **empenhos** e **pagamentos**, desenvolvido para otimizar a execução das dívidas de uma instituição pública. Ele permite o cadastro, visualização, edição e exclusão das entidades **Despesa**, **Empenho** e **Pagamento**, respeitando as regras de negócio detalhadas. O sistema visa proporcionar maior transparência e controle sobre os processos financeiros da instituição.

---

## ⚙️ Tecnologias

Este projeto foi desenvolvido utilizando as seguintes tecnologias:

- **Spring Boot**: Framework utilizado para desenvolver a API RESTful e gerenciar as dependências de forma eficiente.
- **JPA (Java Persistence API)**: Usado para mapear as entidades para o banco de dados e facilitar a manipulação dos dados de forma orientada a objetos.
- **Spring Security**: Framework para proteger a API com autenticação e autorização (utilizando JWT).
- **PostgreSQL**: Banco de dados relacional utilizado para armazenar os dados da aplicação.
- **Java 17**: Versão do Java utilizada para o desenvolvimento.
- **Lombok**: Biblioteca para reduzir a quantidade de código repetitivo, como **getters**, **setters** e **construtores**.
- **Maven**: Gerenciador de dependências e build para o projeto.
- **JUnit / Mockito**: Frameworks de teste utilizados para garantir a qualidade e estabilidade do código.
- **Spring Boot DevTools**: Ferramenta para facilitar o desenvolvimento local, com funcionalidades como **hot reload**.

---

### Pré-requisitos:
- **docker**
- **docker-compose**

### Passos para Instalar e Executar

### Subir o container do banco de dados

1- Primeiro, clone o repositório para sua máquina local:
git clone https://github.com/jessepereira/orcagov.git

2- Entre na pasta orcago verifique se o docker-compose.yml do postgres estar lá e execute 
docker-compose up, banco de dados será configurado automaticamente com a variável de ambiente `POSTGRES_DB=orcagov` na criação do container.

3 cd api,verifique se o docker-compose.yml estar lá e execute docker-compose up 

### Explicação do Processo

- Dois containers Docker foram escolhidos para garantir que o banco de dados suba primeiro.  
  Isso reduz os riscos de a aplicação travar com algum erro relacionado ao banco de dados.
- O container do banco deve ser iniciado primeiro.
- O container do back-end Java foi configurado para aguardar a conclusão do banco,  
  mas ao separá-los em dois `docker-compose`, minimiza-se o risco de falhas no processo.
- Este ambiente é totalmente voltado para **desenvolvimento**.  
  **Não é recomendado para produção**, pois ele não inclui configurações como balanceamento de carga, segurança ou escalabilidade.

---

### Acessar a aplicação

Quando o container estiver rodando, você pode acessar a aplicação no seguinte endereço:

```text
http://localhost:8081
```

Use as credenciais abaixo para login:

- **Usuário:** `admin`  
- **Senha:** `admin123`

---

### Observações

- Esse ambiente é somente para desenvolvimento. **Não é recomendado para uso em produção**.
- A escolha de separar os containers foi feita para garantir que o banco de dados inicie corretamente antes do back-end.
- Caso o banco ou back-end não subam corretamente, verifique os logs do Docker e as configurações dos containers.
