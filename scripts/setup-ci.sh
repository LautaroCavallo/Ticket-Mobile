#!/bin/bash

# 🚀 Script de configuración automática de CI/CD para Helpdesk Mobile App
# Ejecutar desde la raíz del proyecto Android

set -e  # Exit on any error

echo "🚀 Configurando CI/CD para Helpdesk Mobile App..."
echo "=================================================="

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir mensajes
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar que estamos en un proyecto Android
check_android_project() {
    print_status "Verificando estructura del proyecto Android..."
    
    if [ ! -f "build.gradle" ] && [ ! -f "build.gradle.kts" ]; then
        print_error "No se encontró build.gradle en la raíz. ¿Estás en la raíz del proyecto?"
        exit 1
    fi
    
    if [ ! -d "app" ]; then
        print_error "No se encontró la carpeta 'app'. ¿Estás en un proyecto Android?"
        exit 1
    fi
    
    print_success "Proyecto Android detectado correctamente"
}

# Crear estructura de directorios
create_directories() {
    print_status "Creando estructura de directorios..."
    
    mkdir -p .github/workflows
    print_success "Directorio .github/workflows creado"
}

# Copiar archivos de configuración
copy_config_files() {
    print_status "Copiando archivos de configuración..."
    
    # Verificar que los archivos fuente existen
    if [ ! -f "tpGeneral/.github-workflows-ci.yml" ]; then
        print_error "Archivo ci.yml no encontrado en tpGeneral/"
        exit 1
    fi
    
    # Copiar workflow de CI
    cp "tpGeneral/.github-workflows-ci.yml" ".github/workflows/ci.yml"
    print_success "Workflow CI copiado a .github/workflows/ci.yml"
    
    # Copiar .editorconfig
    if [ -f "tpGeneral/.editorconfig" ]; then
        cp "tpGeneral/.editorconfig" "./.editorconfig"
        print_success "Archivo .editorconfig copiado"
    else
        print_warning ".editorconfig no encontrado, saltando..."
    fi
    
    # Copiar detekt-config.yml
    if [ -f "tpGeneral/detekt-config.yml" ]; then
        cp "tpGeneral/detekt-config.yml" "./detekt-config.yml"
        print_success "Configuración Detekt copiada"
    else
        print_warning "detekt-config.yml no encontrado, saltando..."
    fi
}

# Actualizar build.gradle
update_build_gradle() {
    print_status "Preparando configuraciones para build.gradle..."
    
    BUILD_GRADLE_APP="app/build.gradle"
    BUILD_GRADLE_KTS="app/build.gradle.kts"
    
    # Detectar tipo de build.gradle
    if [ -f "$BUILD_GRADLE_APP" ]; then
        BUILD_FILE="$BUILD_GRADLE_APP"
        print_status "Detectado build.gradle (Groovy)"
    elif [ -f "$BUILD_GRADLE_KTS" ]; then
        BUILD_FILE="$BUILD_GRADLE_KTS"
        print_status "Detectado build.gradle.kts (Kotlin DSL)"
    else
        print_error "No se encontró app/build.gradle ni app/build.gradle.kts"
        exit 1
    fi
    
    # Crear backup
    cp "$BUILD_FILE" "$BUILD_FILE.backup"
    print_success "Backup creado: $BUILD_FILE.backup"
    
    # Mostrar instrucciones manuales
    echo ""
    print_warning "⚠️  ACCIÓN MANUAL REQUERIDA:"
    echo "   Debes agregar manualmente las dependencias al archivo: $BUILD_FILE"
    echo "   Consulta el archivo: tpGeneral/build-gradle-dependencies.gradle"
    echo "   O ejecuta: cat tpGeneral/build-gradle-dependencies.gradle"
    echo ""
}

# Crear archivos adicionales
create_additional_files() {
    print_status "Creando archivos adicionales..."
    
    # Crear lint-baseline.xml vacío
    if [ ! -f "app/lint-baseline.xml" ]; then
        echo '<?xml version="1.0" encoding="UTF-8"?>' > app/lint-baseline.xml
        echo '<issues format="6" by="lint 8.1.4">' >> app/lint-baseline.xml
        echo '</issues>' >> app/lint-baseline.xml
        print_success "Baseline de Android Lint creado"
    fi
    
    # Crear detekt-baseline.xml vacío
    if [ ! -f "detekt-baseline.xml" ]; then
        echo '<?xml version="1.0" encoding="UTF-8"?>' > detekt-baseline.xml
        echo '<SmellBaseline>' >> detekt-baseline.xml
        echo '  <ManuallySuppressedIssues></ManuallySuppressedIssues>' >> detekt-baseline.xml
        echo '  <CurrentIssues></CurrentIssues>' >> detekt-baseline.xml
        echo '</SmellBaseline>' >> detekt-baseline.xml
        print_success "Baseline de Detekt creado"
    fi
    
    # Crear dependency-check-suppressions.xml
    if [ ! -f "dependency-check-suppressions.xml" ]; then
        cat > dependency-check-suppressions.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
    <!-- Ejemplo de supresión para false positives -->
    <!-- 
    <suppress>
       <notes>False positive</notes>
       <filePath regex="true">.*\\.jar</filePath>
       <cve>CVE-XXXX-XXXX</cve>
    </suppress>
    -->
</suppressions>
EOF
        print_success "Archivo de supresiones Dependency Check creado"
    fi
}

# Validar configuración
validate_setup() {
    print_status "Validando configuración..."
    
    ERRORS=0
    
    # Verificar archivos críticos
    if [ ! -f ".github/workflows/ci.yml" ]; then
        print_error "Workflow CI no encontrado"
        ((ERRORS++))
    fi
    
    if [ ! -f ".editorconfig" ]; then
        print_warning ".editorconfig no encontrado"
    fi
    
    if [ ! -f "detekt-config.yml" ]; then
        print_warning "detekt-config.yml no encontrado"
    fi
    
    # Verificar permisos de gradlew
    if [ -f "gradlew" ]; then
        if [ ! -x "gradlew" ]; then
            print_status "Agregando permisos de ejecución a gradlew..."
            chmod +x gradlew
            print_success "Permisos agregados a gradlew"
        fi
    else
        print_warning "gradlew no encontrado"
    fi
    
    if [ $ERRORS -eq 0 ]; then
        print_success "Validación completada sin errores críticos"
    else
        print_error "Se encontraron $ERRORS errores críticos"
        exit 1
    fi
}

# Test build local
test_build() {
    print_status "¿Quieres ejecutar un build de prueba? (y/n)"
    read -r response
    
    if [[ "$response" =~ ^[Yy]$ ]]; then
        print_status "Ejecutando build de prueba..."
        
        if [ -x "gradlew" ]; then
            # Limpiar primero
            ./gradlew clean
            
            # Build básico
            if ./gradlew assembleDebug; then
                print_success "✅ Build exitoso!"
            else
                print_error "❌ Build falló. Revisa la configuración."
                exit 1
            fi
        else
            print_warning "gradlew no ejecutable, saltando test build"
        fi
    fi
}

# Mostrar resumen final
show_summary() {
    echo ""
    echo "🎉 ¡Configuración de CI/CD completada!"
    echo "======================================"
    echo ""
    print_success "✅ Archivos configurados:"
    echo "   - .github/workflows/ci.yml (GitHub Actions)"
    echo "   - .editorconfig (formato código)"
    echo "   - detekt-config.yml (análisis estático)"
    echo "   - lint-baseline.xml (Android Lint)"
    echo "   - detekt-baseline.xml (Detekt baseline)"
    echo ""
    print_warning "⚠️  Pendiente (manual):"
    echo "   - Actualizar app/build.gradle con dependencias"
    echo "   - Revisar archivo: tpGeneral/build-gradle-dependencies.gradle"
    echo "   - Configurar SonarCloud (opcional)"
    echo ""
    print_status "📚 Documentación:"
    echo "   - Consulta: tpGeneral/CI-CD-README.md"
    echo ""
    print_status "🚀 Próximos pasos:"
    echo "   1. git add ."
    echo "   2. git commit -m 'feat: setup CI/CD pipeline'"
    echo "   3. git push origin feature/ci-setup"
    echo "   4. Crear PR para probar el workflow"
    echo ""
    print_success "🎯 ¡El workflow se activará automáticamente en el próximo push!"
}

# Función principal
main() {
    echo "🔧 Iniciando configuración automática..."
    echo ""
    
    check_android_project
    create_directories
    copy_config_files
    update_build_gradle
    create_additional_files
    validate_setup
    test_build
    show_summary
}

# Verificar dependencias del script
check_dependencies() {
    if ! command -v git &> /dev/null; then
        print_error "Git no está instalado"
        exit 1
    fi
}

# Manejo de errores
trap 'print_error "Script interrumpido"; exit 1' INT TERM

# Verificar dependencias y ejecutar
check_dependencies
main

exit 0
