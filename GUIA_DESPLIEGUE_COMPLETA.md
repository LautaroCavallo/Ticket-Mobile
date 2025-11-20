# 📱 Guía Completa: Deploy del Backend + APK Android

Esta guía te ayudará a:
1. ✅ Subir el backend a Railway (gratis, 5 minutos)
2. ✅ Generar el APK de Android
3. ✅ Instalar y usar la app

---

## 🚀 PARTE 1: Subir el Backend a Railway (Gratis)

### Paso 1: Crear cuenta en Railway

1. Abre tu navegador y ve a: **https://railway.app**
2. Haz clic en **"Start a New Project"** o **"Sign up"**
3. Inicia sesión con tu cuenta de **GitHub** (es la opción más fácil)
4. Confirma tu email si te lo pide

### Paso 2: Crear un nuevo proyecto

1. En Railway, haz clic en **"+ New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Si es tu primera vez:
   - Haz clic en **"Configure GitHub App"**
   - Selecciona **"All repositories"** o solo este repositorio
   - Autoriza Railway
4. Selecciona tu repositorio **"Ticket-Mobile"**
5. Railway detectará que es un proyecto Python

### Paso 3: Configurar el proyecto

1. Una vez creado el proyecto, haz clic en el servicio (aparecerá algo como "ticket-mobile")
2. Ve a la pestaña **"Settings"**
3. Busca **"Root Directory"** y cámbialo a: `backend`
4. Haz clic en **"Save changes"**

### Paso 4: Agregar Base de Datos PostgreSQL

1. En el dashboard de Railway, haz clic en **"+ New"**
2. Selecciona **"Database"**
3. Selecciona **"Add PostgreSQL"**
4. Espera unos segundos a que se cree
5. Railway conectará automáticamente la base de datos con tu backend

### Paso 5: Configurar Variables de Entorno

1. Haz clic en tu servicio backend
2. Ve a la pestaña **"Variables"**
3. Agrega las siguientes variables (haz clic en **"+ New Variable"** para cada una):

```
SECRET_KEY=tu-clave-super-secreta-y-larga-cambiarla-en-produccion-123456
DEBUG=False
ALLOWED_HOSTS=.railway.app,localhost,127.0.0.1
```

4. Haz clic en **"Add"** o **"Save"**

### Paso 6: Desplegar

1. Railway empezará a desplegar automáticamente
2. Ve a la pestaña **"Deployments"** para ver el progreso
3. Espera 2-3 minutos (verás logs en tiempo real)
4. Cuando diga **"Success"** o **"Deployed"**, está listo ✅

### Paso 7: Obtener la URL de tu Backend

1. Ve a la pestaña **"Settings"**
2. Busca **"Domains"**
3. Haz clic en **"Generate Domain"**
4. Railway te dará una URL como: `https://ticket-mobile-production-xxxx.up.railway.app`
5. **⚠️ GUARDA ESTA URL**, la necesitarás más adelante

### Paso 8: Inicializar Datos (Crear usuarios y tickets)

1. En la pestaña de tu servicio, haz clic en **"Settings"**
2. Desplázate hasta encontrar **"Service"** → **"Command"**
3. Abre una terminal en tu computadora y ejecuta:

```bash
# En Windows PowerShell:
cd D:\UADE\DA\Ticket-Mobile\backend
railway login
railway link
python init_railway_data.py
```

**O ALTERNATIVA (más fácil):**

1. Ve a la pestaña **"Deployments"**
2. Haz clic en el deployment activo
3. Busca la opción **"View Logs"**
4. En otra terminal, desde la carpeta `backend`:

```bash
railway run python init_railway_data.py
```

**CREDENCIALES CREADAS:**
- Admin: `admin@test.com` / `Admin123!`
- Soporte: `maria.garcia@support.com` / `Soporte123!`
- Usuario: `usuario@test.com` / `Usuario123!`

---

## 📱 PARTE 2: Generar el APK de Android

### Paso 1: Configurar la URL del Backend en el Código

1. Abre el proyecto en **Android Studio**
2. Navega a: `frontend/app/src/main/java/com/uade/ticket_mobile/data/api/ApiConfig.kt`
3. Busca la línea que dice:
   ```kotlin
   const val BASE_URL = "http://10.0.2.2:8000/api/" // ← CAMBIAR AQUÍ
   ```
4. **REEMPLÁZALA** con la URL de Railway que obtuviste:
   ```kotlin
   const val BASE_URL = "https://ticket-mobile-production-xxxx.up.railway.app/api/"
   ```
   (Reemplaza `ticket-mobile-production-xxxx` con tu URL real)
5. Guarda el archivo (Ctrl + S)

### Paso 2: Generar el APK

#### Opción A: APK de Debug (más rápido, para pruebas)

1. En Android Studio, abre el menú **Build**
2. Selecciona **Build Bundle(s) / APK(s)**
3. Haz clic en **Build APK(s)**
4. Espera 2-5 minutos (verás el progreso abajo)
5. Cuando termine, aparecerá un mensaje **"APK(s) generated successfully"**
6. Haz clic en **"locate"** para ver el APK
7. El APK estará en: `frontend/app/build/outputs/apk/debug/app-debug.apk`

#### Opción B: APK Firmado (para distribución)

1. En Android Studio, ve a **Build** → **Generate Signed Bundle / APK**
2. Selecciona **APK** y haz clic en **Next**
3. Si no tienes una key:
   - Haz clic en **Create new...**
   - Key store path: Elige dónde guardar (ej: `mi-app-key.jks`)
   - Password: Crea una contraseña **⚠️ GUÁRDALA**
   - Alias: Escribe `ticket-mobile`
   - Validity: 25 años
   - Certificate: Completa tus datos
   - Haz clic en **OK**
4. Selecciona **release** como Build Type
5. Marca **V1 (Jar Signature)** y **V2 (Full APK Signature)**
6. Haz clic en **Finish**
7. Espera 3-5 minutos
8. El APK estará en: `frontend/app/release/app-release.apk`

---

## 📲 PARTE 3: Instalar el APK en tu Celular

### Opción 1: Instalación Directa (Cable USB)

1. Conecta tu celular con un cable USB a la computadora
2. En tu celular:
   - Ve a **Ajustes** → **Acerca del teléfono**
   - Toca **7 veces** en "Número de compilación" (activa modo desarrollador)
   - Vuelve a Ajustes → **Opciones de desarrollador**
   - Activa **Depuración USB**
   - Acepta en tu celular cuando te pregunte
3. Copia el APK a tu celular (puedes arrastrarlo a la carpeta "Descargas")
4. En tu celular, abre **Archivos** o **Administrador de archivos**
5. Busca el APK en "Descargas"
6. Toca el APK
7. Si te pide, permite **"Instalar aplicaciones desconocidas"**
8. Toca **Instalar**
9. Espera unos segundos
10. Toca **Abrir**

### Opción 2: Compartir por WhatsApp/Email

1. Sube el APK a Google Drive o Dropbox
2. Comparte el enlace por WhatsApp o Email
3. Abre el enlace en tu celular
4. Descarga el APK
5. Abre el APK desde las notificaciones
6. Permite **"Instalar aplicaciones desconocidas"** si te lo pide
7. Toca **Instalar**

### Opción 3: Código QR (Requiere herramienta extra)

1. Sube el APK a un servicio como https://www.file.io/
2. Genera un código QR con el enlace
3. Escanea el QR desde tu celular
4. Descarga e instala

---

## ✅ PARTE 4: Usar la Aplicación

### Primera vez:

1. Abre la app **Ticket Mobile**
2. Verás la pantalla de Login
3. Ingresa credenciales:
   - **Email**: `admin@test.com`
   - **Contraseña**: `Admin123!`
4. Toca **Iniciar Sesión**
5. ¡Listo! Deberías ver el listado de tickets

### Usuarios disponibles:

| Email | Contraseña | Rol |
|-------|------------|-----|
| admin@test.com | Admin123! | Administrador |
| maria.garcia@support.com | Soporte123! | Soporte |
| carlos.lopez@support.com | Soporte123! | Soporte |
| usuario@test.com | Usuario123! | Usuario Normal |

---

## 🆘 Solución de Problemas

### ❌ El backend no despliega en Railway

**Solución:**
1. Ve a **Deployments** → **View Logs**
2. Lee el error
3. Verifica que la carpeta root sea `backend`
4. Asegúrate de que PostgreSQL esté conectado

### ❌ El APK no se instala

**Solución:**
1. Ve a Ajustes → Seguridad
2. Activa **"Orígenes desconocidos"** o **"Instalar apps desconocidas"**
3. Intenta de nuevo

### ❌ La app dice "Error de conexión"

**Solución:**
1. Verifica que el backend esté funcionando: Abre en el navegador:
   ```
   https://tu-dominio.railway.app/api/health/
   ```
   Debe mostrar: `{"status": "healthy"}`
2. Verifica que hayas cambiado la URL en `ApiConfig.kt`
3. Recompila el APK con la URL correcta

### ❌ "No hay usuarios" en la pantalla de gestión

**Solución:**
1. Ejecuta: `railway run python init_railway_data.py`
2. Verifica que haya datos en la base de datos

---

## 🎉 ¡Felicidades!

Ahora tienes:
- ✅ Backend funcionando en la nube (Railway)
- ✅ APK instalado en tu celular
- ✅ App completa funcionando

**Comparte el APK** con quien quieras que pruebe tu app.

---

## 📝 Comandos Útiles

### Ver logs del backend:
```bash
railway logs
```

### Reiniciar el backend:
```bash
railway restart
```

### Ejecutar migraciones:
```bash
railway run python manage.py migrate
```

### Crear nuevo superusuario:
```bash
railway run python manage.py createsuperuser
```

---

**¿Necesitas ayuda?** Revisa los logs en Railway o en Android Studio (Logcat).

