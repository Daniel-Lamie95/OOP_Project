param(
    [string]$JavafxLib = $env:JAVAFX_LIB,
    [string]$MainClass = 'Main'
)

# Validate provided JavaFX lib path (should be the SDK 'lib' folder)
if (-not $JavafxLib -or -not (Test-Path $JavafxLib -PathType Container)) {
    Write-Error "JavaFX lib path not found. Set the JAVAFX_LIB environment variable to the SDK's 'lib' folder or pass -JavafxLib 'C:\path\to\javafx-sdk-XX\lib'"
    exit 1
}

Write-Host "Using JavaFX lib: $JavafxLib"

# Collect java sources
$srcFiles = Get-ChildItem -Path .\src -Recurse -Filter *.java -File | ForEach-Object { $_.FullName }
if (-not $srcFiles -or $srcFiles.Count -eq 0) {
    Write-Error "No Java source files found under .\src"
    exit 1
}

# Ensure javac/java are available
if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    Write-Error "javac not found in PATH. Ensure a JDK is installed and 'javac' is available on PATH."
    exit 1
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "java not found in PATH. Ensure a JDK is installed and 'java' is available on PATH."
    exit 1
}

# Prepare output dir
if (Test-Path .\out) { Remove-Item -Recurse -Force .\out }
New-Item -ItemType Directory -Path .\out | Out-Null

# Build javac argument array (PowerShell will handle quoting for paths with spaces)
$moduleArgs = @('--module-path', $JavafxLib, '--add-modules', 'javafx.controls,javafx.fxml', '-d', (Resolve-Path .\out).Path)
$javacArgs = $moduleArgs + $srcFiles

Write-Host "Compiling with javac..."
Write-Host "javac $($javacArgs -join ' ')"

# Invoke javac
& javac @javacArgs
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed (javac exit code $LASTEXITCODE). Ensure JDK is installed and JAVAFX_LIB points to the JavaFX SDK lib folder."
    exit $LASTEXITCODE
}

# Run the app
$javaArgs = @('--module-path', $JavafxLib, '--add-modules', 'javafx.controls,javafx.fxml', '-cp', (Resolve-Path .\out).Path, $MainClass)
Write-Host "Running: java $($javaArgs -join ' ')"
& java @javaArgs
$rc = $LASTEXITCODE
if ($rc -ne 0) {
    Write-Error "Application exited with code $rc"
}
exit $rc
