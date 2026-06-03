$services = @('classflow-back-gateway-1','classflow-back-auth-1','classflow-back-academic-1','classflow-back-assistance-1','classflow-back-message-1','classflow-back-notification-1')
$all = $false
for ($i = 0; $i -lt 60; $i++) {
  docker ps --format '{{.Names}} | {{.Status}} | {{.Ports}}'
  $all = $true
  foreach ($s in $services) {
    $status = docker ps --filter "name=$s" --format '{{.Status}}'
    if ($status -notmatch 'healthy') { $all = $false; break }
  }
  if ($all) { Write-Host 'ALL_HEALTHY'; exit 0 }
  Start-Sleep -Seconds 2
}
Write-Host 'TIMEOUT'
exit 2
