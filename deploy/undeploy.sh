#!/bin/bash

# Script para remover a aplicação LibEasy do Kubernetes
# Autor: Sistema LibEasy

set -e

echo "=================================================="
echo "  Removendo LibEasy do Kubernetes"
echo "=================================================="
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Confirmar remoção
read -p "Tem certeza que deseja remover toda a aplicação LibEasy? (s/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[SsYy]$ ]]; then
    print_info "Operação cancelada."
    exit 0
fi

# Remover Spring Cloud Gateway
print_info "Removendo Spring Cloud Gateway..."
kubectl delete -f gateway/service.yaml --ignore-not-found=true
kubectl delete -f gateway/deployment.yaml --ignore-not-found=true
kubectl delete -f gateway/configmap.yaml --ignore-not-found=true
kubectl delete -f gateway/rbac.yaml --ignore-not-found=true
echo ""

# Remover aplicação LibEasy
print_info "Removendo aplicação LibEasy..."
kubectl delete -f libeasy/service.yaml --ignore-not-found=true
kubectl delete -f libeasy/deployment.yaml --ignore-not-found=true
kubectl delete -f libeasy/configmap.yaml --ignore-not-found=true
echo ""

# Remover Auth Service
print_info "Removendo Auth Service..."
kubectl delete -f auth-service/service.yaml --ignore-not-found=true
kubectl delete -f auth-service/deployment.yaml --ignore-not-found=true
kubectl delete -f auth-service/secret.yaml --ignore-not-found=true
echo ""

# Remover MySQL (Auth Service Database)
print_info "Removendo MySQL (Auth Service)..."
kubectl delete -f auth-mysql/service.yaml --ignore-not-found=true
kubectl delete -f auth-mysql/statefulset.yaml --ignore-not-found=true
kubectl delete -f auth-mysql/secret.yaml --ignore-not-found=true
echo ""

# Remover Redis
print_info "Removendo Redis..."
kubectl delete -f redis/service.yaml --ignore-not-found=true
kubectl delete -f redis/deployment.yaml --ignore-not-found=true
echo ""

# Remover Elasticsearch
print_info "Removendo Elasticsearch..."
kubectl delete -f elasticsearch/service.yaml --ignore-not-found=true
kubectl delete -f elasticsearch/deployment.yaml --ignore-not-found=true
kubectl delete -f elasticsearch/configmap.yaml --ignore-not-found=true
kubectl delete -f elasticsearch/pvc.yaml --ignore-not-found=true
echo ""

# Remover PostgreSQL
print_info "Removendo PostgreSQL..."
kubectl delete -f postgres/service.yaml --ignore-not-found=true
kubectl delete -f postgres/deployment.yaml --ignore-not-found=true
kubectl delete -f postgres/configmap.yaml --ignore-not-found=true
kubectl delete -f postgres/pvc.yaml --ignore-not-found=true
echo ""

# Perguntar se deseja remover o namespace
read -p "Deseja remover o namespace 'libeasy'? Isso removerá TODOS os dados. (s/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[SsYy]$ ]]; then
    print_warn "Removendo namespace e TODOS os dados..."
    kubectl delete namespace libeasy
    print_info "Namespace removido com sucesso!"
else
    print_info "Namespace 'libeasy' mantido."
fi

echo ""
echo "=================================================="
print_info "Remoção concluída!"
echo "=================================================="

