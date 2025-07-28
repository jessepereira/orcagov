# Dockerfile.dev (você pode ter esse separado do Dockerfile "de produção")

FROM eclipse-temurin:17-jdk

# Instalação do Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Diretório de trabalho
WORKDIR /app

# Copia o Maven Wrapper
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Baixa as dependências
RUN ./mvnw dependency:go-offline

# Copia o código-fonte (opcional, pois vamos usar volume no compose)
COPY src ./src

# Porta da aplicação
EXPOSE 8080

# Executa em modo dev
CMD ["./mvnw", "spring-boot:run"]
