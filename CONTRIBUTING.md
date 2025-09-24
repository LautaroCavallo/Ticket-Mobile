# Guía de Contribución

Este documento define las **reglas de colaboración técnica** para el proyecto *Sistema de Gestión de Tickets*.

---

## Flujo de trabajo (Issues + Project Board)
1. Cada tarea se crea como **Issue** en GitHub y se vincula al **Project** del repositorio.
2. **Milestones:**  
   - **H1** (entrega intermedia)  
   - **H2** (entrega final)
3. **Columnas del board:**
   - **No status:** entrada/triage (recién creado, aún sin columna).
   - **Backlog:** pendiente, no comprometido al sprint.
   - **Ready / To Do Sprint:** priorizado para el sprint actual.
   - **In progress:** en desarrollo.
   - **In review:** PR abierto / en pruebas.
   - **Done:** completado y mergeado.
4. **Quién mueve las tarjetas:** cada miembro mueve **sus propias issues**.  
   El **PO** prioriza (Backlog → Ready) y revisa el estado general.
5. **Labels sugeridos:** `UX`, `UI`, `Backend`, `API`, `DB`, `Auth`, `QA`, `DevOps`, `Tests`, `Docs`, `Metrics`.

---

## Definiciones de calidad
**Definition of Ready (para entrar en Ready):**
- Objetivo claro y criterios de aceptación definidos.
- Responsable asignado y milestone (H1/H2).
- Labels adecuadas.

**Definition of Done (para pasar a Done):**
- Implementado y probado.
- Documentado (README/Swagger/comentarios si aplica).
- Mergeado a `main` vía Pull Request.
- Issue cerrado y tarjeta en **Done**.

---

## Convenciones de commits
Usamos **Conventional Commits**:

- `feat:` nueva funcionalidad  
- `fix:` corrección de bug  
- `docs:` documentación (README, Swagger, etc.)  
- `style:` formato/estilo (sin cambiar lógica)  
- `refactor:` cambio interno sin alterar comportamiento  
- `test:` tests nuevos o actualizados  
- `chore:` mantenimiento/configuraciones/dependencias  
- `ci:` pipelines y automatizaciones

**Ejemplos:**
```
feat(api): add CRUD de categorías
fix(auth): validar email y password nulos
docs(readme): agregar objetivos de H1
chore: actualizar dependencias del backend
```

---

## Estrategia de ramas
- **main**: rama estable.  
- **feature/***: nuevas features → `feature/api-categorias`  
- **fix/***: hotfixes/bugs → `fix/login-null-pointer`  
- **docs/***: documentación → `docs/readme-h1`  
- **ci/***: pipelines → `ci/actions-build`

**Regla práctica:** abrir **Pull Requests** hacia `main`.  
Preferir **Squash & merge** para mantener un historial limpio.

---

## Versionado (Semantic Versioning)
Usamos **MAYOR.MINOR.PATCH** y tags `vX.Y.Z`:

- **MAYOR**: cambios incompatibles (breaking).  
- **MINOR**: nuevas features compatibles.  
- **PATCH**: fixes/ajustes menores.

**Hitos del proyecto:**
- **H1** → tag `v1.0.0`
- **H2** → tag `v2.0.0`

> Al cerrar un hito, crear el tag y generar _Release Notes_ con resumen de cambios y capturas.

---

## Colaboración
- Mantener descripciones claras y criterios de aceptación.    
- El tablero debe reflejar el **estado real** todos los días.

---

Cualquier mejora a esta guía se propone vía **Pull Request** a `CONTRIBUTING.md`.
