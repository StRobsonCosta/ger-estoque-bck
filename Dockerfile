# Usar a imagem do OpenJDK 17 como base
FROM eclipse-temurin:17-jdk-alpine

# Definir o diretório de trabalho dentro do container
WORKDIR /app

# Copiar o arquivo JAR correto da aplicação para o container
COPY target/*.jar app.jar

# Expor a porta que a aplicação utilizará
EXPOSE 8081

# Comando para rodar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

