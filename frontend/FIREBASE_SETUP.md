# 🔥 Configuración de Firebase Analytics

## 📋 Pasos para Configurar Firebase

### 1. Crear Proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto"
3. Nombre del proyecto: `ticket-mobile` (o el que prefieras)
4. Acepta los términos y crea el proyecto

### 2. Agregar App Android

1. En la página del proyecto, haz clic en el ícono de Android
2. Completa los datos:
   - **Package name**: `com.uade.ticket_mobile` (IMPORTANTE: debe coincidir exactamente)
   - **App nickname**: Ticket Mobile (opcional)
   - **SHA-1**: Opcional por ahora
3. Haz clic en "Registrar app"

### 3. Descargar google-services.json

1. Descarga el archivo `google-services.json`
2. Colócalo en: `frontend/app/google-services.json`
3. **NO lo subas a Git** (ya está en .gitignore)

### 4. Verificar Dependencias

Las dependencias ya están agregadas en `app/build.gradle.kts`:

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-crashlytics-ktx")
```

### 5. Sync y Build

1. Sync Gradle: `File > Sync Project with Gradle Files`
2. Build el proyecto: `Build > Make Project`
3. Si hay errores, verifica que `google-services.json` esté en el lugar correcto

---

## 📊 Eventos Implementados

La app ya tiene tracking de los siguientes eventos:

### **Eventos de Usuario:**
- `login` - Cuando el usuario inicia sesión
- `sign_up` - Cuando se registra un nuevo usuario
- `user_role` - Rol del usuario (user property)

### **Eventos de Tickets:**
- `ticket_created` - Cuando se crea un ticket
  - Params: `priority`, `category`
- `ticket_viewed` - Cuando se ve un ticket
  - Params: `item_id`, `priority`
- `ticket_updated` - Cuando se actualiza un ticket
  - Params: `ticket_id`, `new_status`
- `ticket_deleted` - Cuando se elimina un ticket
  - Params: `ticket_id`

### **Eventos de Comentarios:**
- `comment_added` - Cuando se agrega un comentario
  - Params: `ticket_id`

### **Eventos de Navegación:**
- `screen_view` - Cada vez que se navega a una pantalla
  - Params: `screen_name`, `screen_class`

### **Eventos de Métricas:**
- `metrics_viewed` - Cuando se accede a estadísticas
  - Params: `tab_name`

### **Eventos de Onboarding:**
- `onboarding_completed` - Cuando completa el onboarding
- `onboarding_skipped` - Cuando lo salta

### **Eventos de Errores:**
- `error_occurred` - Cuando hay un error
  - Params: `error_type`, `error_message`

---

## 🔍 Ver Eventos en Firebase Console

1. Ve a Firebase Console
2. Selecciona tu proyecto
3. En el menú lateral: **Analytics > Events**
4. Verás los eventos en tiempo real (puede tardar unos minutos)

---

## 📱 Usar Analytics en el Código

### Inicializar en el ViewModel:

```kotlin
class TicketViewModel(application: Application) : AndroidViewModel(application) {
    private val analytics = AnalyticsManager(application)
    
    fun login(username: String, password: String) {
        // ... lógica de login ...
        analytics.logLogin("email")
    }
}
```

### Tracking de pantallas:

```kotlin
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val analytics = remember { AnalyticsManager(context) }
    
    LaunchedEffect(Unit) {
        analytics.logScreenView("MyScreen")
    }
}
```

---

## 🎯 Tier Gratuito de Firebase

Firebase Analytics es **100% GRATIS** e incluye:
- ✅ Eventos ilimitados
- ✅ Hasta 500 parámetros distintos por evento
- ✅ Retención de datos de 14 meses
- ✅ Dashboard en tiempo real
- ✅ Reportes de audiencia
- ✅ Reportes de comportamiento
- ✅ Integración con Google Analytics 4

**No necesitas tarjeta de crédito.**

---

## ⚠️ Importante

- El archivo `google-services.json` contiene claves de configuración
- **NO lo subas a repositorios públicos**
- Cada desarrollador debe descargar su propio archivo
- Para producción, usa diferentes proyectos Firebase para dev/staging/prod

---

## 🧪 Testing

Para verificar que funciona:

1. Corre la app en debug
2. Ve a Firebase Console > Analytics > DebugView
3. Selecciona tu dispositivo
4. Realiza acciones en la app
5. Verás los eventos aparecer en tiempo real

---

## 📚 Documentación Oficial

- [Firebase Analytics](https://firebase.google.com/docs/analytics)
- [Eventos de Analytics](https://firebase.google.com/docs/analytics/events)
- [DebugView](https://firebase.google.com/docs/analytics/debugview)

