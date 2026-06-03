# Script para probar todos los microservicios
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
$workspaceRoot = "C:\Users\pv-alumno\Downloads\ClassFlow-Back"

$services = @(
    @{name = "auth-service"; port = 8081},
    @{name = "academic-service"; port = 8082},
    @{name = "assistance-service"; port = 8083},
    @{name = "message-service"; port = 8084},
    @{name = "notification-service"; port = 8085},
    @{name = "gateway-service"; port = 8080}
)

Write-Host "=== INICIANDO PRUEBA DE MICROSERVICIOS ===" -ForegroundColor Cyan
Write-Host ""

foreach ($service in $services) {
    $serviceName = $service.name
    $port = $service.port
    $servicePath = Join-Path $workspaceRoot $serviceName
    
    if (Test-Path $servicePath) {
        Write-Host "Probando: $serviceName (puerto $port)" -ForegroundColor Yellow
        Write-Host "Compilando y verificando..." -ForegroundColor Gray
        
        Push-Location $servicePath
        $output = cmd /c mvnw.cmd clean compile 2>&1 | Select-Object -Last 3
        $buildResult = $?
        Pop-Location
        
        if ($buildResult) {
            Write-Host "✓ $serviceName COMPILA OK" -ForegroundColor Green
        } else {
            Write-Host "✗ $serviceName TIENE ERRORES DE COMPILACIÓN" -ForegroundColor Red
            Write-Host "  $($output[0])" -ForegroundColor Red
        }
    } else {
        Write-Host "✗ $serviceName NO ENCONTRADO" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "=== PARA PROBAR EN TIEMPO REAL ===" -ForegroundColor Cyan
Write-Host "Abre una terminal para cada servicio y ejecuta:" -ForegroundColor Yellow
Write-Host "`$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.2'" -ForegroundColor Green
Write-Host "cd [ruta-del-servicio]" -ForegroundColor Green
Write-Host "cmd /c mvnw.cmd spring-boot:run" -ForegroundColor Green
Write-Host ""
Write-Host "Verifica en los logs: 'Started [ServiceName]Application'" -ForegroundColor Cyan
