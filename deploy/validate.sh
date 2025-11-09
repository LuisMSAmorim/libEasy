#!/bin/bash

# Script de validação do ambiente LibEasy no Kubernetes
# Verifica se todos os componentes estão rodando corretamente

set -e

NAMESPACE="libeasy"

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

# Contador de erros
ERRORS=0
WARNINGS=0

print_header "Validação do Ambiente LibEasy"

# 1. Verificar namespace
print_header "1. Verificando Namespace"
if kubectl get namespace $NAMESPACE &> /dev/null; then
    print_success "Namespace '$NAMESPACE' existe"
else
    print_error "Namespace '$NAMESPACE' não encontrado"
    ERRORS=$((ERRORS + 1))
fi

# 2. Verificar PostgreSQL
print_header "2. Verificando PostgreSQL"
if kubectl get deployment postgres -n $NAMESPACE &> /dev/null; then
    print_success "Deployment do PostgreSQL existe"
    
    # Verificar se está rodando
    READY=$(kubectl get deployment postgres -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
    DESIRED=$(kubectl get deployment postgres -n $NAMESPACE -o jsonpath='{.status.replicas}')
    
    if [ "$READY" == "$DESIRED" ]; then
        print_success "PostgreSQL está rodando ($READY/$DESIRED réplicas)"
    else
        print_error "PostgreSQL não está pronto ($READY/$DESIRED réplicas)"
        ERRORS=$((ERRORS + 1))
    fi
    
    # Verificar service
    if kubectl get service postgres -n $NAMESPACE &> /dev/null; then
        print_success "Service do PostgreSQL existe"
    else
        print_error "Service do PostgreSQL não encontrado"
        ERRORS=$((ERRORS + 1))
    fi
else
    print_error "Deployment do PostgreSQL não encontrado"
    ERRORS=$((ERRORS + 1))
fi

# 3. Verificar Elasticsearch
print_header "3. Verificando Elasticsearch"
if kubectl get deployment elasticsearch -n $NAMESPACE &> /dev/null; then
    print_success "Deployment do Elasticsearch existe"
    
    # Verificar se está rodando
    READY=$(kubectl get deployment elasticsearch -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
    DESIRED=$(kubectl get deployment elasticsearch -n $NAMESPACE -o jsonpath='{.status.replicas}')
    
    if [ "$READY" == "$DESIRED" ]; then
        print_success "Elasticsearch está rodando ($READY/$DESIRED réplicas)"
    else
        print_error "Elasticsearch não está pronto ($READY/$DESIRED réplicas)"
        ERRORS=$((ERRORS + 1))
    fi
    
    # Verificar service
    if kubectl get service elasticsearch -n $NAMESPACE &> /dev/null; then
        print_success "Service do Elasticsearch existe"
    else
        print_error "Service do Elasticsearch não encontrado"
        ERRORS=$((ERRORS + 1))
    fi
else
    print_error "Deployment do Elasticsearch não encontrado"
    ERRORS=$((ERRORS + 1))
fi

# 4. Verificar LibEasy
print_header "4. Verificando Aplicação LibEasy"
if kubectl get deployment libeasy -n $NAMESPACE &> /dev/null; then
    print_success "Deployment da aplicação existe"
    
    # Verificar se está rodando
    READY=$(kubectl get deployment libeasy -n $NAMESPACE -o jsonpath='{.status.readyReplicas}')
    DESIRED=$(kubectl get deployment libeasy -n $NAMESPACE -o jsonpath='{.status.replicas}')
    
    if [ "$READY" == "$DESIRED" ]; then
        print_success "Aplicação está rodando ($READY/$DESIRED réplicas)"
    else
        print_warning "Aplicação não está totalmente pronta ($READY/$DESIRED réplicas)"
        WARNINGS=$((WARNINGS + 1))
    fi
    
    # Verificar service
    if kubectl get service libeasy -n $NAMESPACE &> /dev/null; then
        print_success "Service da aplicação existe"
    else
        print_error "Service da aplicação não encontrado"
        ERRORS=$((ERRORS + 1))
    fi
else
    print_error "Deployment da aplicação não encontrado"
    ERRORS=$((ERRORS + 1))
fi

# 5. Verificar APISix
print_header "5. Verificando APISix"
if helm list -n $NAMESPACE | grep -q apisix; then
    print_success "APISix está instalado via Helm"
    
    # Verificar gateway
    if kubectl get deployment -n $NAMESPACE | grep -q apisix-gateway; then
        print_success "APISix Gateway deployment existe"
        
        READY=$(kubectl get deployment apisix-gateway -n $NAMESPACE -o jsonpath='{.status.readyReplicas}' 2>/dev/null || echo "0")
        DESIRED=$(kubectl get deployment apisix-gateway -n $NAMESPACE -o jsonpath='{.status.replicas}' 2>/dev/null || echo "0")
        
        if [ "$READY" == "$DESIRED" ] && [ "$READY" != "0" ]; then
            print_success "APISix Gateway está rodando ($READY/$DESIRED réplicas)"
        else
            print_error "APISix Gateway não está pronto ($READY/$DESIRED réplicas)"
            ERRORS=$((ERRORS + 1))
        fi
    else
        print_warning "APISix Gateway deployment não encontrado"
        WARNINGS=$((WARNINGS + 1))
    fi
    
    # Verificar rotas
    if kubectl get apisixroute -n $NAMESPACE &> /dev/null; then
        ROUTES=$(kubectl get apisixroute -n $NAMESPACE --no-headers 2>/dev/null | wc -l)
        print_success "Rotas APISix configuradas: $ROUTES"
    else
        print_warning "Nenhuma rota APISix encontrada"
        WARNINGS=$((WARNINGS + 1))
    fi
else
    print_error "APISix não está instalado"
    ERRORS=$((ERRORS + 1))
fi

# 6. Verificar PVCs
print_header "6. Verificando Volumes Persistentes"
if kubectl get pvc -n $NAMESPACE &> /dev/null; then
    PVC_COUNT=$(kubectl get pvc -n $NAMESPACE --no-headers | wc -l)
    BOUND_COUNT=$(kubectl get pvc -n $NAMESPACE --no-headers | grep -c Bound)
    
    print_info "PVCs encontrados: $PVC_COUNT"
    
    if [ "$PVC_COUNT" == "$BOUND_COUNT" ]; then
        print_success "Todos os PVCs estão bound ($BOUND_COUNT/$PVC_COUNT)"
    else
        print_error "Alguns PVCs não estão bound ($BOUND_COUNT/$PVC_COUNT)"
        ERRORS=$((ERRORS + 1))
        
        # Listar PVCs com problema
        kubectl get pvc -n $NAMESPACE | grep -v Bound || true
    fi
else
    print_warning "Nenhum PVC encontrado"
    WARNINGS=$((WARNINGS + 1))
fi

# 7. Verificar ConfigMaps
print_header "7. Verificando ConfigMaps"
CM_COUNT=$(kubectl get configmap -n $NAMESPACE --no-headers 2>/dev/null | wc -l)
if [ "$CM_COUNT" -gt 0 ]; then
    print_success "ConfigMaps encontrados: $CM_COUNT"
else
    print_warning "Nenhum ConfigMap encontrado"
    WARNINGS=$((WARNINGS + 1))
fi

# 8. Testar conectividade
print_header "8. Testando Conectividade"

# Verificar se APISix está acessível
if kubectl get service -n $NAMESPACE | grep -q apisix-gateway; then
    NODEPORT=$(kubectl get service apisix-gateway -n $NAMESPACE -o jsonpath='{.spec.ports[?(@.name=="http")].nodePort}' 2>/dev/null || echo "")
    
    if [ -n "$NODEPORT" ]; then
        print_info "APISix NodePort: $NODEPORT"
        
        # Testar conectividade
        if command -v curl &> /dev/null; then
            print_info "Testando health check via APISix..."
            if curl -s -H 'Host: libeasy.local' http://localhost:$NODEPORT/actuator/health &> /dev/null; then
                print_success "Health check respondeu com sucesso"
            else
                print_warning "Health check não respondeu (aplicação pode não estar pronta ainda)"
                WARNINGS=$((WARNINGS + 1))
            fi
        else
            print_info "curl não disponível, pulando teste de conectividade"
        fi
    fi
fi

# 9. Verificar recursos dos pods
print_header "9. Verificando Recursos dos Pods"
if command -v kubectl &> /dev/null; then
    if kubectl top pods -n $NAMESPACE &> /dev/null; then
        echo ""
        kubectl top pods -n $NAMESPACE
        print_success "Métricas de recursos disponíveis"
    else
        print_warning "Metrics server não está instalado (kubectl top não disponível)"
        WARNINGS=$((WARNINGS + 1))
    fi
fi

# 10. Verificar eventos recentes
print_header "10. Eventos Recentes (últimos 5)"
kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp' | tail -n 6

# Resumo Final
print_header "Resumo da Validação"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    print_success "Todos os componentes estão funcionando corretamente! 🎉"
    echo ""
    print_info "Você pode acessar a aplicação:"
    echo "  curl -H 'Host: libeasy.local' http://localhost:30080/actuator/health"
    echo ""
    print_info "Ou adicione ao /etc/hosts:"
    echo "  echo '127.0.0.1 libeasy.local' | sudo tee -a /etc/hosts"
    exit 0
elif [ $ERRORS -eq 0 ]; then
    print_warning "Sistema operacional com $WARNINGS avisos"
    echo ""
    print_info "Os avisos não são críticos, mas devem ser verificados"
    exit 0
else
    print_error "Encontrados $ERRORS erros e $WARNINGS avisos"
    echo ""
    print_info "Execute os seguintes comandos para investigar:"
    echo "  kubectl get pods -n $NAMESPACE"
    echo "  kubectl describe pod <pod-name> -n $NAMESPACE"
    echo "  kubectl logs <pod-name> -n $NAMESPACE"
    exit 1
fi

