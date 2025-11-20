# 🚀 Inicio Rápido - 3 Pasos

## Paso 1: Railway (5 minutos)
1. Ve a https://railway.app
2. Crea cuenta con GitHub
3. Click en **"+ New Project"** → **"Deploy from GitHub repo"**
4. Selecciona **Ticket-Mobile**
5. Settings → Root Directory: `backend`
6. **+ New** → **Database** → **PostgreSQL**
7. Variables → Agrega:
   ```
   SECRET_KEY=tu-clave-super-secreta-123456
   DEBUG=False
   ```
8. Settings → Domains → **Generate Domain**
9. **COPIA LA URL** (ej: `https://ticket-mobile-xxxx.railway.app`)

## Paso 2: Android Studio (3 minutos)
1. Abre `ApiConfig.kt` (busca en el proyecto)
2. Cambia esta línea:
   ```kotlin
   const val BASE_URL = "https://TU-URL-DE-RAILWAY.railway.app/api/"
   ```
3. Build → Build APK(s)
4. Espera a que termine
5. Click en "locate" → Copia el APK

## Paso 3: Celular (2 minutos)
1. Pasa el APK a tu celular
2. Ábrelo
3. Permitir "Instalar apps desconocidas"
4. Instalar
5. Login: `admin@test.com` / `Admin123!`

## ✅ ¡Listo!

**Ver guía detallada:** `GUIA_DESPLIEGUE_COMPLETA.md`

