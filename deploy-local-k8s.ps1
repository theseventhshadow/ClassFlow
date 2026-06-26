<#
.SYNOPSIS
    Despliega ClassFlow completo en Kubernetes local (Docker Desktop).
.DESCRIPTION
    Construye todas las imágenes Docker, las despliega en el K8s de Docker Desktop
    y abre port-forwardings para acceder desde el navegador.
.NOTES
    Requisitos:
      - Docker Desktop con Kubernetes habilitado
      - Contexto kubectl: docker-desktop
#>

#Requires -Version 5.1

$ErrorActionPreference = "Stop"
$ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path

# ── Colores ──────────────────────────────────────────────────────────────────
function Write-Step($text)  { Write-Host "`n=== $text ===" -ForegroundColor Cyan }
function Write-OK($text)    { Write-Host "  [OK] $text" -ForegroundColor Green }
function Write-Warn($text)  { Write-Host "  [!] $text" -ForegroundColor Yellow }
function Write-Info($text)  { Write-Host "     $text" -ForegroundColor Gray }

# ── Verificaciones iniciales ─────────────────────────────────────────────────
Write-Step "Verificando requisitos"

# Verificar kubectl
if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Write-Host "❌ kubectl no encontrado. Asegúrate de tener Docker Desktop con Kubernetes habilitado." -ForegroundColor Red
    exit 1
}
Write-OK "kubectl disponible"

# Verificar contexto
$ctx = kubectl config current-context 2>$null
if ($ctx -ne "docker-desktop") {
    Write-Warn "Contexto actual: $ctx"
    Write-Warn "¿Docker Desktop K8s está habilitado? Se recomienda el contexto 'docker-desktop'"
    Write-Info "Para cambiarlo: kubectl config use-context docker-desktop"
}
else {
    Write-OK "Contexto Kubernetes: docker-desktop"
}

# Verificar Docker
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Docker no encontrado." -ForegroundColor Red
    exit 1
}
Write-OK "Docker disponible"

# ── Build de imágenes ────────────────────────────────────────────────────────
Write-Step "1. Construyendo imágenes Docker"
Write-Info "Esto puede tomar varios minutos la primera vez..."

Push-Location "$ROOT\backend"
try {
    docker compose build --parallel
    if ($LASTEXITCODE -ne 0) { throw "Error al construir imágenes del backend" }
    Write-OK "Imágenes backend construidas"
}
finally {
    Pop-Location
}

# Verificar que las imágenes existen
$images = @(
    "api-gateway:local",
    "bff:local",
    "ms-auth:local",
    "ms-academic:local",
    "ms-assistance:local",
    "ms-message:local",
    "ms-notification:local",
    "classflow-frontend:local"
)

foreach ($img in $images) {
    $exists = docker images -q $img 2>$null
    if (-not $exists) {
        Write-Warn "Imagen '$img' no encontrada. Puede que hayas cancelado el build."
    }
    else {
        Write-OK "Imagen $img lista"
    }
}

# ── Namespace + Config ───────────────────────────────────────────────────────
Write-Step "2. Creando namespace, configmap y secrets"

kubectl apply -f "$ROOT\backend\k8s\namespace.yml"
kubectl apply -f "$ROOT\backend\k8s\configmap.yml"
kubectl apply -f "$ROOT\backend\k8s\secret.yml"
Write-OK "Recursos base creados"

# ── Bases de datos ───────────────────────────────────────────────────────────
Write-Step "3. Desplegando bases de datos - StatefulSets"

kubectl apply -f "$ROOT\backend\k8s\databases\"
Write-OK "Manifests de BD aplicados"

Write-Info "Esperando a que todas las bases de datos estén listas..."
$dbLabels = @("auth-db", "academic-db", "assistance-db", "message-db", "notification-db")
foreach ($label in $dbLabels) {
    Write-Info "  Esperando $label..."
    kubectl wait --for=condition=ready pod -l app=$label -n classflow --timeout=120s
    Write-OK "$label lista"
}

# ── Microservicios ───────────────────────────────────────────────────────────
Write-Step "4. Desplegando microservicios"

$msDeployments = @(
    "ms-auth.yml",
    "ms-academic.yml",
    "ms-assistance.yml",
    "ms-message.yml",
    "ms-notification.yml"
)

foreach ($file in $msDeployments) {
    Write-Info "Aplicando $file..."
    kubectl apply -f "$ROOT\backend\k8s\$file"
}

Write-Info "Esperando a que los microservicios estén listos..."
$msLabels = @("ms-auth", "ms-academic", "ms-assistance", "ms-message", "ms-notification")
foreach ($label in $msLabels) {
    Write-Info "  Esperando $label..."
    kubectl wait --for=condition=ready pod -l app=$label -n classflow --timeout=180s
    Write-OK "$label listo"
}

# ── Infraestructura (BFF + Gateway) ──────────────────────────────────────────
Write-Step "5. Desplegando infraestructura - BFF + API Gateway"

kubectl apply -f "$ROOT\backend\k8s\bff.yml"
kubectl apply -f "$ROOT\backend\k8s\api-gateway.yml"

Write-Info "Esperando a BFF..."
kubectl wait --for=condition=ready pod -l app=bff -n classflow --timeout=120s
Write-OK "BFF listo"

Write-Info "Esperando a API Gateway..."
kubectl wait --for=condition=ready pod -l app=api-gateway -n classflow --timeout=120s
Write-OK "API Gateway listo"

# ── Frontend ─────────────────────────────────────────────────────────────────
Write-Step "6. Desplegando Frontend"

kubectl apply -f "$ROOT\backend\k8s\frontend.yml"

Write-Info "Esperando al Frontend..."
kubectl wait --for=condition=ready pod -l app=frontend -n classflow --timeout=120s
Write-OK "Frontend listo"

# ── Verificación final ───────────────────────────────────────────────────────
Write-Step "7. Verificando estado del despliegue"

Write-Host "`nPods en namespace classflow:" -ForegroundColor Yellow
kubectl get pods -n classflow

Write-Host "`nServices en namespace classflow:" -ForegroundColor Yellow
kubectl get services -n classflow

# ── Instrucciones de acceso ──────────────────────────────────────────────────
Write-Step "8. Para acceder a ClassFlow"
Write-Host ""
Write-Host "Abre DOS terminales y ejecuta estos comandos:" -ForegroundColor Yellow
Write-Host ""
Write-Host "  Terminal 1 - Frontend:" -ForegroundColor Cyan
Write-Host "    kubectl port-forward service/frontend 3000:80 -n classflow" -ForegroundColor White
Write-Host ""
Write-Host "  Terminal 2 - API Gateway:" -ForegroundColor Cyan
Write-Host "    kubectl port-forward service/api-gateway 8080:8080 -n classflow" -ForegroundColor White
Write-Host ""
Write-Host "  Luego abre http://localhost:3000 en tu navegador" -ForegroundColor Green
Write-Host "  Email: admin@classflow.cl / Password: password" -ForegroundColor Green

# ── Resumen final ────────────────────────────────────────────────────────────
Write-Step "DESPLIEGUE COMPLETADO"
Write-Host "--" -ForegroundColor Cyan
Write-Host "Ya solo falta abrir los puertos (paso 8 arriba)" -ForegroundColor Cyan
Write-Host "--" -ForegroundColor Yellow
Write-Host "Destroy:   kubectl delete namespace classflow" -ForegroundColor Yellow
Write-Host "--" -ForegroundColor Gray

# ── Helper: reinicio rápido de un servicio ──────────────────────────────────
Write-Step "Comandos utiles - copia y pega segun necesites"
Write-Host "  # Reconstruir y reiniciar un solo microservicio:" -ForegroundColor Gray
Write-Host "      cd backend" -ForegroundColor Gray
Write-Host "      docker compose build ms-auth" -ForegroundColor Gray
Write-Host "      kubectl rollout restart deployment/ms-auth -n classflow" -ForegroundColor Gray
Write-Host "      kubectl rollout status deployment/ms-auth -n classflow" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "  # Ver logs en tiempo real:" -ForegroundColor Gray
Write-Host "      kubectl logs -n classflow -l app=ms-auth --tail=50 --follow" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "  # Acceder a la terminal de un pod:" -ForegroundColor Gray
Write-Host "      kubectl exec -n classflow -it deployment/ms-auth -- sh" -ForegroundColor Gray
Write-Host "" -ForegroundColor Gray
Write-Host "  # Ver recursos usados:" -ForegroundColor Gray
Write-Host "      kubectl top pods -n classflow" -ForegroundColor Gray
