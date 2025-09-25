<#
==========================================================
 msgpingpong Runner Java 21
 ----------------------------------------------------------
 This script compiles and runs the messaging game in two modes:
   1. single - Single-process mode (in-memory Mediator)
   2. server - Start PlayerServer (multi-process via socket)
   3. client - Start PlayerClient (multi-process via socket)

 Usage:
   ./run.ps1 single    -> Run with InMemoryCommunication
   ./run.ps1 server    -> Start socket server (PlayerServer)
   ./run.ps1 client    -> Start socket client (PlayerClient)
==========================================================
#>

param (
    [string]$Mode
)

if (-not $Mode) {
    Write-Host "Usage: ./run.ps1 [single|server|client]"
    exit 1
}

# Ensure Java is installed
if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    Write-Host "javac not found. Please install Java JDK 21 or later first."
    exit 1
}

# Source and output directories
$SRC_DIR = "src"
$OUT_DIR = "target/classes"

# Clean previous build
Write-Host "Cleaning previous build..."
if (Test-Path $OUT_DIR) {
    Remove-Item -Recurse -Force $OUT_DIR
}
New-Item -ItemType Directory -Force -Path $OUT_DIR | Out-Null

# Compile Java sources
Write-Host "Compiling Java sources..."
$javaFiles = Get-ChildItem -Path $SRC_DIR -Recurse -Include *.java | ForEach-Object { $_.FullName }
& javac -d $OUT_DIR $javaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed. Please check your Java code."
    exit 1
}

Write-Host "Build successful."

# Run based on mode
switch ($Mode.ToLower()) {
    "single" {
        Write-Host "Starting game in SINGLE-PROCESS mode..."
        & java -cp $OUT_DIR org.msgpingpong.core.GameController
    }
    "server" {
        Write-Host "Starting game in MULTI-PROCESS SERVER mode..."
        & java -cp $OUT_DIR org.msgpingpong.core.PlayerServer
    }
    "client" {
        Write-Host "Starting game in MULTI-PROCESS CLIENT mode..."
        & java -cp $OUT_DIR org.msgpingpong.core.PlayerClient
    }
    default {
        Write-Host "Invalid mode. Use single, server, or client."
        exit 1
    }
}
