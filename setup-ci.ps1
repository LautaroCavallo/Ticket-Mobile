# 🚀 Script de configuración automática de CI/CD para Helpdesk Mobile App
# Ejecutar desde la raíz del proyecto Android con PowerShell

param(
    [switch]$SkipTest,
    [switch]$Force
)

# Configuración de colores para output
$ErrorActionPreference = "Stop"

function Write-ColorOutput($ForegroundColor, $Message) {
    $originalColor = $Host.UI.RawUI.ForegroundColor
    $Host.UI.RawUI.ForegroundColor = $ForegroundColor
    Write-Output $Message
    $Host.UI.RawUI.ForegroundColor = $originalColor
}

function Write-Info($Message) {
    Write-ColorOutput "Cyan" "[INFO] $Message"
}

function Write-Success($Message) {
    Write-ColorOutput "Green" "[SUCCESS] $Message"
}

function Write-Warning($Message) {
    Write-ColorOutput "Yellow" "[WARNING] $Message"
}

function Write-Error($Message) {
    Write-ColorOutput "Red" "[ERROR] $Message"
}

# Función para verificar si estamos en un proyecto Android
function Test-AndroidProject {
    Write-Info "Verificando estructura del proyecto Android..."
    
    if (!(Test-Path "build.gradle") -and !(Test-Path "build.gradle.kts")) {
        Write-Error "No se encontró build.gradle en la raíz. ¿Estás en la raíz del proyecto?"
        exit 1
    }
    
    if (!(Test-Path "app" -PathType Container)) {
        Write-Error "No se encontró la carpeta 'app'. ¿Estás en un proyecto Android?"
        exit 1
    }
    
    Write-Success "Proyecto Android detectado correctamente"
}

# Crear estructura de directorios
function New-DirectoryStructure {
    Write-Info "Creando estructura de directorios..."
    
    $githubDir = ".github\workflows"
    if (!(Test-Path $githubDir)) {
        New-Item -ItemType Directory -Path $githubDir -Force | Out-Null
        Write-Success "Directorio .github\workflows creado"
    } else {
        Write-Warning "Directorio .github\workflows ya existe"
    }
}

# Copiar archivos de configuración
function Copy-ConfigFiles {
    Write-Info "Copiando archivos de configuración..."
    
    $sourceDir = "tpGeneral"
    
    # Verificar directorio fuente
    if (!(Test-Path $sourceDir)) {
        Write-Error "Directorio $sourceDir no encontrado"
        exit 1
    }
    
    # Copiar workflow de CI
    $sourceWorkflow = Join-Path $sourceDir ".github-workflows-ci.yml"
    $targetWorkflow = ".github\workflows\ci.yml"
    
    if (Test-Path $sourceWorkflow) {
        Copy-Item $sourceWorkflow $targetWorkflow -Force
        Write-Success "Workflow CI copiado a $targetWorkflow"
    } else {
        Write-Error "Archivo workflow no encontrado en $sourceWorkflow"
        exit 1
    }
    
    # Copiar .editorconfig
    $sourceEditor = Join-Path $sourceDir ".editorconfig"
    if (Test-Path $sourceEditor) {
        Copy-Item $sourceEditor ".\.editorconfig" -Force
        Write-Success "Archivo .editorconfig copiado"
    } else {
        Write-Warning ".editorconfig no encontrado, saltando..."
    }
    
    # Copiar detekt-config.yml
    $sourceDetekt = Join-Path $sourceDir "detekt-config.yml"
    if (Test-Path $sourceDetekt) {
        Copy-Item $sourceDetekt ".\detekt-config.yml" -Force
        Write-Success "Configuración Detekt copiada"
    } else {
        Write-Warning "detekt-config.yml no encontrado, saltando..."
    }
}

# Actualizar build.gradle
function Update-BuildGradle {
    Write-Info "Preparando configuraciones para build.gradle..."
    
    $buildGradleApp = "app\build.gradle"
    $buildGradleKts = "app\build.gradle.kts"
    
    $buildFile = $null
    if (Test-Path $buildGradleApp) {
        $buildFile = $buildGradleApp
        Write-Info "Detectado build.gradle (Groovy)"
    } elseif (Test-Path $buildGradleKts) {
        $buildFile = $buildGradleKts
        Write-Info "Detectado build.gradle.kts (Kotlin DSL)"
    } else {
        Write-Error "No se encontró app\build.gradle ni app\build.gradle.kts"
        exit 1
    }
    
    # Crear backup
    $backupFile = "$buildFile.backup"
    Copy-Item $buildFile $backupFile -Force
    Write-Success "Backup creado: $backupFile"
    
    # Mostrar instrucciones manuales
    Write-Host ""
    Write-Warning "⚠️  ACCIÓN MANUAL REQUERIDA:"
    Write-Host "   Debes agregar manualmente las dependencias al archivo: $buildFile"
    Write-Host "   Consulta el archivo: tpGeneral\build-gradle-dependencies.gradle"
    Write-Host "   O ejecuta: Get-Content tpGeneral\build-gradle-dependencies.gradle"
    Write-Host ""
}

# Crear archivos adicionales
function New-AdditionalFiles {
    Write-Info "Creando archivos adicionales..."
    
    # Crear lint-baseline.xml vacío
    $lintBaseline = "app\lint-baseline.xml"
    if (!(Test-Path $lintBaseline)) {
        @'
<?xml version="1.0" encoding="UTF-8"?>
<issues format="6" by="lint 8.1.4">
</issues>
'@ | Out-File -FilePath $lintBaseline -Encoding UTF8
        Write-Success "Baseline de Android Lint creado"
    }
    
    # Crear detekt-baseline.xml vacío
    $detektBaseline = "detekt-baseline.xml"
    if (!(Test-Path $detektBaseline)) {
        @'
<?xml version="1.0" encoding="UTF-8"?>
<SmellBaseline>
  <ManuallySuppressedIssues></ManuallySuppressedIssues>
  <CurrentIssues></CurrentIssues>
</SmellBaseline>
'@ | Out-File -FilePath $detektBaseline -Encoding UTF8
        Write-Success "Baseline de Detekt creado"
    }
    
    # Crear dependency-check-suppressions.xml
    $depCheckSuppressions = "dependency-check-suppressions.xml"
    if (!(Test-Path $depCheckSuppressions)) {
        @'
<?xml version="1.0" encoding="UTF-8"?>
<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
    <!-- Ejemplo de supresión para false positives -->
    <!-- 
    <suppress>
       <notes>False positive</notes>
       <filePath regex="true">.*\.jar</filePath>
       <cve>CVE-XXXX-XXXX</cve>
    </suppress>
    -->
</suppressions>
'@ | Out-File -FilePath $depCheckSuppressions -Encoding UTF8
        Write-Success "Archivo de supresiones Dependency Check creado"
    }
}

# Validar configuración
function Test-Setup {
    Write-Info "Validando configuración..."
    
    $errors = 0
    
    # Verificar archivos críticos
    if (!(Test-Path ".github\workflows\ci.yml")) {
        Write-Error "Workflow CI no encontrado"
        $errors++
    }
    
    if (!(Test-Path ".editorconfig")) {
        Write-Warning ".editorconfig no encontrado"
    }
    
    if (!(Test-Path "detekt-config.yml")) {
        Write-Warning "detekt-config.yml no encontrado"
    }
    
    # Verificar gradlew (en Windows puede ser gradlew.bat)
    if (Test-Path "gradlew.bat") {
        Write-Success "gradlew.bat encontrado"
    } elseif (Test-Path "gradlew") {
        Write-Success "gradlew encontrado"
    } else {
        Write-Warning "gradlew/gradlew.bat no encontrado"
    }
    
    if ($errors -eq 0) {
        Write-Success "Validación completada sin errores críticos"
    } else {
        Write-Error "Se encontraron $errors errores críticos"
        exit 1
    }
}

# Test build local
function Test-Build {
    if ($SkipTest) {
        Write-Info "Saltando test build (parámetro -SkipTest)"
        return
    }
    
    Write-Info "¿Quieres ejecutar un build de prueba? (y/n)"
    $response = Read-Host
    
    if ($response -match "^[Yy]") {
        Write-Info "Ejecutando build de prueba..."
        
        $gradlew = if (Test-Path "gradlew.bat") { ".\gradlew.bat" } else { ".\gradlew" }
        
        if (Test-Path $gradlew) {
            try {
                # Limpiar primero
                & $gradlew clean
                
                # Build básico
                & $gradlew assembleDebug
                Write-Success "✅ Build exitoso!"
            } catch {
                Write-Error "❌ Build falló. Revisa la configuración."
                Write-Error $_.Exception.Message
                exit 1
            }
        } else {
            Write-Warning "gradlew no encontrado, saltando test build"
        }
    }
}

# Mostrar resumen final
function Show-Summary {
    Write-Host ""
    Write-Success "🎉 ¡Configuración de CI/CD completada!"
    Write-Host "======================================"
    Write-Host ""
    Write-Success "✅ Archivos configurados:"
    Write-Host "   - .github\workflows\ci.yml (GitHub Actions)"
    Write-Host "   - .editorconfig (formato código)"
    Write-Host "   - detekt-config.yml (análisis estático)"
    Write-Host "   - lint-baseline.xml (Android Lint)"
    Write-Host "   - detekt-baseline.xml (Detekt baseline)"
    Write-Host ""
    Write-Warning "⚠️  Pendiente (manual):"
    Write-Host "   - Actualizar app\build.gradle con dependencias"
    Write-Host "   - Revisar archivo: tpGeneral\build-gradle-dependencies.gradle"
    Write-Host "   - Configurar SonarCloud (opcional)"
    Write-Host ""
    Write-Info "📚 Documentación:"
    Write-Host "   - Consulta: tpGeneral\CI-CD-README.md"
    Write-Host ""
    Write-Info "🚀 Próximos pasos:"
    Write-Host "   1. git add ."
    Write-Host "   2. git commit -m 'feat: setup CI/CD pipeline'"
    Write-Host "   3. git push origin feature/ci-setup"
    Write-Host "   4. Crear PR para probar el workflow"
    Write-Host ""
    Write-Success "🎯 ¡El workflow se activará automáticamente en el próximo push!"
}

# Función principal
function Main {
    try {
        Write-Host "🔧 Iniciando configuración automática..." -ForegroundColor Magenta
        Write-Host ""
        
        Test-AndroidProject
        New-DirectoryStructure
        Copy-ConfigFiles
        Update-BuildGradle
        New-AdditionalFiles
        Test-Setup
        Test-Build
        Show-Summary
        
    } catch {
        Write-Error "Error durante la configuración: $($_.Exception.Message)"
        exit 1
    }
}

# Verificar dependencias
function Test-Dependencies {
    # Verificar Git
    try {
        git --version | Out-Null
    } catch {
        Write-Error "Git no está instalado o no está en el PATH"
        exit 1
    }
    
    # Verificar PowerShell version
    if ($PSVersionTable.PSVersion.Major -lt 5) {
        Write-Error "Se requiere PowerShell 5.0 o superior"
        exit 1
    }
}

# Banner de inicio
Write-Host @"
🚀 Configurador CI/CD - Helpdesk Mobile App
==========================================
PowerShell Setup Script v1.0

Parámetros disponibles:
  -SkipTest    : Saltar test build
  -Force       : Sobrescribir archivos existentes

"@ -ForegroundColor Cyan

# Verificar dependencias y ejecutar
Test-Dependencies
Main

Write-Success "✨ Configuración completada exitosamente!"
