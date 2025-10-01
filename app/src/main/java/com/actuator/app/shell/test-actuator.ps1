# test-actuator.ps1
$base = 'http://localhost:8080'
$metric = 'http.server.requests'
$endpointTemplate = '/api/stock/critical'  # parte común de la ruta
$requests = 200

Write-Host "Leyendo métricas iniciales..."
$m1 = Invoke-RestMethod "$base/actuator/metrics/$metric"
$count1 = ($m1.measurements | Where-Object { $_.statistic -eq 'COUNT' }).value
$total1 = ($m1.measurements | Where-Object { $_.statistic -eq 'TOTAL_TIME' }).value
$max1   = ($m1.measurements | Where-Object { $_.statistic -eq 'MAX' }).value

"INICIAL -> COUNT = $count1 ; TOTAL_TIME(s) = $total1 ; MAX(s) = $max1"

Write-Host "Generando carga: $requests peticiones a $endpointTemplate/CRIT{i} ..."
for ($i=0; $i -lt $requests; $i++) {
    $url = "$base$endpointTemplate/CRIT$i"
    try {
        Invoke-RestMethod $url -TimeoutSec 10 | Out-Null
    } catch {
        # ignorar 500
    }
}

Start-Sleep -Seconds 2  # espera breve para que métricas se actualicen

Write-Host "Leyendo métricas finales..."
$m2 = Invoke-RestMethod "$base/actuator/metrics/$metric"
$count2 = ($m2.measurements | Where-Object { $_.statistic -eq 'COUNT' }).value
$total2 = ($m2.measurements | Where-Object { $_.statistic -eq 'TOTAL_TIME' }).value
$max2   = ($m2.measurements | Where-Object { $_.statistic -eq 'MAX' }).value

"FINAL   -> COUNT = $count2 ; TOTAL_TIME(s) = $total2 ; MAX(s) = $max2"

# Cálculos del intervalo
$deltaCount = $count2 - $count1
$deltaTotal = $total2 - $total1

if ($deltaCount -gt 0) {
    $avg_s = $deltaTotal / $deltaCount
    $avg_ms = [math]::Round($avg_s * 1000, 2)
    "DELTA   -> Requests = $deltaCount ; TotalTime(s) = $([math]::Round($deltaTotal,4)) ; Avg latency = $avg_ms ms"
} else {
    "DELTA   -> No se registraron nuevas requests (deltaCount = $deltaCount)"
}
