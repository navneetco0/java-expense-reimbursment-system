@echo off
REM ============================================================================
REM Expense Reimbursement System - Windows Build Script
REM ============================================================================
REM Compiles Java source, creates JAR, and packages for Windows

setlocal enabledelayedexpansion

echo.
echo ============================================================================
echo Expense Reimbursement System - Windows Build
echo ============================================================================
echo.

REM Check if Java is installed
where javac >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java compiler not found. Please install JDK
    exit /b 1
)

REM Clean previous build
echo 🧹 Cleaning...
if exist bin rmdir /s /q bin
if exist output rmdir /s /q output
if exist input rmdir /s /q input
mkdir bin input\lib 2>nul

REM Compile
echo ⚙️  Compiling...
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src/*.java
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed
    exit /b 1
)
echo ✓ Compilation successful

REM Copy assets
if exist src\assets (
    echo 🖼️  Copying assets...
    xcopy src\assets bin\assets /E /I /Y >nul
)

REM Create JAR
echo 📦 Creating JAR...
jar cfm app.jar manifest.txt -C bin .
if %ERRORLEVEL% neq 0 (
    echo ERROR: JAR creation failed
    exit /b 1
)
echo ✓ JAR created (app.jar)

REM Verify JAR contents
echo 🔍 Verifying JAR...
jar tf app.jar | findstr /C:"DatabaseInitializer.class" >nul
if %ERRORLEVEL% neq 0 (
    echo WARNING: DatabaseInitializer class not found in JAR
)

REM Package for distribution (optional)
echo 📂 Preparing distribution...
copy app.jar input\ >nul
copy lib\mysql-connector-j-9.6.0.jar input\lib\ >nul

REM Create launcher batch file
echo Creating launcher...
(
    echo @echo off
    echo cd /d "%%~dp0"
    echo java -Xmx512m -cp "app.jar;lib/mysql-connector-j-9.6.0.jar" App
    echo pause
) > input\launch.bat

echo.
echo 🎉 Build complete!
echo    - JAR file: app.jar
echo    - Binary files: bin\
echo    - Distribution ready in: input\
echo.
