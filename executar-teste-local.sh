#!/usr/bin/env bash
set -e

# Use este script para executar testes locais

RESULTS_WORKSPACE="$(pwd)/load-test/user-files/results"
GATLING_BIN_DIR=$GATLING_HOME/bin
GATLING_WORKSPACE="$(pwd)/load-test/user-files"

if [ -z ${GATLING_HOME} ]; then
    echo '[error]   A variavel $GATLING_HOME deve apontar para o diretório do Gatling.';
    exit 1;
fi

if [ -f ${GATLING_BIN_DIR} ]; then
    echo '[error]   A variavel $GATLING_BIN_DIR deve apontar para o binário do Gatling.';
    exit 2;
fi

runGatling() {
    sh $GATLING_BIN_DIR/gatling.sh -rm local -s RinhaBackendCrebitosSimulation \
        -rd "Rinha de Backend - 2024/Q1: Crébito" \
        -rf $RESULTS_WORKSPACE \
        -sf "$GATLING_WORKSPACE/simulations"
}

startTest() {
    for i in {1..20}; do
        # 2 requests to wake the 2 api instances up :)
        curl --fail http://localhost:9999/clientes/1/extrato && \
        echo "" && \
        curl --fail http://localhost:9999/clientes/1/extrato && \
        echo "" && \
        runGatling && \
        break || sleep 2;
    done
}

startTest
