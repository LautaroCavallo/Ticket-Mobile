@echo off
REM Script de configuración inicial para el proyecto Helpdesk (Windows)

echo 🚀 Configurando proyecto Helpdesk Backend...

REM Crear entorno virtual si no existe
if not exist "venv" (
    echo 📦 Creando entorno virtual...
    python -m venv venv
)

REM Activar entorno virtual
echo 🔧 Activando entorno virtual...
call venv\Scripts\activate.bat

REM Instalar dependencias
echo 📚 Instalando dependencias...
pip install -r requirements.txt

REM Crear archivo .env si no existe
if not exist ".env" (
    echo ⚙️ Creando archivo .env...
    copy env.example .env
    echo ⚠️  Por favor edita el archivo .env con tus configuraciones
)

REM Crear directorios necesarios
echo 📁 Creando directorios...
if not exist "logs" mkdir logs
if not exist "media" mkdir media
if not exist "staticfiles" mkdir staticfiles

REM Ejecutar migraciones
echo 🗄️ Ejecutando migraciones...
python manage.py migrate

REM Crear superusuario
echo 👤 Creando superusuario...
echo Por favor completa la información del superusuario:
python manage.py createsuperuser

REM Cargar datos iniciales
echo 📊 Cargando datos iniciales...
python manage.py loaddata fixtures\initial_data.json 2>nul || echo No se encontraron fixtures iniciales

echo ✅ ¡Configuración completada!
echo.
echo Para iniciar el servidor de desarrollo:
echo   python manage.py runserver
echo.
echo Para ver la documentación de la API:
echo   http://localhost:8000/api/docs/

pause
