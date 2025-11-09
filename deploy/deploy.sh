#!/bin/bash

# Script de deploy da aplicação LibEasy no Kubernetes
# Autor: Sistema LibEasy
# Descrição: Deploy completo da aplicação com KrakenD como API Gateway

set -e

echo "=================================================="
echo "  Deploy LibEasy no Kubernetes"
echo "=================================================="
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para imprimir mensagens
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se kubectl está instalado
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl não está instalado. Por favor, instale o kubectl."
    exit 1
fi

# Verificar conexão com o cluster
print_info "Verificando conexão com o cluster Kubernetes..."
if ! kubectl cluster-info &> /dev/null; then
    print_error "Não foi possível conectar ao cluster Kubernetes."
    exit 1
fi

print_info "Conexão com o cluster OK!"
echo ""

# Passo 1: Criar namespace
print_info "Criando namespace 'libeasy'..."
kubectl apply -f postgres/namespace.yaml
echo ""

# Passo 2: Deploy do PostgreSQL
print_info "Deployando PostgreSQL..."
kubectl apply -f postgres/pvc.yaml
kubectl apply -f postgres/configmap.yaml
kubectl apply -f postgres/deployment.yaml
kubectl apply -f postgres/service.yaml
print_info "PostgreSQL deployado com sucesso!"
echo ""

# Passo 3: Deploy do Elasticsearch
print_info "Deployando Elasticsearch..."
kubectl apply -f elasticsearch/pvc.yaml
kubectl apply -f elasticsearch/configmap.yaml
kubectl apply -f elasticsearch/deployment.yaml
kubectl apply -f elasticsearch/service.yaml
print_info "Elasticsearch deployado com sucesso!"
echo ""

# Aguardar PostgreSQL e Elasticsearch ficarem prontos
print_info "Aguardando PostgreSQL ficar pronto..."
kubectl wait --for=condition=ready pod -l app=postgres -n libeasy --timeout=300s

print_info "Aguardando Elasticsearch ficar pronto..."
kubectl wait --for=condition=ready pod -l app=elasticsearch -n libeasy --timeout=300s
echo ""

# Passo 4: Deploy do Redis
print_info "Deployando Redis..."
kubectl apply -f redis/deployment.yaml
kubectl apply -f redis/service.yaml
print_info "Redis deployado com sucesso!"
echo ""

# Aguardar Redis ficar pronto
print_info "Aguardando Redis ficar pronto..."
kubectl wait --for=condition=ready pod -l app=redis -n libeasy --timeout=300s
echo ""

# Passo 5: Deploy da aplicação LibEasy
print_info "Deployando aplicação LibEasy..."
kubectl apply -f libeasy/configmap.yaml
kubectl apply -f libeasy/deployment.yaml
kubectl apply -f libeasy/service.yaml
print_info "Aplicação LibEasy deployada com sucesso!"
echo ""

# Aguardar aplicação ficar pronta
print_info "Aguardando aplicação LibEasy ficar pronta..."
kubectl wait --for=condition=ready pod -l app=libeasy -n libeasy --timeout=300s
echo ""

# Passo 6: Deploy do Spring Cloud Gateway
print_info "Deployando Spring Cloud Gateway..."
kubectl apply -f gateway/rbac.yaml
kubectl apply -f gateway/configmap.yaml
kubectl apply -f gateway/deployment.yaml
kubectl apply -f gateway/service.yaml
print_info "Gateway deployado com sucesso!"
echo ""

# Aguardar Gateway ficar pronto
print_info "Aguardando Gateway ficar pronto..."
kubectl wait --for=condition=ready pod -l app=gateway -n libeasy --timeout=300s
echo ""

# Resumo do deploy
echo "=================================================="
echo "  Deploy Concluído com Sucesso!"
echo "=================================================="
echo ""
print_info "Recursos deployados:"
echo "  ✓ Namespace: libeasy"
echo "  ✓ PostgreSQL"
echo "  ✓ Elasticsearch"
echo "  ✓ Redis (rate limiting)"
echo "  ✓ Aplicação LibEasy (2 réplicas)"
echo "  ✓ Spring Cloud Gateway (2 réplicas com rate limiting distribuído)"
echo ""

print_info "Para acessar a aplicação:"
echo "  1. A aplicação está disponível em:"
echo "     http://localhost:30080"
echo ""
echo "  2. Testar a API:"
echo "     curl http://localhost:30080/actuator/health"
echo ""
echo "  3. Health check do Gateway:"
echo "     curl http://localhost:30080/actuator/health"
echo ""

print_info "Endpoints disponíveis:"
echo "  - POST /api/auth/login - Login de usuário"
echo "  - POST /api/auth/register - Registro de usuário"
echo "  - GET /api/books - Listar livros"
echo "  - POST /api/books - Criar livro"
echo "  - GET /api/books/{id} - Buscar livro por ID"
echo "  - GET /api/books/search - Buscar livros"
echo "  - GET /api/loans - Listar empréstimos"
echo "  - POST /api/loans - Criar empréstimo"
echo ""

print_info "Comandos úteis:"
echo "  # Ver todos os recursos"
echo "  kubectl get all -n libeasy"
echo ""
echo "  # Ver logs da aplicação"
echo "  kubectl logs -f deployment/libeasy -n libeasy"
echo ""
echo "  # Ver logs do Gateway"
echo "  kubectl logs -f deployment/gateway -n libeasy"
echo ""
echo "  # Ver rotas do Gateway"
echo "  kubectl logs deployment/gateway -n libeasy | grep 'Loaded RouteDefinition'"
echo ""
echo "=================================================="

