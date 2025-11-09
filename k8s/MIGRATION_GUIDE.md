# LibEasy Auth Microservice Migration Guide

## Overview
This guide covers the migration from monolithic authentication to a separate auth microservice.

## Architecture Changes

### Before
```
Gateway → LibEasy (auth + book + loan)
```

### After
```
Gateway → Auth Service (authentication)
       → LibEasy (book + loan)
```

## Deployment Steps

### 1. Generate JWT Keys (if not already done)
```bash
./generate-rsa-keys.sh
```

### 2. Create Kubernetes Secret for JWT Keys
```bash
kubectl create secret generic jwt-keys \
  --from-file=private_key.pem=./keys/private_key_pkcs8.pem \
  --from-file=public_key.pem=./keys/public_key.pem \
  --namespace=libeasy
```

### 3. Deploy MySQL for Auth Service
```bash
kubectl apply -f k8s/auth-service/mysql-secret.yaml
kubectl apply -f k8s/auth-service/mysql-statefulset.yaml
kubectl apply -f k8s/auth-service/mysql-service.yaml
```

### 4. Deploy Auth Service
```bash
kubectl apply -f k8s/auth-service/auth-deployment.yaml
kubectl apply -f k8s/auth-service/auth-service.yaml
```

### 5. Update Gateway
```bash
kubectl apply -f k8s/gateway/gateway-configmap.yaml
# Restart gateway to pick up new configuration
kubectl rollout restart deployment/gateway -n libeasy
```

### 6. Update LibEasy Application
```bash
# Redeploy with new code that doesn't include auth module
kubectl rollout restart deployment/libeasy -n libeasy
```

## Data Migration

### Option 1: Manual Migration
```bash
# Run the migration script
./migrate-users-to-mysql.sh
```

### Option 2: Seed New Database
The auth-service will automatically create sample users when running with profile `dev`:
- Admin: admin@libeasy.local / admin123
- Users: {name}@email.com / senha123

## Testing

### 1. Test Auth Service
```bash
# Register new user
curl -X POST http://gateway:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"test123"}'

# Login
curl -X POST http://gateway:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'
```

### 2. Test Book Service (with JWT)
```bash
# Get books (requires authentication)
curl -X GET http://gateway:8080/api/books \
  -H "Authorization: Bearer <access_token>"
```

## Rollback Plan

If issues occur:

1. **Revert Gateway**: Point auth routes back to libeasy monolith
2. **Revert LibEasy**: Deploy previous version with auth module
3. **Remove Auth Service**: Delete auth-service deployment

## Development Environment

### Docker Compose
```bash
docker-compose up -d
```

Services:
- Gateway: http://localhost:8080
- Auth Service: http://localhost:8081 (internal)
- LibEasy App: http://localhost:8080 (through gateway)
- Auth MySQL: localhost:3307
- Main PostgreSQL: localhost:5432

## Monitoring

Check health:
```bash
# Auth Service
kubectl get pods -l app=auth-service -n libeasy
kubectl logs -l app=auth-service -n libeasy

# MySQL
kubectl get statefulsets auth-mysql -n libeasy
kubectl logs auth-mysql-0 -n libeasy
```

## Security Notes

1. **JWT Keys**: Store keys securely as Kubernetes Secrets
2. **Database Credentials**: Never commit passwords to git
3. **HTTPS**: Use ingress with TLS in production
4. **Network Policies**: Restrict inter-service communication

## Troubleshooting

### Auth Service Won't Start
- Check MySQL is ready: `kubectl get pods -l app=auth-mysql`
- Check JWT keys secret exists: `kubectl get secret jwt-keys -n libeasy`
- View logs: `kubectl logs -l app=auth-service -n libeasy`

### Authentication Fails
- Verify Gateway has correct public key mounted
- Check JWT claims include: userId, email, role
- Verify headers are being propagated: X-User-Id, X-User-Email, X-User-Role

### Database Connection Issues
- Check MySQL service: `kubectl get svc auth-mysql -n libeasy`
- Verify credentials in secret: `kubectl get secret auth-mysql-secret -n libeasy -o yaml`
