@echo off
REM ==========================================================
REM msgpingpong Runner Java 21
REM ----------------------------------------------------------
REM This script compiles and runs the messaging game in two modes:
REM   1. single - Single-process mode (in-memory Mediator)
REM   2. server - Start PlayerServer (multi-process via socket)
REM   3. client - Start PlayerClient (multi-process via socket)
REM
REM Usage:
REM   run.bat single    -> Run with InMemoryCommunication
REM   run.bat server    -> Start socket server (PlayerServer)
REM   run.bat client    -> Start socket client (PlayerClient)
REM ==========================================================

set MODE=%1

if "%MODE%"=="" (
  echo Usage: run.bat [single^|server^|client]
  exit /b 1
)

REM Ensure Java is installed
where javac >nul 2>nul
if errorlevel 1 (
  echo javac not found. Please install Java JDK 21 or later first.
  exit /b 1
)

REM Set source and output directories
set SRC_DIR=src
set OUT_DIR=target/classes

REM Clean previous build
echo Cleaning previous build...
if exist %OUT_DIR% rmdir /s /q %OUT_DIR%
mkdir %OUT_DIR% >nul 2>nul

REM Find all Java files and compile them
echo Compiling Java sources...
javac -d %OUT_DIR% -cp %SRC_DIR% %SRD_DIR%\*.java %SRC_DIR%\**\*.java 2>nul

if errorlevel 1 (
  echo Compilation failed. Please check your Java code.
  exit /b 1
)

echo Build successful.

REM Run in single-process (in-memory mediator)
if "%MODE%"=="single" (
  echo Starting game in SINGLE-PROCESS mode...
  java -cp %OUT_DIR% org.msgpingpong.core.GameController
)

REM Run in multi-process (server)
if "%MODE%"=="server" (
  echo Starting game in MULTI-PROCESS SERVER mode...
  java -cp %OUT_DIR% org.msgpingpong.core.PlayerServer
)

REM Run in multi-process (client)
if "%MODE%"=="client" (
  echo Starting game in MULTI-PROCESS CLIENT mode...
  java -cp %OUT_DIR% org.msgpingpong.core.PlayerClient
)