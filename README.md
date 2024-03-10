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

A API responde a chamadas HTTP:
- registrando transações:
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
