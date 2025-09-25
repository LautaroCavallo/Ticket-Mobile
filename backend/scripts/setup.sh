#!/bin/bash

# Script de configuración inicial para el proyecto Helpdesk

echo "🚀 Configurando proyecto Helpdesk Backend..."

# Crear entorno virtual si no existe
if [ ! -d "venv" ]; then
    echo "📦 Creando entorno virtual..."
    python -m venv venv
fi

# Activar entorno virtual
echo "🔧 Activando entorno virtual..."
source venv/bin/activate

# Instalar dependencias
echo "📚 Instalando dependencias..."
pip install -r requirements.txt

# Crear archivo .env si no existe
if [ ! -f ".env" ]; then
    echo "⚙️ Creando archivo .env..."
    cp env.example .env
    echo "⚠️  Por favor edita el archivo .env con tus configuraciones"
fi

# Crear directorios necesarios
echo "📁 Creando directorios..."
mkdir -p logs
mkdir -p media
mkdir -p staticfiles

# Ejecutar migraciones
echo "🗄️ Ejecutando migraciones..."
python manage.py migrate

# Crear superusuario
echo "👤 Creando superusuario..."
echo "Por favor completa la información del superusuario:"
python manage.py createsuperuser

# Cargar datos iniciales
echo "📊 Cargando datos iniciales..."
python manage.py loaddata fixtures/initial_data.json 2>/dev/null || echo "No se encontraron fixtures iniciales"

echo "✅ ¡Configuración completada!"
echo ""
echo "Para iniciar el servidor de desarrollo:"
echo "  python manage.py runserver"
echo ""
echo "Para ver la documentación de la API:"
echo "  http://localhost:8000/api/docs/"
