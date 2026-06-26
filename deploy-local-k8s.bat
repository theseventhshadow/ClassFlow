@echo off
title ClassFlow - Deploy Local K8s
cd /d "%~dp0"

echo ============================================
echo  ClassFlow - Despliegue en K8s Local
echo ============================================
echo.
echo  Ejecutando script de despliegue...
echo  (Si pide permisos, acepta para continuar)
echo.

powershell.exe -ExecutionPolicy Bypass -File "%~dp0deploy-local-k8s.ps1"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  [ERROR] El despliegue fallo. Revisa los mensajes arriba.
    pause
) else (
    echo.
    echo  [OK] Script finalizado.
    timeout /t 5 >nul
)
