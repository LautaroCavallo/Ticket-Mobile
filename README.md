# Sistema de Gestión de Tickets

![Android CI](https://github.com/facundocores/Ticket-Mobile/workflows/Android%20CI/badge.svg)

## Descripción
Este proyecto es un **sistema de gestión de tickets** desarrollado en el marco de la materia *Desarrollo de Aplicaciones I* (UADE, 2025).  
Permite a los usuarios reportar incidencias, a los agentes de soporte gestionarlas y a los administradores supervisar el proceso.

El sistema incluye:
- Creación, actualización y cierre de tickets.
- Clasificación por categorías (ej. Hardware, Software, Redes).
- Roles diferenciados de usuario (admin, soporte y usuario).
- Comentarios y seguimiento dentro de cada ticket.

---

##  Roles del Equipo
- **Product Owner / API Rest:** Lautaro Cavallo  
- **UX/UI:** Ivo Rubino  
- **Backend:** Tomás Liñeiro  
- **QA / DevOps:** Facundo Cores  

---

## Tecnologías
- **Frontend:** Android (Kotlin + Jetpack Compose)  
- **Backend:** Django 4.2.7 + Django REST Framework 3.14.0  
- **Autenticación:** JWT (Simple JWT)
- **Base de datos:** SQLite (desarrollo) / PostgreSQL (producción)  
- **API Rest:** OpenAPI 3.0 (Swagger) - 60+ endpoints REST
- **CI/CD:** GitHub Actions  

---

## Objetivos por Entregas
### H1 – Entrega Intermedia
- Figma con prototipo navegable  
- Flujo de pantallas definido  
- Repo inicializado con tablero de seguimiento (GitHub Projects)  
- DER inicial  
- Plan de pruebas (QA)  
- APK demo con pantallas mockeadas  
- Diagrama de arquitectura inicial  
- Swagger con endpoints iniciales (tickets, usuarios, categorías, comentarios)

### H2 – Entrega Final
- Feature set completo  
- Métricas de calidad y performance  
- APK Release Candidate  
- Documentación final (técnica y de usuario)  
- Demo lista para defensa  

---

## 📐 Arquitectura del Sistema

El sistema implementa una arquitectura de **4 capas**:

1. **Capa de Presentación** - App Android (Kotlin + Jetpack Compose)
2. **Capa de API REST** - Django REST Framework con JWT
3. **Capa de Lógica de Negocio** - 8 Apps Django modulares
4. **Capa de Persistencia** - Base de datos relacional

### Documentación de Arquitectura

- **[📖 Arquitectura Final Completa](docs/arquitectura-final.md)** - Documentación técnica detallada
- **[📊 Diagrama Visual](docs/diagrama-arquitectura-final.txt)** - Diagrama ASCII de la arquitectura
- **[🗄️ Diagrama Entidad-Relación](docs/diagrama-e-relacion.jpg)** - Modelo de datos

### API REST - 8 Apps Implementadas

```
✅ Authentication  (7 endpoints)  → Registro, Login, JWT
✅ Users          (7 endpoints)  → Gestión de usuarios y permisos
✅ Tickets        (10 endpoints) → CRUD tickets con roles
✅ Comments       (5 endpoints)  → Comentarios públicos/privados
✅ Attachments    (4 endpoints)  → Upload de archivos
✅ Categories     (5 endpoints)  → Gestión de categorías
✅ Metrics        (4 endpoints)  → Estadísticas del sistema
✅ Common         (3 endpoints)  → Health check, utilidades
```

**Total:** 60+ endpoints REST completamente implementados

---

## Organización del Repo

- **`/docs`** → Documentación del proyecto
  - `arquitectura-final.md` - Arquitectura completa del sistema
  - `diagrama-arquitectura-final.txt` - Diagrama visual
  - `diagrama-e-relacion.jpg` - DER
  - `plan-pruebas-h1.md` - Plan de pruebas
  - `metricas-calidad-performance.md` - Métricas
  
- **`/frontend`** → Aplicación Android (Kotlin + Jetpack Compose)
  
- **`/backend`** → API REST Django
  - `apps/` - 8 apps modulares (authentication, users, tickets, comments, etc.)
  - `IMPLEMENTATION.md` - Documentación técnica de la API
  - `QUICKSTART.md` - Guía rápida de inicio
  
- **`/api`** → Especificación OpenAPI (`swagger.yaml`)
  
- **`/db`** → Scripts SQL (schema, seeds, queries)
  
- **`/scripts`** → Scripts de CI/CD y automatización
  
- **`/.github/workflows`** → Pipelines GitHub Actions

---

## 🚀 Instalación y Ejecución

### Backend (API REST)

```bash
# 1. Ir al directorio backend
cd backend

# 2. Crear entorno virtual (opcional pero recomendado)
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate

# 3. Instalar dependencias
pip install -r requirements.txt

# 4. Ejecutar migraciones
python manage.py makemigrations
python manage.py migrate

# 5. Crear superusuario
python manage.py createsuperuser

# 6. Ejecutar servidor
python manage.py runserver
```

**API disponible en:** `http://localhost:8000`

Ver [backend/QUICKSTART.md](backend/QUICKSTART.md) para más detalles.

### Frontend (Android App)

```bash
# 1. Abrir el proyecto en Android Studio
# 2. Sincronizar Gradle
# 3. Ejecutar en emulador o dispositivo
```

Ver [frontend/README.md](frontend/README.md) para más detalles.

---

## 📚 Documentación Adicional

- **[🔄 CI/CD Documentation](CI-CD.md)** - Integración y Despliegue Continuo
- **[API Implementation](backend/IMPLEMENTATION.md)** - Documentación técnica completa de la API
- **[Quick Start Guide](backend/QUICKSTART.md)** - Guía rápida para ejecutar la API
- **[Arquitectura del Sistema](docs/arquitectura-final.md)** - Arquitectura completa
- **[Plan de Pruebas](docs/plan-pruebas-h1.md)** - Estrategia de testing

---

## 📝 Licencia

Este proyecto es desarrollado con fines educativos para la materia Desarrollo de Aplicaciones I - UADE 2025
