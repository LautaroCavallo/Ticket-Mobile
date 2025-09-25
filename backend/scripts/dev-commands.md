# 🛠️ Comandos de Desarrollo - Helpdesk Backend

## 🚀 Comandos Básicos

### Configuración Inicial
```bash
# Windows
scripts\setup.bat

# Linux/Mac
chmod +x scripts/setup.sh
./scripts/setup.sh
```

### Servidor de Desarrollo
```bash
# Activar entorno virtual
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# Ejecutar servidor
python manage.py runserver

# Con puerto específico
python manage.py runserver 8001
```

## 🗄️ Base de Datos

### Migraciones
```bash
# Crear migraciones
python manage.py makemigrations

# Aplicar migraciones
python manage.py migrate

# Ver estado de migraciones
python manage.py showmigrations

# Revertir migración
python manage.py migrate app_name 0001
```

### Shell de Django
```bash
# Shell interactivo
python manage.py shell

# Shell con IPython (si está instalado)
python manage.py shell_plus
```

### Datos de Prueba
```bash
# Crear superusuario
python manage.py createsuperuser

# Cargar datos iniciales
python manage.py loaddata fixtures/initial_data.json

# Exportar datos
python manage.py dumpdata --indent 2 > fixtures/backup.json
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
python manage.py test

# App específica
python manage.py test apps.tickets

# Test específico
python manage.py test apps.tickets.tests.test_models

# Con verbosidad
python manage.py test --verbosity=2

# Con coverage
pytest --cov=apps --cov-report=html
```

### Tests de Performance
```bash
# Tests de performance
python manage.py test apps.tickets.tests.performance

# Con profiling
python manage.py test --debug-mode
```

## 📊 Desarrollo y Debug

### Debug Toolbar
```bash
# Instalar debug toolbar
pip install django-debug-toolbar

# Ya está configurado en development.py
# Acceder a: http://localhost:8000/__debug__/
```

### Logs
```bash
# Ver logs en tiempo real
tail -f logs/django.log

# Windows
type logs\django.log
```

### Validación
```bash
# Verificar configuración
python manage.py check

# Verificar con deploy
python manage.py check --deploy

# Validar traducciones
python manage.py makemessages -l es
python manage.py compilemessages
```

## 📚 Documentación API

### Swagger/OpenAPI
```bash
# Generar schema
python manage.py spectacular --file schema.yml

# Servir documentación
# http://localhost:8000/api/docs/
# http://localhost:8000/api/redoc/
```

## 🔧 Utilidades

### Limpieza
```bash
# Limpiar cache
python manage.py clear_cache

# Limpiar archivos estáticos
python manage.py collectstatic --clear

# Limpiar migraciones (¡CUIDADO!)
find . -path "*/migrations/*.py" -not -name "__init__.py" -delete
find . -path "*/migrations/*.pyc" -delete
```

### Backup
```bash
# Backup de base de datos
python manage.py dumpdata > backup_$(date +%Y%m%d).json

# Backup específico
python manage.py dumpdata apps.users > users_backup.json
```

## 🐳 Docker

### Desarrollo con Docker
```bash
# Construir y ejecutar
docker-compose up --build

# Solo base de datos
docker-compose up db redis

# Ejecutar comandos en contenedor
docker-compose exec web python manage.py migrate
docker-compose exec web python manage.py createsuperuser
```

### Producción
```bash
# Construir imagen
docker build -t helpdesk-backend .

# Ejecutar contenedor
docker run -p 8000:8000 helpdesk-backend
```

## 🔍 Debugging

### Debugging Avanzado
```bash
# Con pdb
# Agregar breakpoint en código: import pdb; pdb.set_trace()

# Con ipdb (mejor experiencia)
pip install ipdb
# Agregar breakpoint: import ipdb; ipdb.set_trace()

# Con Django Debug Toolbar
# Ya configurado en development.py
```

### Profiling
```bash
# Instalar django-extensions
pip install django-extensions

# Profiling de views
python manage.py runserver --profiler

# Memory profiling
pip install memory-profiler
```

## 📦 Dependencias

### Gestión de Paquetes
```bash
# Actualizar requirements
pip freeze > requirements.txt

# Instalar desde requirements
pip install -r requirements.txt

# Actualizar paquetes
pip install --upgrade package_name

# Verificar dependencias
pip check
```

## 🚀 Deployment

### Preparación para Producción
```bash
# Configurar settings de producción
export DJANGO_SETTINGS_MODULE=helpdesk.settings.production

# Recopilar archivos estáticos
python manage.py collectstatic --noinput

# Verificar configuración
python manage.py check --deploy
```

### Variables de Entorno
```bash
# Cargar variables desde archivo
export $(cat .env | xargs)

# Verificar variables
echo $DATABASE_URL
echo $SECRET_KEY
```

## 🎯 Comandos Personalizados

### Crear Comando Personalizado
```bash
# Crear comando
python manage.py startapp myapp
# Crear archivo: myapp/management/commands/mycommand.py
```

### Ejemplo de Comando
```python
# myapp/management/commands/seed_data.py
from django.core.management.base import BaseCommand
from apps.categories.models import Categoria

class Command(BaseCommand):
    help = 'Cargar datos iniciales'

    def handle(self, *args, **options):
        Categoria.objects.get_or_create(
            nombre='Hardware',
            defaults={'descripcion': 'Problemas de hardware'}
        )
        self.stdout.write('Datos cargados exitosamente')
```

## 📋 Checklist de Desarrollo

### Antes de Commit
- [ ] Tests pasando: `python manage.py test`
- [ ] No errores de lint: `flake8 .`
- [ ] Migraciones aplicadas: `python manage.py migrate`
- [ ] Documentación actualizada
- [ ] Variables de entorno configuradas

### Antes de Deploy
- [ ] Tests de producción: `python manage.py check --deploy`
- [ ] Archivos estáticos recopilados: `python manage.py collectstatic`
- [ ] Backup de base de datos
- [ ] Variables de entorno de producción configuradas
- [ ] Logs configurados
