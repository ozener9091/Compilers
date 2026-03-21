$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
}

$javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
$jpackageExe = Join-Path $env:JAVA_HOME "bin\jpackage.exe"

if (-not (Test-Path $javaExe)) {
    throw "JAVA_HOME points to an invalid JDK: $env:JAVA_HOME"
}

if (-not (Test-Path $jpackageExe)) {
    throw "jpackage.exe was not found in $env:JAVA_HOME\bin"
}

$env:PATH = (Join-Path $env:JAVA_HOME "bin") + ";" + $env:PATH

$artifactName = "Compilers_Laba1-1.0-SNAPSHOT.jar"
$inputDir = Join-Path $projectRoot "target\jpackage-input"
$distDir = Join-Path $projectRoot "dist"
$appImageDir = Join-Path $distDir "app-image"
$installerDir = Join-Path $distDir "installer"
$portableRoot = Join-Path $appImageDir "CompilersLab1"
$portableZip = Join-Path $distDir "CompilersLab1-portable.zip"

Remove-Item $inputDir -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item $distDir -Recurse -Force -ErrorAction SilentlyContinue

New-Item -ItemType Directory -Path $inputDir | Out-Null
New-Item -ItemType Directory -Path $appImageDir | Out-Null
New-Item -ItemType Directory -Path $installerDir | Out-Null

Write-Host "Building Maven project..."
& .\mvnw.cmd -q -DskipTests clean package dependency:copy-dependencies "-DincludeScope=runtime" "-DoutputDirectory=target\jpackage-input"

Copy-Item (Join-Path $projectRoot "target\$artifactName") $inputDir -Force

$commonArgs = @(
    "--name", "CompilersLab1",
    "--app-version", "1.0.0",
    "--vendor", "Sitnikov V.I.",
    "--description", "JavaFX application for compiler laboratory work",
    "--input", $inputDir,
    "--main-jar", $artifactName,
    "--main-class", "com.example.compilers_laba1.Launcher",
    "--dest"
)

Write-Host "Creating portable app-image..."
& $jpackageExe `
    "--type" "app-image" `
    @commonArgs $appImageDir `
    "--app-content" (Join-Path $projectRoot "flexbison")

if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed to create the portable app-image."
}

Write-Host "Creating portable ZIP archive..."
Compress-Archive -Path $portableRoot -DestinationPath $portableZip -Force

Write-Host "Attempting to create installer EXE..."
try {
    & $jpackageExe `
        "--type" "exe" `
        @commonArgs $installerDir `
        "--app-content" (Join-Path $projectRoot "flexbison") `
        "--win-shortcut" `
        "--win-menu" `
        "--win-dir-chooser"

    if ($LASTEXITCODE -ne 0) {
        throw "jpackage could not create the installer EXE. Install WiX Toolset and rerun the script."
    }
}
catch {
    Write-Warning "EXE installer build failed. The portable app-image was still created successfully."
    Write-Warning $_.Exception.Message
}

Write-Host ""
Write-Host "Done."
Write-Host "Portable launcher: $appImageDir\CompilersLab1\CompilersLab1.exe"
Write-Host "Portable ZIP: $portableZip"
Write-Host "Installer directory: $installerDir"
