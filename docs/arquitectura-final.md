# 🏗️ Arquitectura Final del Sistema de Tickets

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Estado:** Implementado ✅

---

## 📋 Índice

1. [Visión General](#visión-general)
2. [Arquitectura de Capas](#arquitectura-de-capas)
3. [Estructura de la API REST](#estructura-de-la-api-rest)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Sistema de Permisos](#sistema-de-permisos)
6. [Diagrama de Componentes](#diagrama-de-componentes)
7. [Flujo de Datos](#flujo-de-datos)
8. [Tecnologías Utilizadas](#tecnologías-utilizadas)

---

## 🎯 Visión General

Sistema de gestión de tickets de soporte técnico compuesto por:

- **Frontend:** Aplicación móvil Android (Kotlin + Jetpack Compose)
- **Backend:** API REST (Django REST Framework)
- **Base de Datos:** SQLite (desarrollo) / PostgreSQL (producción)
- **Autenticación:** JWT (JSON Web Tokens)

### Usuarios del Sistema

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Usuario   │     │   Soporte   │     │    Admin    │
│   Regular   │     │  (Support)  │     │  (SysAdmin) │
└─────────────┘     └─────────────┘     └─────────────┘
      │                    │                    │
      └────────────────────┴────────────────────┘
                           │
                    ┌──────▼──────┐
                    │   Frontend  │
                    │   Android   │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   API REST  │
                    │   (Django)  │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ Base de     │
                    │ Datos       │
                    └─────────────┘
```

---

## 🏛️ Arquitectura de Capas

### Arquitectura General (4 Capas)

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                 │
│  ┌───────────────────────────────────────────────────┐  │
│  │  Frontend - Android App (Kotlin)                  │  │
│  │  • Activities/Fragments                           │  │
│  │  • ViewModels (MVVM)                              │  │
│  │  • Composables (Jetpack Compose)                  │  │
│  │  • Navigation                                     │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↕ HTTP/REST
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE API REST                     │
│  ┌───────────────────────────────────────────────────┐  │
│  │  Django REST Framework                            │  │
│  │  • Authentication (JWT)                           │  │
│  │  • Endpoints (60+)                                │  │
│  │  • Serializers                                    │  │
│  │  • Permission Classes                             │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↕ ORM
┌─────────────────────────────────────────────────────────┐
│                   CAPA DE LÓGICA DE NEGOCIO             │
│  ┌───────────────────────────────────────────────────┐  │
│  │  8 Apps Django                                    │  │
│  │  • Models (Business Logic)                        │  │
│  │  • Views (Controllers)                            │  │
│  │  • Permissions (Authorization)                    │  │
│  │  • Validations                                    │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↕ SQL
┌─────────────────────────────────────────────────────────┐
│                   CAPA DE PERSISTENCIA                  │
│  ┌───────────────────────────────────────────────────┐  │
│  │  Base de Datos (SQLite/PostgreSQL)                │  │
│  │  • Tablas                                         │  │
│  │  • Relaciones                                     │  │
│  │  • Índices                                        │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 🔌 Estructura de la API REST

### Apps Implementadas (8)

```
backend/
├── apps/
│   ├── authentication/      [Autenticación & JWT]
│   │   ├── serializers.py   • UserRegisterSerializer
│   │   ├── views.py         • UserLoginSerializer
│   │   └── urls.py          • ChangePasswordSerializer
│   │
│   ├── users/              [Gestión de Usuarios]
│   │   ├── models.py        • User (Custom)
│   │   ├── serializers.py   • UserSerializer
│   │   ├── views.py         • UserUpdateSerializer
│   │   ├── urls.py          • UserListSerializer
│   │   └── permissions.py   • IsAdminUser, IsSupportOrAdmin
│   │
│   ├── tickets/            [Core del Negocio]
│   │   ├── models.py        • Ticket
│   │   ├── serializers.py   • TicketListSerializer
│   │   ├── views.py         • TicketDetailSerializer
│   │   └── urls.py          • TicketCreateSerializer
│   │
│   ├── comments/           [Comentarios]
│   │   ├── models.py        • Comment
│   │   ├── serializers.py   • CommentSerializer
│   │   ├── views.py         • CommentCreateSerializer
│   │   └── urls.py
│   │
│   ├── attachments/        [Archivos Adjuntos]
│   │   ├── models.py        • Attachment
│   │   ├── serializers.py   • AttachmentSerializer
│   │   ├── views.py         • AttachmentUploadSerializer
│   │   └── urls.py
│   │
│   ├── categories/         [Categorías]
│   │   ├── models.py        • Category
│   │   ├── serializers.py   • CategorySerializer
│   │   ├── views.py
│   │   └── urls.py
│   │
│   ├── metrics/            [Métricas & Analytics]
│   │   ├── serializers.py   • TicketMetricsSerializer
│   │   ├── views.py         • PerformanceSerializer
│   │   └── urls.py          • UserActivitySerializer
│   │
│   └── common/             [Utilidades]
│       ├── views.py         • Health Check
│       ├── urls.py          • API Info
│       ├── exceptions.py    • Custom Exception Handler
│       ├── responses.py     • Response Helpers
│       └── serializers.py
│
└── helpdesk/               [Configuración Principal]
    ├── settings.py
    ├── urls.py
    └── wsgi.py
```

### Endpoints por App (60+ total)

#### 1. Authentication (7 endpoints)
```
POST   /api/auth/register/           → Registro
POST   /api/auth/login/              → Login (JWT)
POST   /api/auth/refresh/            → Refresh Token
POST   /api/auth/logout/             → Logout
GET    /api/auth/me/                 → Usuario actual
POST   /api/auth/change-password/    → Cambiar contraseña
POST   /api/auth/password-reset/     → Reset contraseña
```

#### 2. Users (7 endpoints)
```
GET    /api/users/                   → Listar usuarios (admin)
GET    /api/users/profile/           → Mi perfil
PUT    /api/users/profile/update/    → Actualizar perfil
GET    /api/users/{id}/              → Ver usuario (admin)
PUT    /api/users/{id}/role/         → Cambiar rol (admin)
PATCH  /api/users/{id}/activation/   → Activar/desactivar (admin)
DELETE /api/users/{id}/delete/       → Eliminar (admin)
```

#### 3. Tickets (10 endpoints)
```
GET    /api/tickets/                 → Listar tickets
POST   /api/tickets/create/          → Crear ticket
GET    /api/tickets/my-tickets/      → Mis tickets
GET    /api/tickets/assigned/        → Asignados (support)
GET    /api/tickets/unassigned/      → Sin asignar (support)
GET    /api/tickets/{id}/            → Ver detalle
PUT    /api/tickets/{id}/update/     → Actualizar
PATCH  /api/tickets/{id}/status/     → Cambiar estado (support)
PATCH  /api/tickets/{id}/assign/     → Asignar (support)
DELETE /api/tickets/{id}/delete/     → Eliminar (admin)
```

#### 4. Comments (5 endpoints)
```
GET    /api/tickets/{id}/comments/                → Listar
POST   /api/tickets/{id}/comments/create/         → Crear
GET    /api/tickets/{id}/comments/{cid}/          → Ver
PUT    /api/tickets/{id}/comments/{cid}/update/   → Actualizar
DELETE /api/tickets/{id}/comments/{cid}/delete/   → Eliminar
```

#### 5. Attachments (4 endpoints)
```
GET    /api/tickets/{id}/attachments/              → Listar
POST   /api/tickets/{id}/attachments/upload/       → Subir
GET    /api/tickets/{id}/attachments/{aid}/        → Ver
DELETE /api/tickets/{id}/attachments/{aid}/delete/ → Eliminar
```

#### 6. Categories (5 endpoints)
```
GET    /api/categories/              → Listar
POST   /api/categories/create/       → Crear (admin)
GET    /api/categories/{id}/         → Ver
PUT    /api/categories/{id}/update/  → Actualizar (admin)
DELETE /api/categories/{id}/delete/  → Eliminar (admin)
```

#### 7. Metrics (4 endpoints)
```
GET    /api/metrics/tickets/overview/     → Resumen tickets
GET    /api/metrics/tickets/performance/  → Performance
GET    /api/metrics/users/activity/       → Actividad (admin)
GET    /api/metrics/system/health/        → Estado sistema
```

#### 8. Common (3 endpoints)
```
GET    /api/health/    → Health check
GET    /api/info/      → Info API
GET    /api/version/   → Versión
```

---

## 🔐 Flujo de Autenticación

### Diagrama de Flujo JWT

```
┌─────────┐                                    ┌─────────┐
│ Cliente │                                    │   API   │
└────┬────┘                                    └────┬────┘
     │                                              │
     │  1. POST /api/auth/register/                │
     │     {email, password, ...}                  │
     ├────────────────────────────────────────────►│
     │                                              │
     │  2. Validar datos                           │
     │     • Email único                            │
     │     • Contraseña fuerte                      │
     │                                              │
     │  3. Crear usuario                            │
     │     • Hash password (bcrypt)                 │
     │     • role = 'user'                          │
     │                                              │
     │  4. Generar tokens JWT                       │
     │     • Access token (1h)                      │
     │     • Refresh token (30d)                    │
     │                                              │
     │  5. Response                                 │
     │     {user, accessToken, refreshToken}        │
     │◄────────────────────────────────────────────┤
     │                                              │
     │  6. Guardar tokens en storage               │
     │                                              │
     │  7. Request con autenticación                │
     │     Header: "Authorization: Bearer TOKEN"    │
     ├────────────────────────────────────────────►│
     │                                              │
     │  8. Validar JWT                              │
     │     • Verificar firma                        │
     │     • Verificar expiración                   │
     │     • Obtener user_id                        │
     │                                              │
     │  9. Response con datos                       │
     │◄────────────────────────────────────────────┤
     │                                              │
     │  10. Si token expira                         │
     │      POST /api/auth/refresh/                 │
     │      {refreshToken}                          │
     ├────────────────────────────────────────────►│
     │                                              │
     │  11. Nuevo access token                      │
     │◄────────────────────────────────────────────┤
     │                                              │
```

### Tokens JWT

#### Access Token (1 hora)
```json
{
  "token_type": "access",
  "exp": 1234567890,
  "iat": 1234564290,
  "jti": "abc123...",
  "user_id": 1
}
```

#### Refresh Token (30 días)
```json
{
  "token_type": "refresh",
  "exp": 1237156290,
  "iat": 1234564290,
  "jti": "xyz789...",
  "user_id": 1
}
```

---

## 🛡️ Sistema de Permisos

### Arquitectura de Permisos

```
┌─────────────────────────────────────────────────────────┐
│                Request con JWT Token                     │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│          1. JWT Authentication Middleware                │
│             • Validar token                              │
│             • Extraer user_id                            │
│             • request.user = User                        │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│          2. Permission Classes                           │
│             • IsAuthenticated (base)                     │
│             • IsAdminUser (custom)                       │
│             • IsSupportOrAdmin (custom)                  │
│             • IsOwnerOrAdmin (custom)                    │
└────────────────────────┬────────────────────────────────┘
                         │
                    ┌────┴────┐
                    │ Granted │
                    └────┬────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│          3. View Logic                                   │
│             • Filtros adicionales por rol                │
│             • Validaciones de negocio                    │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│          4. Response                                     │
└─────────────────────────────────────────────────────────┘
```

### Matriz de Permisos Detallada

| Recurso | User | Support | Admin |
|---------|------|---------|-------|
| **Autenticación** |
| Register | ✅ Público | ✅ Público | ✅ Público |
| Login | ✅ Público | ✅ Público | ✅ Público |
| Logout | ✅ | ✅ | ✅ |
| Cambiar contraseña | ✅ Propia | ✅ Propia | ✅ Propia |
| **Usuarios** |
| Ver lista | ❌ | ❌ | ✅ |
| Ver perfil propio | ✅ | ✅ | ✅ |
| Ver perfil ajeno | ❌ | ❌ | ✅ |
| Actualizar propio | ✅ | ✅ | ✅ |
| Actualizar ajeno | ❌ | ❌ | ✅ |
| Cambiar rol | ❌ | ❌ | ✅ |
| Eliminar usuario | ❌ | ❌ | ✅ |
| **Tickets** |
| Ver propios | ✅ | ✅ | ✅ |
| Ver asignados | ❌ | ✅ | ✅ |
| Ver sin asignar | ❌ | ✅ | ✅ |
| Ver todos | ❌ | ❌ | ✅ |
| Crear | ✅ | ✅ | ✅ |
| Actualizar propios (open) | ✅ | ❌ | ✅ |
| Actualizar asignados | ❌ | ✅ | ✅ |
| Cambiar estado | ❌ | ✅ (asignados) | ✅ |
| Asignar | ❌ | ✅ | ✅ |
| Eliminar | ❌ | ❌ | ✅ |
| **Comentarios** |
| Ver en propios tickets | ✅ | ✅ | ✅ |
| Ver privados | ❌ | ✅ | ✅ |
| Crear | ✅ | ✅ | ✅ |
| Crear privados | ❌ | ✅ | ✅ |
| Actualizar propios | ✅ | ✅ | ✅ |
| Eliminar propios | ✅ | ✅ | ✅ |
| Eliminar ajenos | ❌ | ❌ | ✅ |
| **Attachments** |
| Ver públicos | ✅ | ✅ | ✅ |
| Ver privados | ❌ | ✅ | ✅ |
| Subir | ✅ | ✅ | ✅ |
| Subir privados | ❌ | ✅ | ✅ |
| Eliminar propios | ✅ | ✅ | ✅ |
| Eliminar ajenos | ❌ | ❌ | ✅ |
| **Categorías** |
| Ver | ✅ | ✅ | ✅ |
| Crear | ❌ | ❌ | ✅ |
| Actualizar | ❌ | ❌ | ✅ |
| Eliminar | ❌ | ❌ | ✅ |
| **Métricas** |
| Overview tickets | ❌ | ✅ | ✅ |
| Performance | ❌ | ✅ | ✅ |
| Actividad usuarios | ❌ | ❌ | ✅ |
| Health sistema | ❌ | ✅ | ✅ |

### Implementación de Permisos

```python
# apps/users/permissions.py

class IsAdminUser(BasePermission):
    """Solo sysAdmin"""
    def has_permission(self, request, view):
        return request.user.role == 'sysAdmin'

class IsSupportOrAdmin(BasePermission):
    """Support o sysAdmin"""
    def has_permission(self, request, view):
        return request.user.role in ['support', 'sysAdmin']

class IsOwnerOrAdmin(BasePermission):
    """Dueño del recurso o sysAdmin"""
    def has_object_permission(self, request, view, obj):
        if request.user.role == 'sysAdmin':
            return True
        return obj.creator == request.user
```

---

## 🧩 Diagrama de Componentes

### Relación entre Modelos

```
┌─────────────────┐
│      User       │◄──────────┐
│  • email        │           │
│  • role         │           │ creator
│  • is_active    │           │
└────────┬────────┘           │
         │                    │
         │ assignee      ┌────┴──────┐
         │               │  Ticket   │
         └──────────────►│ • title   │
                         │ • status  │◄───────────┐
                         │ • priority│            │ ticket
                         └────┬──────┘            │
                              │                   │
                 ┌────────────┼────────────┐      │
                 │            │            │      │
            ticket│       ticket│     ticket│      │
                 │            │            │      │
          ┌──────▼──────┐ ┌──▼────────┐ ┌▼───────────┐
          │  Comment    │ │Attachment │ │  Category  │
          │  • text     │ │• file     │ │  • name    │
          │  • author   │ │• size     │ │  • desc    │
          │  • private  │ │• private  │ └────────────┘
          └─────────────┘ └───────────┘
```

### Base de Datos (Tablas)

```sql
-- Users
users (
    id, email, first_name, last_name,
    role, profile_picture, created_at,
    last_login, is_active
)

-- Tickets
tickets (
    id, title, description, status,
    priority, creator_id, assignee_id,
    created_at, updated_at, resolved_at
)
FK: creator_id → users.id
FK: assignee_id → users.id

-- Comments
comments (
    id, text, author_id, ticket_id,
    created_at, is_private
)
FK: author_id → users.id
FK: ticket_id → tickets.id

-- Attachments
attachments (
    id, ticket_id, uploaded_by_id,
    file, original_filename, file_size,
    mime_type, created_at, is_private
)
FK: ticket_id → tickets.id
FK: uploaded_by_id → users.id

-- Categories
categories (
    id, name, description, created_at
)
```

---

## 🔄 Flujo de Datos

### 1. Crear Ticket (Completo)

```
[User] → [Frontend]
           │
           │ POST /api/tickets/create/
           │ {title, description, priority}
           │ Header: Authorization: Bearer TOKEN
           │
           ▼
      [API Gateway]
           │
           │ JWT Authentication
           ▼
    [Authentication]
           │ ✓ Token válido
           │ ✓ Usuario activo
           │
           ▼
    [Ticket View]
           │
           │ Permission: IsAuthenticated
           ▼
    [Ticket Serializer]
           │
           │ Validaciones:
           │ • title >= 5 chars
           │ • description >= 10 chars
           │ • priority válida
           │
           ▼
    [Create Ticket]
           │
           │ ticket.creator = request.user
           │ ticket.status = 'open'
           │ ticket.save()
           │
           ▼
      [Database]
           │
           │ INSERT INTO tickets
           │
           ▼
    [Response 201]
           │
           │ {
           │   msg: "Ticket creado",
           │   ticket: {...}
           │ }
           │
           ▼
      [Frontend]
           │
           └→ Mostrar confirmación
```

### 2. Asignar Ticket (Support/Admin)

```
[Support] → [Frontend]
              │
              │ PATCH /api/tickets/1/assign/
              │ {assigneeId: 5}
              │
              ▼
         [API Gateway]
              │
              ▼
       [Authentication]
              │ ✓ Token válido
              │
              ▼
        [Ticket View]
              │
              │ Permission: IsSupportOrAdmin
              ▼
       [Validaciones]
              │
              │ • Usuario existe?
              │ • Es support o admin?
              │ • Está activo?
              │
              ▼
       [Update Ticket]
              │
              │ ticket.assignee_id = 5
              │ ticket.save()
              │
              ▼
         [Database]
              │
              │ UPDATE tickets SET assignee_id=5
              │
              ▼
       [Response 200]
              │
              │ {
              │   msg: "Asignado a Juan Pérez",
              │   ticket: {...}
              │ }
              │
              ▼
         [Frontend]
              │
              └→ Actualizar UI
```

---

## 💻 Tecnologías Utilizadas

### Backend
```
┌────────────────────────────────────┐
│ Django 4.2.7                       │
│  ├─ Django REST Framework 3.14.0   │
│  ├─ Simple JWT (Authentication)    │
│  ├─ django-cors-headers            │
│  └─ Pillow (Image handling)        │
└────────────────────────────────────┘
```

### Frontend
```
┌────────────────────────────────────┐
│ Android (Kotlin)                   │
│  ├─ Jetpack Compose                │
│  ├─ Retrofit 2 (HTTP client)       │
│  ├─ MVVM Architecture               │
│  └─ Navigation Component            │
└────────────────────────────────────┘
```

### Base de Datos
```
┌────────────────────────────────────┐
│ Desarrollo: SQLite                 │
│ Producción: PostgreSQL 14+         │
└────────────────────────────────────┘
```

### Autenticación
```
┌────────────────────────────────────┐
│ JWT (JSON Web Tokens)              │
│  ├─ Access Token: 1 hora           │
│  ├─ Refresh Token: 30 días         │
│  ├─ Algoritmo: HS256                │
│  └─ Blacklist al logout            │
└────────────────────────────────────┘
```

---

## 📊 Métricas de la Arquitectura

### Endpoints
- **Total:** 60+ endpoints REST
- **Con autenticación:** 55+
- **Públicos:** 5 (register, login, health, info, version)

### Modelos de Datos
- **Total:** 6 modelos principales
- **Relaciones:** 8 foreign keys
- **Tablas:** 6 tablas principales

### Serializers
- **Total:** 30+ serializers
- **Con validaciones:** 100%
- **Campos calculados:** 15+

### Permisos
- **Clases custom:** 3
- **Roles:** 3 (user, support, sysAdmin)
- **Reglas de acceso:** 50+

### Validaciones
- **Tipos:** 20+ tipos diferentes
- **Nivel serializer:** 30+
- **Nivel view:** 25+
- **Nivel modelo:** 5+

---

## 🔮 Escalabilidad y Mejoras Futuras

### Corto Plazo
- [ ] Cache con Redis
- [ ] Rate limiting avanzado
- [ ] Logs estructurados
- [ ] Notificaciones push

### Medio Plazo
- [ ] WebSockets para real-time
- [ ] Microservicios (si crece)
- [ ] CDN para archivos
- [ ] ElasticSearch para búsquedas

### Largo Plazo
- [ ] GraphQL como alternativa
- [ ] Machine Learning para asignación automática
- [ ] Multi-tenancy
- [ ] Kubernetes deployment

---

## 📝 Notas Técnicas

### Decisiones de Arquitectura

1. **¿Por qué Django REST Framework?**
   - Rápido desarrollo
   - Serializers robustos
   - Autenticación integrada
   - ORM potente

2. **¿Por qué JWT?**
   - Stateless (no necesita sesiones)
   - Escalable
   - Compatible con móviles
   - Renovación automática

3. **¿Por qué 8 apps separadas?**
   - Separación de responsabilidades
   - Mantenibilidad
   - Escalabilidad
   - Testing aislado

4. **¿Por qué camelCase en API?**
   - Compatibilidad con frontend JavaScript/Kotlin
   - Estándar en APIs REST
   - Mejor DX (Developer Experience)

---

## 👥 Equipo

- **Product Owner / API Rest:** Lautaro Cavallo
- **Backend:** Tomás Liñeiro
- **UX/UI:** Ivo Rubino
- **QA / DevOps:** Facundo Cores

---

**Versión:** 1.0  
**Última actualización:** Noviembre 2025  
**Estado:** ✅ Implementado y Documentado

