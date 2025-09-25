# 🚀 CI/CD Setup - Helpdesk Mobile App

Este documento explica la configuración de **Continuous Integration** para la aplicación móvil de helpdesk usando **GitHub Actions**.

## 📁 Archivos de Configuración

```
proyecto/
├── .github/
│   └── workflows/
│       └── ci.yml                    # Workflow principal de CI
├── .editorconfig                     # Configuración de formato de código
├── detekt-config.yml                 # Configuración de análisis estático
├── build-gradle-dependencies.gradle  # Dependencias para linters
└── CI-CD-README.md                   # Esta documentación
```

## ⚙️ Configuración Inicial

### 1. **Setup del Workflow GitHub Actions**

Crea la carpeta `.github/workflows/` en la raíz de tu proyecto y copia el archivo `ci.yml` renombrándolo:

```bash
mkdir -p .github/workflows
cp .github-workflows-ci.yml .github/workflows/ci.yml
```

### 2. **Configurar Dependencias en build.gradle**

Agrega al archivo `app/build.gradle` el contenido de `build-gradle-dependencies.gradle`:

```gradle
// En app/build.gradle - sección plugins
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'org.jlleitschuh.gradle.ktlint' version '11.6.1'
    id 'io.gitlab.arturbosch.detekt' version '1.23.4'
    id 'jacoco'
}

// Agregar las configuraciones del archivo build-gradle-dependencies.gradle
```

### 3. **Copiar Archivos de Configuración**

```bash
# Configuración de formato de código
cp .editorconfig ./

# Configuración de Detekt
cp detekt-config.yml ./
```

## 🔧 Linters y Herramientas Incluidas

### **1. Ktlint** - Formato de Código Kotlin
- ✅ **Propósito**: Formato consistente del código Kotlin
- ✅ **Configuración**: `.editorconfig` + plugin gradle
- ✅ **Ejecutar local**: `./gradlew ktlintCheck`
- ✅ **Auto-fix**: `./gradlew ktlintFormat`

### **2. Android Lint** - Análisis Android Específico
- ✅ **Propósito**: Detectar problemas específicos de Android
- ✅ **Configuración**: `android.lintOptions` en build.gradle
- ✅ **Ejecutar local**: `./gradlew lintDebug`
- ✅ **Reportes**: HTML + XML en `build/reports/lint-results/`

### **3. Detekt** - Análisis Estático de Código
- ✅ **Propósito**: Detectar code smells, complejidad, bugs potenciales
- ✅ **Configuración**: `detekt-config.yml`
- ✅ **Ejecutar local**: `./gradlew detekt`
- ✅ **Reportes**: HTML + XML + TXT en `build/reports/detekt/`

### **4. JaCoCo** - Cobertura de Pruebas
- ✅ **Propósito**: Medir cobertura de tests unitarios
- ✅ **Ejecutar local**: `./gradlew jacocoTestReport`
- ✅ **Reportes**: HTML en `build/reports/jacoco/`

### **5. OWASP Dependency Check** - Seguridad
- ✅ **Propósito**: Detectar vulnerabilidades en dependencias
- ✅ **Ejecutar local**: `./gradlew dependencyCheckAnalyze`
- ✅ **Reportes**: HTML en `build/reports/`

## 🚦 Workflow de GitHub Actions

### **Triggers Automáticos**
- ✅ **Push** a ramas `main` y `develop`
- ✅ **Pull Requests** hacia `main` y `develop`

### **Jobs Ejecutados**

#### **Job 1: Build & Lint Check**
1. **Setup** del ambiente (Java 17, Gradle cache)
2. **Ktlint Check** - formato de código
3. **Android Lint** - análisis específico Android
4. **Build** - compilación del APK debug
5. **Unit Tests** - ejecución de tests
6. **Coverage Report** - reporte de cobertura
7. **Upload Artifacts** - APK + reportes

#### **Job 2: Security Check**
1. **Dependency Check** - vulnerabilidades en libs
2. **Detekt Analysis** - análisis estático avanzado
3. **Upload Security Reports**

#### **Job 3: Code Quality** (solo PRs)
1. **SonarCloud Scan** - análisis de calidad (requiere configuración)

## 📊 Reportes Generados

Los siguientes reportes están disponibles como **artifacts** en cada build:

| Reporte | Ubicación | Descripción |
|---------|-----------|-------------|
| **APK Debug** | `artifacts/debug-apk/` | APK compilado para testing |
| **Lint Report** | `artifacts/lint-report/` | Análisis Android Lint (HTML/XML) |
| **Test Reports** | `artifacts/test-reports/` | Resultados tests unitarios |
| **Security Reports** | `artifacts/security-reports/` | Vulnerabilidades + Detekt |
| **Coverage Report** | `build/reports/jacoco/` | Cobertura de tests (local) |

## 🛠️ Comandos Útiles para Desarrollo

### **Ejecutar Todos los Checks Localmente**
```bash
# Comando personalizado que ejecuta todo
./gradlew fullCheck

# O ejecutar individualmente:
./gradlew ktlintCheck        # Formato de código
./gradlew lintDebug         # Android lint
./gradlew detekt            # Análisis estático
./gradlew testDebugUnitTest # Tests unitarios
./gradlew jacocoTestReport  # Cobertura
```

### **Auto-Fix de Formato**
```bash
./gradlew ktlintFormat      # Corrige formato automáticamente
```

### **Generar Baseline (ignorar issues existentes)**
```bash
./gradlew detektBaseline    # Genera baseline de Detekt
```

## 🔧 Configuración Avanzada

### **SonarCloud Integration** (Opcional)
Para habilitar análisis con SonarCloud:

1. **Crear cuenta** en [SonarCloud](https://sonarcloud.io)
2. **Agregar secrets** en GitHub:
   - `SONAR_TOKEN`: Token de SonarCloud
3. **Configurar** propiedades en `build.gradle`

### **Slack/Teams Notifications** (Opcional)
Para recibir notificaciones de builds:

```yaml
# Agregar al final de .github/workflows/ci.yml
- name: Slack Notification
  if: failure()
  uses: rtCamp/action-slack-notify@v2
  env:
    SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK }}
    SLACK_MESSAGE: "Build failed in ${{ github.repository }}"
```

### **Custom Quality Gates**
Modificar umbrales en `detekt-config.yml`:

```yaml
complexity:
  LongMethod:
    threshold: 40        # Máximo 40 líneas por método
  LargeClass:
    threshold: 500       # Máximo 500 líneas por clase
```

## 📋 Checklist de Implementación

- [ ] Crear carpeta `.github/workflows/`
- [ ] Copiar archivo `ci.yml` 
- [ ] Actualizar `app/build.gradle` con dependencias
- [ ] Copiar configuraciones (`.editorconfig`, `detekt-config.yml`)
- [ ] Probar build local: `./gradlew fullCheck`
- [ ] Crear PR de prueba para validar workflow
- [ ] Configurar branch protection rules (opcional)
- [ ] Setup de SonarCloud (opcional)

## 🚨 Troubleshooting

### **Build Falla en GitHub Actions**
1. Verificar que `gradlew` tiene permisos de ejecución
2. Revisar versión de Java (debe ser 17+)
3. Verificar sintaxis en archivos de configuración

### **Ktlint Falla Localmente**
```bash
# Ver detalles del error
./gradlew ktlintCheck --info

# Auto-fix
./gradlew ktlintFormat
```

### **Detekt Reporta Muchos Issues**
```bash
# Generar baseline para ignorar issues existentes
./gradlew detektBaseline
```

### **Tests No Ejecutan**
```bash
# Limpiar y rebuildar
./gradlew clean
./gradlew testDebugUnitTest --info
```

## 📈 Métricas y KPIs

El workflow proporciona las siguientes métricas:

- ✅ **Build Success Rate**: % de builds exitosos
- ✅ **Code Coverage**: % de código cubierto por tests
- ✅ **Code Quality Score**: Rating de SonarCloud
- ✅ **Security Issues**: Vulnerabilidades detectadas
- ✅ **Build Time**: Tiempo promedio de CI

---

## 🎯 Próximos Pasos

1. **Automatización de Deploy** a Google Play Console
2. **Integration Tests** con Espresso en CI
3. **Performance Testing** automatizado
4. **Security Scanning** más avanzado (SAST/DAST)

¿Necesitas ayuda con alguna configuración específica? ¡Consulta la documentación o abre un issue!
