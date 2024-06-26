#!/usr/bin/env bash
set -e

# Script para rodar testes simples para coletar dados para target/config

### TRANSACOES CREDITO
for i in {1..150}; do USUARIO_ID=$(($RANDOM%6+1)); VALOR_TRX=$(($RANDOM%30000+5000)); curl -q -s --no-progress-meter -X POST localhost:4567/clientes/${USUARIO_ID}/transacoes -H "Content-Type: application/json" -d "{\"valor\":${VALOR_TRX},\"tipo\":\"c\",\"descricao\":\"descricao\"}"; done &2>/dev/null &

### TRANSACOES DEBITO
for i in {1..150}; do USUARIO_ID=$(($RANDOM%6+1)); VALOR_TRX=$(($RANDOM%15000+80000)); curl -q -s --no-progress-meter -X POST localhost:4567/clientes/${USUARIO_ID}/transacoes -H "Content-Type: application/json" -d "{\"valor\":${VALOR_TRX},\"tipo\":\"d\",\"descricao\":\"descricao\"}"; done &2>/dev/null &

### EXTRATO
for i in {1..150}; do USUARIO_ID=$(($RANDOM%6+1)); curl -q -s --no-progress-meter localhost:4567/clientes/${USUARIO_ID}/extrato; done;