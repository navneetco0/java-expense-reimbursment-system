#!/bin/bash

# ============================================================================
# Expense Reimbursement System - macOS Launcher
# ============================================================================
# This script checks for MySQL, creates database and runs the app
# Requirements: MySQL Server 5.7+ (via Homebrew or DMG installer)
# ============================================================================

set -e  # Exit on error

clear
echo ""
echo "======================================================================"
echo "Expense Reimbursement System - macOS Launcher"
echo "======================================================================"
echo ""

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print messages
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Check if MySQL is installed
log_info "Checking for MySQL installation..."
if ! command -v mysql &> /dev/null; then
    log_error "MySQL is not installed or not in PATH"
    echo ""
    echo "To install MySQL on macOS:"
    echo "1. Using Homebrew (recommended):"
    echo "   brew install mysql"
    echo "   brew services start mysql"
    echo ""
    echo "2. Or download DMG installer from:"
    echo "   https://dev.mysql.com/downloads/mysql/"
    echo ""
    exit 1
fi
log_info "MySQL found at: $(which mysql)"

# Check for Java
log_info "Checking for Java installation..."
if ! command -v java &> /dev/null; then
    log_error "Java is not installed or not in PATH"
    echo ""
    echo "To install Java on macOS:"
    echo "1. Using Homebrew:"
    echo "   brew install java"
    echo ""
    echo "2. Or download from:"
    echo "   https://www.oracle.com/java/technologies/javase-downloads.html"
    echo ""
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
log_info "Java found: $JAVA_VERSION"

# Check if MySQL JDBC driver exists
if [ ! -f "lib/mysql-connector-j-9.6.0.jar" ]; then
    log_error "MySQL JDBC driver not found at lib/mysql-connector-j-9.6.0.jar"
    echo "Please ensure the mysql-connector-j library is in the lib folder"
    exit 1
fi
log_info "MySQL JDBC driver found"

# Check for MySQL connection
log_info "Checking MySQL connection..."
if ! mysql -u root -ptiger -e "SELECT 1;" &> /dev/null; then
    log_warn "MySQL server is not responding"
    echo ""
    echo "Starting MySQL server..."
    
    # Try Homebrew MySQL
    if command -v brew &> /dev/null && brew services list | grep -q mysql; then
        brew services start mysql 2>/dev/null || true
        sleep 2
    fi
    
    # Verify connection again
    if ! mysql -u root -ptiger -e "SELECT 1;" &> /dev/null; then
        log_error "Cannot connect to MySQL server"
        echo ""
        echo "Please ensure:"
        echo "  1. MySQL is installed: brew install mysql"
        echo "  2. MySQL is running: brew services start mysql"
        echo "  3. Root password is 'tiger' (or modify run.sh and DatabaseInitializer.java)"
        echo ""
        exit 1
    fi
fi
log_info "MySQL server is running"

# Build the application
echo ""
log_info "Building application..."
if [ ! -f "build.sh" ]; then
    log_error "build.sh not found in current directory"
    exit 1
fi

chmod +x build.sh
./build.sh 2>&1 | grep -E "(✓|✅|🎉|error|Error)" || true

if [ ! -f "app.jar" ]; then
    log_error "Build failed - app.jar not found"
    exit 1
fi
log_info "Build complete"

# Launch the application
echo ""
echo "======================================================================"
echo "Launching Expense Reimbursement System..."
echo "======================================================================"
echo ""

# Create bin directory if needed
mkdir -p bin

# Run the application
java -Xmx512m -cp "app.jar:lib/mysql-connector-j-9.6.0.jar" \
    -Dapple.laf.useScreenMenuBar=true \
    -Dapple.awt.application.name="Expense Reimbursement System" \
    App

echo ""
log_info "Application closed"
