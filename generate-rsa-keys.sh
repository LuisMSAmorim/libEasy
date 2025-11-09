#!/bin/bash

# Script to generate RSA key pair for JWT RS256

echo "Generating RSA key pair for JWT RS256..."

# Generate private key (2048-bit)
openssl genrsa -out keys/private_key.pem 2048

# Generate public key from private key
openssl rsa -in keys/private_key.pem -pubout -out keys/public_key.pem

# Convert private key to PKCS8 format (required by Java)
openssl pkcs8 -topk8 -inform PEM -outform PEM -in keys/private_key.pem -out keys/private_key_pkcs8.pem -nocrypt

echo "✓ Private key generated: keys/private_key_pkcs8.pem"
echo "✓ Public key generated: keys/public_key.pem"
echo ""
echo "Keys generated successfully!"
echo ""
echo "To use in Kubernetes secrets, encode with base64:"
echo "  cat keys/private_key_pkcs8.pem | base64 -w 0"
echo "  cat keys/public_key.pem | base64 -w 0"
