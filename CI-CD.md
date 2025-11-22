# 🔄 CI/CD - Integración y Despliegue Continuo

## 📋 Índice
- [Descripción General](#descripción-general)
- [¿Cuándo se Ejecuta?](#cuándo-se-ejecuta)
- [Pipeline de CI/CD](#pipeline-de-cicd)
- [Ver Resultados](#ver-resultados)
- [Artefactos Generados](#artefactos-generados)
- [Troubleshooting](#troubleshooting)
- [Configuración Avanzada](#configuración-avanzada)

---

## 📖 Descripción General

Este proyecto utiliza **GitHub Actions** para automatizar el proceso de integración continua (CI) y asegurar la calidad del código en cada cambio.

### ✨ Características principales:
- ✅ Análisis de código estático (Ktlint, Detekt, Android Lint)
- ✅ Ejecución automática de tests unitarios
- ✅ Generación de reportes de cobertura
- ✅ Compilación de APK de debug
- ✅ Comentarios automáticos en Pull Requests
- ✅ Retención de artefactos por 7 días

---

## 🚀 ¿Cuándo se Ejecuta?

El pipeline de CI/CD se ejecuta automáticamente en los siguientes casos:

### 1. **Push a ramas principales**
```yaml
branches: [ main, develop ]
```
- Cuando haces `git push` a `main` o `develop`
- Solo si hay cambios en `frontend/**` o `.github/workflows/ci.yml`

### 2. **Pull Requests**
```yaml
pull_request:
  branches: [ main, develop ]
```
- Cuando abres un PR hacia `main` o `develop`
- Solo si hay cambios en `frontend/**`

### 3. **Manualmente**
```yaml
workflow_dispatch:
```
- Puedes ejecutarlo manualmente desde GitHub Actions
- Ir a: **Actions** → **Android CI** → **Run workflow**

---

## 🔧 Pipeline de CI/CD

El pipeline está compuesto por los siguientes pasos:

### 📦 **1. Checkout code**
```bash
actions/checkout@v4
```
- Descarga el código del repositorio
- Incluye todo el historial para SonarCloud (`fetch-depth: 0`)

### ☕ **2. Set up JDK 11**
```bash
actions/setup-java@v4
```
- Instala Java Development Kit versión 11
- Distribución: Temurin (Eclipse)

### 🎯 **3. Setup Gradle**
```bash
gradle/actions/setup-gradle@v3
```
- Configura Gradle con caché optimizado
- Cache de solo lectura para ramas que no sean `main`

### 📝 **4. Análisis de Código**

#### a) **Ktlint Check**
```bash
./gradlew ktlintCheck
```
- Verifica el formato del código Kotlin
- Sigue las convenciones oficiales de Kotlin
- ⚠️ `continue-on-error: true` - No detiene el build si falla

#### b) **Detekt Analysis**
```bash
./gradlew detekt
```
- Análisis estático de código
- Detecta code smells y problemas potenciales
- ⚠️ `continue-on-error: true` - No detiene el build si falla

#### c) **Android Lint**
```bash
./gradlew lintDebug
```
- Análisis específico de Android
- Detecta problemas de performance, seguridad y compatibilidad
- ⚠️ `continue-on-error: true` - No detiene el build si falla

### 🧪 **5. Run Unit Tests**
```bash
./gradlew testDebugUnitTest
```
- Ejecuta todos los tests unitarios
- ❌ Si falla, detiene el pipeline

### 📊 **6. Generate Coverage Report**
```bash
./gradlew jacocoTestReport
```
- Genera reporte de cobertura de código
- Formato JaCoCo (HTML y XML)
- ⚠️ `continue-on-error: true` - No detiene el build si falla

### 🏗️ **7. Build Debug APK**
```bash
./gradlew assembleDebug
```
- Compila el APK de debug
- ❌ Si falla, detiene el pipeline

### 📤 **8. Upload Artifacts**

Se suben varios artefactos para revisión:

| Artefacto | Contenido | Retención |
|-----------|-----------|-----------|
| `debug-apk` | APK compilado | 7 días |
| `test-reports` | Resultados de tests | 7 días |
| `lint-reports` | Reportes de Lint y Detekt | 7 días |
| `coverage-reports` | Reportes de cobertura (JaCoCo) | 7 días |

### 💬 **9. Comment PR with Results**
- Solo en Pull Requests
- Comenta automáticamente con el resultado del build
- Incluye enlace a los artefactos

---

## 👀 Ver Resultados

### En GitHub:

1. **Ve a la pestaña Actions**
   ```
   https://github.com/[usuario]/Ticket-Mobile/actions
   ```

2. **Selecciona el workflow "Android CI"**

3. **Haz clic en el run específico**

4. **Revisa cada paso:**
   - ✅ Verde = Éxito
   - ❌ Rojo = Fallo
   - ⚠️ Amarillo = Advertencia

### Descargar Reportes:

1. Ve al workflow ejecutado
2. Scroll hasta **Artifacts**
3. Descarga los reportes que necesites:
   - `debug-apk` → APK para instalar
   - `test-reports` → Resultados de tests en HTML
   - `lint-reports` → Reportes de análisis estático
   - `coverage-reports` → Cobertura de tests

---

## 📦 Artefactos Generados

### 1. **Debug APK**
```
frontend/app/build/outputs/apk/debug/app-debug.apk
```
- APK instalable en dispositivos
- Incluye logs de debug
- No apto para producción

### 2. **Test Reports**
```
frontend/app/build/reports/tests/testDebugUnitTest/index.html
```
- Resumen de tests ejecutados
- Tests exitosos y fallidos
- Tiempo de ejecución

### 3. **Lint Reports**
```
frontend/app/build/reports/lint-results.html
frontend/app/build/reports/detekt/detekt.html
```
- Problemas detectados por categoría
- Severidad (Error, Warning, Info)
- Sugerencias de corrección

### 4. **Coverage Reports**
```
frontend/app/build/reports/jacoco/jacocoTestReport/html/index.html
```
- Porcentaje de cobertura por clase
- Líneas cubiertas vs no cubiertas
- Branches cubiertos

---

## 🔍 Troubleshooting

### ❌ **Build falla en Ktlint**

**Problema:** Código no sigue las convenciones de Kotlin

**Solución:**
```bash
cd frontend
./gradlew ktlintFormat  # Auto-formatea el código
./gradlew ktlintCheck   # Verifica que esté correcto
```

### ❌ **Tests fallan**

**Problema:** Algún test unitario no pasa

**Solución:**
1. Ejecuta localmente:
   ```bash
   cd frontend
   ./gradlew testDebugUnitTest --info
   ```
2. Revisa el reporte en `app/build/reports/tests/`
3. Corrige el test o la funcionalidad

### ❌ **Build falla en Detekt**

**Problema:** Code smells o problemas de código

**Solución:**
```bash
cd frontend
./gradlew detekt
# Revisa el reporte en app/build/reports/detekt/
```

### ❌ **Build APK falla**

**Problema:** Error de compilación

**Solución:**
1. Ejecuta localmente:
   ```bash
   cd frontend
   ./gradlew assembleDebug --stacktrace
   ```
2. Revisa los errores de compilación
3. Verifica dependencias en `build.gradle.kts`

### ⚠️ **Pipeline muy lento**

**Problema:** El pipeline tarda más de 10-15 minutos

**Soluciones:**
- El caché de Gradle debería acelerar builds subsecuentes
- Verifica que no estés haciendo `clean` innecesariamente
- Revisa el timeout actual (30 minutos)

---

## ⚙️ Configuración Avanzada

### Modificar el Pipeline

El archivo de configuración está en:
```
.github/workflows/ci.yml
```

### Agregar nuevos pasos

```yaml
- name: Mi Nuevo Paso
  run: |
    cd frontend
    ./gradlew miTarea
```

### Cambiar ramas monitoreadas

```yaml
on:
  push:
    branches: [ main, develop, feature/* ]  # Agrega más ramas
```

### Modificar timeout

```yaml
jobs:
  build:
    timeout-minutes: 45  # Aumenta si es necesario
```

### Deshabilitar análisis específico

Comenta o elimina el paso que no necesites:

```yaml
# - name: Ktlint Check
#   run: |
#     cd frontend
#     ./gradlew ktlintCheck
```

### Variables de entorno

Agrega secrets en GitHub:
1. Settings → Secrets and variables → Actions
2. New repository secret

Úsalos en el workflow:
```yaml
env:
  MY_SECRET: ${{ secrets.MY_SECRET }}
```

---

## 📊 Badges

Agrega badges al README para mostrar el estado del CI:

### Badge de Build Status

```markdown
![Android CI](https://github.com/[usuario]/Ticket-Mobile/workflows/Android%20CI/badge.svg)
```

Reemplaza `[usuario]` con tu nombre de usuario de GitHub.

---

## 📚 Recursos Adicionales

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Build Scans](https://scans.gradle.com/)
- [Detekt Documentation](https://detekt.dev/)
- [Ktlint Documentation](https://pinterest.github.io/ktlint/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/)

---

## 🤝 Contribuir

Si encuentras problemas o tienes sugerencias para mejorar el pipeline:

1. Abre un issue describiendo el problema
2. Propón cambios vía Pull Request
3. Asegúrate de que el CI pase antes de solicitar revisión

---

**Última actualización:** Noviembre 2024

