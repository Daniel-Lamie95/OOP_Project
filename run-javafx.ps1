<#
run-javafx.ps1
Usage:
  .\run-javafx.ps1               # uses javac/java from PATH
  .\run-javafx.ps1 "C:\Program Files\Java\jdk-17.0.8\bin"   # provide JDK bin folder if javac not in PATH

What it does:
 - Resolves the local JavaFX SDK in ./javafx-sdk-20.0.2/javafx-sdk-20.0.2/lib
 - Compiles all .java files in src/ to out/ using javac
 - Runs the Main class with the javafx modules on the module-path
#>
param(
    [string]$jdkBin
)

function Resolve-JavaCmds {
    param([string]$jdkBinParam)
    if ($jdkBinParam -and (Test-Path $jdkBinParam)) {
        $javac = Join-Path $jdkBinParam 'javac.exe'
        $java = Join-Path $jdkBinParam 'java.exe'
        if (Test-Path $javac -and Test-Path $java) { return @{javac=$javac; java=$java} }
    }
    $javacCmd = Get-Command javac -ErrorAction SilentlyContinue
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if ($javacCmd -and $javaCmd) { return @{javac=$javacCmd.Path; java=$javaCmd.Path} }
    return $null
}

$jdk = Resolve-JavaCmds -jdkBinParam $jdkBin
if (-not $jdk) {
    Write-Host "ERROR: javac/java not found. Install JDK 17+ or provide path to JDK bin as parameter." -ForegroundColor Red
    Write-Host "Example: .\\run-javafx.ps1 'C:\\Program Files\\Java\\jdk-17.0.8\\bin'"
    exit 1
}
$mpPath = Resolve-Path '.\javafx-sdk-20.0.2\javafx-sdk-20.0.2\lib' -ErrorAction SilentlyContinue
if (-not $mpPath) {
    Write-Host "ERROR: JavaFX SDK not found at .\javafx-sdk-20.0.2\javafx-sdk-20.0.2\lib" -ForegroundColor Red
    Write-Host "Make sure the javafx-sdk folder from the repo is present.";
    exit 1
}
$mp = $mpPath.Path
Write-Host "Using javac: $($jdk.javac)" -ForegroundColor Green
Write-Host "Using java:  $($jdk.java)" -ForegroundColor Green
Write-Host "JavaFX lib:  $mp" -ForegroundColor Green

# create out dir
if (-not (Test-Path .\out)) { New-Item -ItemType Directory -Path .\out | Out-Null }

# compile
& "$($jdk.javac)" --module-path "$mp" --add-modules javafx.controls,javafx.fxml -d out src\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed." -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "Compilation succeeded." -ForegroundColor Green

# run
& "$($jdk.java)" --module-path "$mp" --add-modules javafx.controls,javafx.fxml -cp out Main

if ($LASTEXITCODE -ne 0) { Write-Host "Application exited with code $LASTEXITCODE" -ForegroundColor Yellow }

