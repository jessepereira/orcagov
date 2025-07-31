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

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/jessepereira/orcagov.git
   cd orcagov
