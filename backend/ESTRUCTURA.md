# 📁 Estructura del Proyecto Django - Helpdesk Backend

## 🏗️ Arquitectura General

```
backend/
├── 📁 helpdesk/                    # Proyecto principal Django
│   ├── 📁 settings/               # Configuraciones por ambiente
│   │   ├── 📄 __init__.py
│   │   ├── 📄 base.py            # Configuración base
│   │   ├── 📄 development.py     # Desarrollo local
│   │   └── 📄 production.py      # Producción
│   ├── 📄 __init__.py
│   ├── 📄 urls.py               # URLs principales
│   ├── 📄 wsgi.py               # WSGI application
│   └── 📄 asgi.py               # ASGI application
│
├── 📁 apps/                       # Aplicaciones Django
│   ├── 📁 authentication/        # 🔐 Autenticación JWT
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # (Vacío - usa User)
│   │   ├── 📄 serializers.py    # Login, Register, Token
│   │   ├── 📄 views.py          # Auth endpoints
│   │   ├── 📄 urls.py           # /api/auth/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 users/                 # 👥 Gestión de usuarios
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # User model personalizado
│   │   ├── 📄 serializers.py    # User serializers
│   │   ├── 📄 views.py          # Profile, admin endpoints
│   │   ├── 📄 urls.py           # /api/users/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 tickets/               # 🎫 Gestión de tickets
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # Ticket model
│   │   ├── 📄 serializers.py    # Ticket serializers
│   │   ├── 📄 views.py          # CRUD tickets
│   │   ├── 📄 urls.py           # /api/tickets/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 categories/            # 📂 Categorías
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # Categoria model
│   │   ├── 📄 serializers.py    # Category serializers
│   │   ├── 📄 views.py          # CRUD categories
│   │   ├── 📄 urls.py           # /api/categories/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 comments/              # 💬 Comentarios
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # Comment model
│   │   ├── 📄 serializers.py    # Comment serializers
│   │   ├── 📄 views.py          # CRUD comments
│   │   ├── 📄 urls.py           # /api/comments/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 attachments/           # 📎 Archivos adjuntos
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # Attachment model
│   │   ├── 📄 serializers.py    # Attachment serializers
│   │   ├── 📄 views.py          # Upload/download files
│   │   ├── 📄 urls.py           # /api/attachments/*
│   │   └── 📄 tests.py
│   │
│   ├── 📁 metrics/               # 📊 Métricas y estadísticas
│   │   ├── 📄 __init__.py
│   │   ├── 📄 apps.py
│   │   ├── 📄 models.py         # (Vacío - queries directas)
│   │   ├── 📄 serializers.py    # Metrics serializers
│   │   ├── 📄 views.py          # Analytics endpoints
│   │   ├── 📄 urls.py           # /api/metrics/*
│   │   └── 📄 tests.py
│   │
│   └── 📁 common/                # 🔧 Utilidades comunes
│       ├── 📄 __init__.py
│       ├── 📄 apps.py
│       ├── 📄 models.py         # Base models, mixins
│       ├── 📄 serializers.py    # Serializers base
│       ├── 📄 views.py          # Health check, utils
│       ├── 📄 urls.py           # /api/health/*
│       ├── 📄 utils.py          # Funciones utilitarias
│       ├── 📄 permissions.py    # Custom permissions
│       └── 📄 tests.py
│
├── 📁 static/                    # Archivos estáticos
├── 📁 media/                     # Archivos de medios
├── 📁 logs/                      # Logs de la aplicación
├── 📁 scripts/                   # Scripts de utilidad
│   ├── 📄 setup.sh             # Setup Linux/Mac
│   └── 📄 setup.bat            # Setup Windows
│
├── 📁 fixtures/                  # Datos iniciales
│   ├── 📄 initial_data.json    # Categorías, usuarios demo
│   └── 📄 sample_tickets.json  # Tickets de ejemplo
│
├── 📄 manage.py                  # Script de gestión Django
├── 📄 requirements.txt           # Dependencias Python
├── 📄 env.example               # Variables de entorno ejemplo
├── 📄 Dockerfile                # Configuración Docker
├── 📄 docker-compose.yml        # Orquestación servicios
├── 📄 README.md                 # Documentación principal
├── 📄 ESTRUCTURA.md             # Este archivo
└── 📄 .gitignore                # Archivos a ignorar
```

## 🔄 Flujo de Datos

### 1. **Request Flow**
```
Client Request → Django URLs → ViewSet → Serializer → Model → Database
                ↓
Client Response ← JSON Response ← Serializer ← Model ← Database
```

### 2. **Authentication Flow**
```
Login → JWT Token → Authentication Middleware → ViewSet
       ↓
Token Refresh → New Access Token → Continue Request
```

### 3. **File Upload Flow**
```
File Upload → Attachment View → File Storage → Database Record
             ↓
File Download ← File Storage ← Attachment View ← Client Request
```

## 🎯 Responsabilidades por App

### 🔐 **authentication**
- Login/Logout de usuarios
- Generación y refresh de tokens JWT
- Registro de nuevos usuarios
- Validación de credenciales

### 👥 **users**
- Gestión de perfiles de usuario
- Roles y permisos
- Información personal
- Administración de usuarios (admin)

### 🎫 **tickets**
- CRUD de tickets
- Estados y prioridades
- Asignación de tickets
- Filtros y búsqueda

### 📂 **categories**
- Gestión de categorías
- Clasificación de tickets
- Categorías predefinidas

### 💬 **comments**
- Comentarios en tickets
- Comentarios privados/públicos
- Historial de conversaciones

### 📎 **attachments**
- Subida de archivos
- Descarga de archivos
- Validación de tipos de archivo
- Gestión de storage

### 📊 **metrics**
- Estadísticas del sistema
- Métricas de performance
- Dashboards administrativos
- Reportes

### 🔧 **common**
- Utilidades compartidas
- Health checks
- Permisos personalizados
- Mixins y clases base

## 🚀 Próximos Pasos

### 1. **Implementar Serializers**
- Crear serializers para cada modelo
- Validaciones personalizadas
- Serializers anidados para relaciones

### 2. **Crear ViewSets**
- Implementar CRUD completo
- Filtros y paginación
- Permisos por endpoint

### 3. **Configurar URLs**
- Routing de API
- Documentación automática
- Versionado de API

### 4. **Testing**
- Tests unitarios
- Tests de integración
- Tests de performance

### 5. **Deployment**
- Configuración de producción
- Docker containers
- CI/CD pipeline

## 📝 Notas Importantes

- **Custom User Model**: Se usa `apps.users.User` en lugar del User por defecto
- **JWT Authentication**: Configurado con refresh tokens
- **File Storage**: Preparado para AWS S3 en producción
- **Database**: PostgreSQL en producción, SQLite en desarrollo
- **Caching**: Redis para cache y Celery
- **Documentation**: Swagger/OpenAPI automático con drf-spectacular

Esta estructura sigue las mejores prácticas de Django y está preparada para escalar según las necesidades del proyecto.
