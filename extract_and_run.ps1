$cwd = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
Set-Location $cwd
$zip = Join-Path $cwd 'openjfx-sdk.zip'
if (-not (Test-Path $zip)) { Write-Error 'Zip not found'; exit 1 }
$dest = Join-Path $cwd 'javafx-sdk-20.0.2'
if (Test-Path $dest) { Remove-Item $dest -Recurse -Force }

Write-Host "Extracting $zip to $dest..."
Expand-Archive -LiteralPath $zip -DestinationPath $dest -Force

Write-Host 'Looking for lib folder...'
$lib = Get-ChildItem -Path $dest -Recurse -Directory | Where-Object { $_.Name -eq 'lib' } | Select-Object -First 1
if (-not $lib) { Write-Error 'lib not found'; exit 2 }

Write-Host "Found lib: $($lib.FullName)"

Write-Host 'Running run-javafx.ps1 with detected JavaFX lib...'
& powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $cwd 'run-javafx.ps1') -JavafxLib $lib.FullName
exit $LASTEXITCODE

