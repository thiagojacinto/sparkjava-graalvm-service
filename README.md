# sparkjava-graalvm-service
API desenvolvida para testes do evento Rinha de Backend, 2024 Q1

## Feita com
- Java 21
- SparkJava ([nmondal](https://github.com/nmondal/spark-11)'s fork)
- GraalVM Community Edition for [JDK 21.0.1](https://www.graalvm.org/release-notes/JDK_21/)
- GraalVM's [Native Image](https://www.graalvm.org/latest/reference-manual/native-image/basics/)

## Uso

A partir do uso do Docker / Podman para orquestrar os containers que executam o projeto, com o comando:
```bash
podman compose up -f docker-compose.yml --detach
```

### Registrando transações:
```bash
curl -q --no-progress-meter -X POST localhost:4567/clientes/1/transacoes -H "Content-Type: application/json" -d "{\"valor\":900,\"tipo\":\"c\",\"descricao\":\"descricao\"}"
```

cujo o retorno esperado é uma resposta em _[JSON](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON)_ com o seguinte formato:
```json
{
   "limite" : 100000,
   "saldo" : 900
}
```
### Gerando um extrato
```bash
curl -q --no-progress-meter localhost:4567/clientes/1/extrato
```
com retorno esperado no seguinte formato, mostrando até 10 últimas transações realizadas com aquele cliente:
```json
{
   "saldo" : {
      "data_extrato" : "2024-03-17T04:02:40.888931",
      "limite" : 1000000,
      "total" : -537500
   },
   "ultimas_transacoes" : [
      {
         "descricao" : "descricao",
         "realizada_em" : "2024-03-16T21:48:56.696681",
         "tipo_transacao" : "D",
         "valor" : 9000
      },
      {
         "descricao" : "descricao",
         "realizada_em" : "2024-03-16T21:48:52.727091",
         "tipo_transacao" : "C",
         "valor" : 9000
      }
   ]
}
```

### Estressando a aplicação

Script `bash` para enviar um número bom de requisições para a API desenvolvida.

```bash
# criando transações - CREDITO:
# while true; do \      # para loops infinitos
for i in {1..200}; do \
USUARIO_ID=$(($RANDOM%5+1)); \
VALOR_TRX=$(($RANDOM%10000+350)); \
curl -q --no-progress-meter -X POST localhost:9999/clientes/${USUARIO_ID}/transacoes -H "Content-Type: application/json" -d "{\"valor\":${VALOR_TRX},\"tipo\":\"c\",\"descricao\":\"descricao\"}" ;\
done; 

# criando transações - DEBITO:
# while true; do \      # para loops infinitos
for i in {1..200}; do \
USUARIO_ID=$(($RANDOM%5+1)); \
VALOR_TRX=$(($RANDOM%15000+8000)); \
curl -q --no-progress-meter -X POST localhost:9999/clientes/${USUARIO_ID}/transacoes -H "Content-Type: application/json" -d "{\"valor\":${VALOR_TRX},\"tipo\":\"d\",\"descricao\":\"descricao\"}" ;\
done; 

# gerando extratos:
# while true; do \      # para loops infinitos
for i in {1..200}; do \
USUARIO_ID=$(($RANDOM%5+1)); \
curl -q --no-progress-meter localhost:9999/clientes/${USUARIO_ID}/extrato \
done; 
```