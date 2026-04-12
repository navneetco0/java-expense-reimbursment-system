@echo off
REM ============================================================================
REM Expense Reimbursement System - Windows Launcher
REM ============================================================================
REM This script checks for MySQL, starts the server, creates database and runs the app
REM Requirements: MySQL Server 5.7+ or MariaDB installed
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ======================================================================
echo Expense Reimbursement System - Windows Launcher
echo ======================================================================
echo.

REM Check if MySQL is installed
echo Checking for MySQL installation...
where mysql >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: MySQL is not installed or not in PATH
    echo.
    echo To fix this:
    echo 1. Install MySQL Community Server from: https://dev.mysql.com/downloads/mysql/
    echo 2. During installation, remember the MySQL root password (default: tiger)
    echo 3. Add MySQL bin folder to System Path
    echo 4. Restart this launcher
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL found
echo.

REM Check for Java
echo Checking for Java installation...
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Java is not installed or not in PATH
    echo.
    echo To fix this:
    echo 1. Install Java 21 or later from: https://www.oracle.com/java/technologies/javase-downloads.html
    echo 2. Add Java bin folder to System Path
    echo 3. Restart this launcher
    echo.
    pause
    exit /b 1
)
echo [OK] Java found
echo.

REM Get the script directory
set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%"

REM Check if MySQL driver exists
if not exist "lib\mysql-connector-j-9.6.0.jar" (
    echo ERROR: MySQL JDBC driver not found at lib\mysql-connector-j-9.6.0.jar
    echo Please ensure the mysql-connector-j library is in the lib folder
    pause
    exit /b 1
)

echo Rebuilding application...
call build.bat
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Starting MySQL server (if not already running)...
REM Try to connect to MySQL to check if it's running
mysql -u root -ptiger -e "SELECT 1;" >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo.
    echo Attempting to start MySQL service...
    net start MySQL80 >nul 2>nul
    if %ERRORLEVEL% neq 0 (
        REM Try alternative service names
        net start MySQL >nul 2>nul
        if %ERRORLEVEL% neq 0 (
            net start MariaDB >nul 2>nul
            if %ERRORLEVEL% neq 0 (
                echo WARNING: Could not start MySQL service
                echo Please start MySQL manually
                echo.
                pause
            ) else (
                echo [OK] MariaDB service started
            )
        ) else (
            echo [OK] MySQL service started
        )
    ) else (
        echo [OK] MySQL service started
    )
    timeout /t 2 /nobreak
)

REM Verify MySQL is accessible
mysql -u root -ptiger -e "SELECT 1;" >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Cannot connect to MySQL server
    echo Please ensure:
    echo   1. MySQL is running
    echo   2. Root user password is correct (default: tiger)
    echo   3. MySQL is listening on localhost:3306
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL server is running
echo.

REM Launch the application
echo ======================================================================
echo Launching Expense Reimbursement System...
echo ======================================================================
echo.

REM Create bin directory if needed
if not exist "bin" mkdir bin

REM Check if app.jar exists
if not exist "app.jar" (
    echo ERROR: app.jar not found. Build may have failed.
    pause
    exit /b 1
)

REM Run the application
java -Xmx512m -cp "app.jar;lib/mysql-connector-j-9.6.0.jar" ^
    -Dapple.laf.useScreenMenuBar=true ^
    -Dapple.awt.application.name="Expense Reimbursement System" ^
    App

:end
echo.
echo Application closed.
pause
