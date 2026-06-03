$dirs = @('academic-service', 'assistance-service', 'auth-service', 'gateway-service', 'message-service', 'notification-service')
$results = @()

foreach($dir in $dirs) {
    $path = "c:\Users\puc0\Documents\Proyectos\ClassFlow-Back\$dir"
    Set-Location $path
    
    Write-Host "Testing $dir..." -ForegroundColor Cyan
    
    $output = .\mvnw.cmd clean test -q 2>&1
    $exitCode = $LASTEXITCODE
    
    $results += @{
        Service = $dir
        ExitCode = $exitCode
        Status = if($exitCode -eq 0) { "PASSED" } else { "FAILED" }
    }
    
    Write-Host "Status: $(if($exitCode -eq 0) { 'PASSED' } else { 'FAILED' })" -ForegroundColor $(if($exitCode -eq 0) { 'Green' } else { 'Red' })
    Write-Host ""
}

Write-Host "=== TEST SUMMARY ===" -ForegroundColor Yellow
foreach($result in $results) {
    Write-Host "$($result.Service): $($result.Status)" -ForegroundColor $(if($result.ExitCode -eq 0) { 'Green' } else { 'Red' })
}

$passedCount = ($results | Where-Object { $_.ExitCode -eq 0 }).Count
$totalCount = $results.Count
Write-Host "Total: $passedCount/$totalCount passed"
