# 🎫 Backend - Sistema de Gestión de Tickets

Backend desarrollado en Django para el sistema de gestión de tickets del Hospital Petrona Villegas de Cordero.

## 🚀 Inicio Rápido

### Prerrequisitos
- Python 3.9+
- PostgreSQL (para producción)
- Redis (opcional, para cache y Celery)

### Instalación

1. **Clonar el repositorio**
```bash
cd backend
```

2. **Crear entorno virtual**
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
# o
venv\Scripts\activate     # Windows
```

3. **Instalar dependencias**
```bash
pip install -r requirements.txt
```

4. **Configurar variables de entorno**
```bash
cp env.example .env
# Editar .env con tus configuraciones
```

5. **Ejecutar migraciones**
```bash
python manage.py migrate
```

6. **Crear superusuario**
```bash
python manage.py createsuperuser
```

7. **Cargar datos iniciales**
```bash
python manage.py loaddata fixtures/initial_data.json
```

8. **Ejecutar servidor de desarrollo**
```bash
python manage.py runserver
```

## 📁 Estructura del Proyecto

```
backend/
├── helpdesk/                 # Configuración principal del proyecto
│   ├── settings/            # Configuraciones por ambiente
│   │   ├── base.py         # Configuración base
│   │   ├── development.py  # Configuración de desarrollo
│   │   └── production.py   # Configuración de producción
│   ├── urls.py             # URLs principales
│   ├── wsgi.py             # WSGI application
│   └── asgi.py             # ASGI application
├── apps/                   # Aplicaciones Django
│   ├── authentication/     # Autenticación y JWT
│   ├── users/             # Gestión de usuarios
│   ├── tickets/           # Gestión de tickets
│   ├── categories/        # Categorías de tickets
│   ├── comments/          # Comentarios en tickets
│   ├── attachments/       # Archivos adjuntos
│   ├── metrics/           # Métricas y estadísticas
│   └── common/            # Utilidades comunes
├── static/                # Archivos estáticos
├── media/                 # Archivos de medios
├── logs/                  # Logs de la aplicación
├── requirements.txt       # Dependencias Python
└── manage.py             # Script de gestión Django
```

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SECRET_KEY` | Clave secreta de Django | - |
| `DEBUG` | Modo debug | `False` |
| `DATABASE_URL` | URL de la base de datos | `sqlite:///db.sqlite3` |
| `REDIS_URL` | URL de Redis | `redis://localhost:6379/0` |
| `EMAIL_HOST` | Servidor SMTP | - |
| `AWS_ACCESS_KEY_ID` | Clave de acceso AWS | - |

### Base de Datos

#### Desarrollo (SQLite)
```bash
# Usar SQLite para desarrollo local
DATABASE_URL=sqlite:///db.sqlite3
```

#### Producción (PostgreSQL)
```bash
DATABASE_URL=postgres://user:password@localhost:5432/helpdesk_db
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
python manage.py test

# Tests específicos
python manage.py test apps.tickets

# Con cobertura
pytest --cov=apps --cov-report=html
```

### Tests de Performance
```bash
python manage.py test apps.tickets.tests.performance
```

## 📚 API Documentation

### Swagger UI
- **Desarrollo**: http://localhost:8000/api/docs/
- **Producción**: https://api.helpdesk.com/api/docs/

### ReDoc
- **Desarrollo**: http://localhost:8000/api/redoc/
- **Producción**: https://api.helpdesk.com/api/redoc/

## 🔐 Autenticación

El sistema utiliza JWT (JSON Web Tokens) para la autenticación:

```bash
# Login
POST /api/auth/login/
{
    "email": "user@example.com",
    "password": "password123"
}

# Response
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "user": {...}
}
```

### Uso del Token
```bash
# Incluir en headers
Authorization: Bearer <access_token>
```

## 🚀 Deployment

### Docker
```bash
# Construir imagen
docker build -t helpdesk-backend .

# Ejecutar contenedor
docker run -p 8000:8000 helpdesk-backend
```

### Heroku
```bash
# Instalar Heroku CLI
# Configurar variables de entorno
heroku config:set SECRET_KEY=your-secret-key
heroku config:set DATABASE_URL=postgres://...

# Deploy
git push heroku main
```

## 📊 Monitoreo

### Logs
Los logs se guardan en `logs/django.log` y incluyen:
- Requests HTTP
- Errores de la aplicación
- Actividad de usuarios
- Performance de queries

### Métricas
- **Health Check**: `/api/health/`
- **Métricas de Tickets**: `/api/metrics/tickets/`
- **Métricas de Usuarios**: `/api/metrics/users/`

## 🤝 Contribución

### Flujo de Trabajo
1. Crear rama desde `main`
2. Implementar cambios
3. Ejecutar tests
4. Crear Pull Request

### Estándares de Código
- PEP 8 para Python
- Docstrings en todas las funciones
- Tests para nuevas funcionalidades
- Type hints donde sea posible

## 📞 Soporte

Para soporte técnico:
- **Email**: tomas.lineiro@helpdesk.com
- **Issues**: GitHub Issues
- **Documentación**: [Wiki del proyecto](https://github.com/helpdesk/wiki)

## 📄 Licencia

Este proyecto es parte del trabajo académico para UADE (2025).
