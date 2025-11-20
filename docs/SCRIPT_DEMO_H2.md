# 🎬 Script de Demo - Defensa H2

## ⏱️ Tiempo Total: 10 minutos

**Objetivo:** Demostrar todas las funcionalidades clave de TicketMobile de forma fluida y sin errores.

---

## 🎯 Preparación Previa (Antes de la Defensa)

### **Checklist de Setup:**

- [ ] **Backend corriendo** - `python manage.py runserver` en `localhost:8000`
- [ ] **App instalada** en dispositivo/emulador con conexión al backend
- [ ] **Base de datos poblada** con:
  - 2 usuarios: `admin@hospital.com` (admin) y `tecnico@hospital.com` (user)
  - 5-10 tickets de ejemplo en diferentes estados
  - 3-4 categorías (Hardware, Software, Redes, Impresoras)
- [ ] **Proyección/OBS** configurado para mostrar pantalla del emulador
- [ ] **Backup plan** - APK instalado en celular físico por si falla emulador

### **Datos de Login para Demo:**

```
Usuario Admin:
- Email: admin@hospital.com
- Password: Admin123!

Usuario Normal:
- Email: tecnico@hospital.com  
- Password: Tecnico123!
```

---

## 📝 Script Paso a Paso

### **[0:00 - 1:00] Introducción y Contexto** ⏱️ 1 min

**QUÉ DECIR:**
> "Buenos días/tardes. Vamos a demostrar TicketMobile, una aplicación móvil para gestión de tickets de soporte técnico diseñada específicamente para el Hospital Petrona V. de Cordero en San Fernando. El problema que resuelve es que actualmente el equipo de sistemas recibe todas las solicitudes por WhatsApp, lo cual genera desorganización, falta de trazabilidad y es imposible generar métricas."

**QUÉ MOSTRAR:**
- Pantalla del emulador/dispositivo listo
- (Opcional) Mostrar un screenshot de conversación de WhatsApp caótica

---

### **[1:00 - 1:30] Onboarding (Primera Instalación)** ⏱️ 30 seg

**QUÉ DECIR:**
> "Al instalar la app por primera vez, el usuario ve un onboarding de 4 pantallas que explica las funcionalidades principales."

**QUÉ HACER:**
1. **Abrir la app** (si es primera vez, se verá automáticamente)
2. **Swipear** las 4 pantallas rápidamente:
   - Bienvenida
   - Gestiona tickets
   - Colabora con equipo
   - Métricas
3. **Presionar "Comenzar"**

**RESULTADO ESPERADO:**
- Se ve el onboarding fluido con animaciones
- Al finalizar, va directo a pantalla de login

---

### **[1:30 - 2:00] Login y Autenticación JWT** ⏱️ 30 seg

**QUÉ DECIR:**
> "La app usa autenticación mediante JWT. Vamos a hacer login como técnico de sistemas."

**QUÉ HACER:**
1. **Ingresar credenciales:**
   - Email: `tecnico@hospital.com`
   - Password: `Tecnico123!`
2. **Presionar "Iniciar Sesión"**

**RESULTADO ESPERADO:**
- Login exitoso
- Redirección a lista de tickets
- Se ve el nombre del usuario en pantalla

**BACKUP SI FALLA:**
> "Tenemos un problema de conexión con el backend, voy a usar el modo demo..." (cambiar a datos mock si es necesario)

---

### **[2:00 - 3:30] Lista de Tickets (Pantalla Principal)** ⏱️ 1.5 min

**QUÉ DECIR:**
> "Esta es la pantalla principal del técnico. Puede ver todos los tickets pendientes, filtrarlos por estado y acceder rápidamente a cada uno."

**QUÉ HACER:**
1. **Mostrar lista** con 5-10 tickets
2. **Explicar elementos visuales:**
   - Título del ticket
   - Estado (chip de color)
   - Prioridad (icono)
   - Fecha de creación
3. **Cambiar tabs:**
   - Tab "Todos"
   - Tab "Pendientes" 
   - Tab "Completados"
4. **Scroll** para mostrar que hay más tickets

**PUNTOS CLAVE A DESTACAR:**
- ✅ Cards con Material Design 3
- ✅ Chips de estado con colores (Abierto=naranja, Resuelto=verde)
- ✅ Filtrado por tabs

---

### **[3:30 - 5:00] Crear Ticket con Cámara** ⏱️ 1.5 min

**QUÉ DECIR:**
> "Ahora vamos a crear un ticket nuevo. Una característica clave es que el técnico puede tomar una foto directamente desde la app para documentar el problema."

**QUÉ HACER:**
1. **Presionar FAB** (botón flotante "+")
2. **Llenar formulario:**
   - Título: "Impresora no enciende - Piso 3"
   - Descripción: "La impresora HP del consultorio 302 no responde. Cable de poder conectado."
   - Prioridad: "Alta"
   - Categoría: "Hardware"
3. **Presionar botón de cámara** 📷
4. **Tomar foto** (de cualquier cosa, simular impresora)
5. **Confirmar foto**
6. **Presionar "Crear Ticket"**

**RESULTADO ESPERADO:**
- Ticket creado exitosamente
- Vuelve a lista de tickets
- El nuevo ticket aparece en la lista
- Snackbar/Toast de confirmación

**PUNTOS CLAVE A DESTACAR:**
- ✅ CameraX integrado (no va a galería)
- ✅ Validaciones (título obligatorio)
- ✅ Categorías dinámicas

**BACKUP SI FALLA CÁMARA:**
> "La cámara también permite seleccionar de galería" (usar imagen guardada)

---

### **[5:00 - 6:00] Detalle de Ticket (Usuario Normal)** ⏱️ 1 min

**QUÉ DECIR:**
> "El usuario puede ver el detalle completo de su ticket, incluyendo el estado actual, la foto adjunta y hacer seguimiento."

**QUÉ HACER:**
1. **Click en el ticket** recién creado
2. **Mostrar pantalla de detalle:**
   - Título y descripción
   - Estado y prioridad
   - Foto adjunta (ampliar)
   - Fecha de creación
   - Creador
3. **Volver** a lista

**PUNTOS CLAVE:**
- ✅ Toda la información visible
- ✅ Fotos ampliables
- ✅ Historial de cambios (si hay)

---

### **[6:00 - 7:30] Login como Admin + Dashboard de Métricas** ⏱️ 1.5 min

**QUÉ DECIR:**
> "Ahora vamos a cambiar a vista de administrador. Los admins tienen acceso a un dashboard completo de métricas que permite visualizar el estado del sistema en tiempo real."

**QUÉ HACER:**
1. **Logout** del usuario actual (menú hamburguesa → Cerrar Sesión)
2. **Login como Admin:**
   - Email: `admin@hospital.com`
   - Password: `Admin123!`
3. **Se abre AdminHomeScreen** (diferente a usuarios normales)
4. **Presionar "Ver Estadísticas"** 
5. **Mostrar 4 tabs de métricas:**

**Tab 1 - Resumen:**
- Gráfico de dona (tickets por prioridad)
- Gráfico de barras (tickets por estado)
- Total de tickets
- Tickets sin asignar (alerta)

**Tab 2 - Rendimiento:**
- Tiempo promedio de resolución
- Tasa de resolución (%)
- Tickets creados hoy/semana/mes

**Tab 3 - Actividad de Usuarios:**
- Lista de usuarios con sus estadísticas
- Tickets creados/asignados/resueltos
- Comentarios

**Tab 4 - Salud del Sistema:**
- Estado general (Healthy/Warning/Critical)
- Usuarios activos
- Tickets urgentes/sin asignar

**PUNTOS CLAVE A DESTACAR:**
- ✅ Métricas en tiempo real
- ✅ Gráficos animados
- ✅ 4 vistas diferentes según necesidad
- ✅ Roles diferenciados (user vs admin)

---

### **[7:30 - 8:30] Asignar Ticket (Función Admin)** ⏱️ 1 min

**QUÉ DECIR:**
> "El administrador puede asignar tickets a técnicos específicos. Volviendo al home de admin, podemos ver tickets sin asignar y asignarlos."

**QUÉ HACER:**
1. **Volver** al AdminHomeScreen
2. **Seleccionar un ticket** sin asignar
3. **Presionar "Editar" o "Asignar"**
4. **Seleccionar técnico** de la lista
5. **Guardar cambios**

**RESULTADO ESPERADO:**
- Ticket actualizado
- Estado cambia a "En Progreso"
- Se ve el técnico asignado

**PUNTOS CLAVE:**
- ✅ Gestión de equipo
- ✅ Asignación manual
- ✅ Cambio de estado automático

---

### **[8:30 - 9:30] Modo Offline (Demo de Caché)** ⏱️ 1 min

**QUÉ DECIR:**
> "Una característica importante es el modo offline. Los técnicos se mueven por todo el hospital donde el WiFi puede ser irregular. La app usa Room Database para cachear los tickets localmente."

**QUÉ HACER:**
1. **Activar modo avión** en el dispositivo
2. **Volver a lista de tickets**
3. **Mostrar que los tickets** siguen visibles
4. **Click en un ticket** para ver detalle
5. **Explicar:** "Los datos se cargan desde caché local. Cuando vuelva la conexión, se sincroniza automáticamente."

**RESULTADO ESPERADO:**
- Tickets visibles sin conexión
- No hay errores de red

**PUNTOS CLAVE:**
- ✅ Room Database implementado
- ✅ Funcionalidad offline mínima
- ✅ Sincronización automática

**NOTA:** Si no da tiempo o da problemas, **saltar esta parte**

---

### **[9:30 - 10:00] Cierre y Tecnologías** ⏱️ 30 seg

**QUÉ DECIR:**
> "En resumen, TicketMobile es una solución completa que incluye:
> - **Frontend:** Android nativo en Kotlin con Jetpack Compose
> - **Backend:** Django + Django REST Framework con 60+ endpoints
> - **Autenticación:** JWT con roles diferenciados
> - **Base de datos:** Room para caché local, SQLite/PostgreSQL en backend
> - **Analytics:** Firebase Analytics para observabilidad
> - **Features clave:** Modo offline, cámara integrada, métricas en tiempo real, onboarding
> 
> Todo el código está disponible en GitHub. Muchas gracias."

**QUÉ MOSTRAR:**
- (Opcional) Pantalla de GitHub con el repo
- (Opcional) Slide final con equipo y tecnologías

---

## 🔥 Manejo de Problemas Durante Demo

### **Si el backend no responde:**
> "Tenemos un problema de conexión, voy a mostrar con datos de demostración..." (cambiar Repository a MockRepository temporalmente)

### **Si el emulador se congela:**
> "Voy a cambiar al dispositivo físico que tengo de backup..." (tener celular real con APK)

### **Si la cámara falla:**
> "La app también permite seleccionar fotos de galería" (mostrar esa opción)

### **Si hay un crash:**
> "Este es un issue conocido que estamos trackeando en GitHub. Permítanme reiniciar..." (reiniciar app rápidamente)

### **Si se acaba el tiempo:**
**Priorizar mostrar:**
1. ✅ Crear ticket con foto (feature diferenciador)
2. ✅ Métricas (dashboard admin)
3. ✅ Login y roles
4. ⚠️ Saltar: onboarding, modo offline si no da tiempo

---

## 📊 Métricas de Éxito de la Demo

Al finalizar, deberías haber mostrado:

- [x] Onboarding (primera instalación)
- [x] Login con JWT
- [x] Lista de tickets con filtros
- [x] Crear ticket con cámara
- [x] Detalle de ticket
- [x] Vista de admin diferenciada
- [x] Dashboard de métricas (4 tabs)
- [x] Asignación de tickets
- [x] (Opcional) Modo offline

**Tiempo total:** 10 minutos  
**Features demostrados:** 8-9 de 9  

---

## 🎤 Tips para el Presentador

### **Antes de Empezar:**
1. Respira profundo
2. Ten agua a mano
3. Practica al menos 2 veces antes
4. Ten el backup plan claro

### **Durante la Demo:**
- ✅ Habla claro y pausado
- ✅ Explica QUÉ haces ANTES de hacerlo
- ✅ Si algo falla, mantén la calma
- ✅ No te disculpes en exceso
- ✅ Destaca las features únicas (offline, cámara, métricas)

### **Frases para Ganar Tiempo:**
- "Como pueden ver aquí..."
- "Esto es importante porque..."
- "Una feature clave es..."
- "Comparado con otras soluciones..."

---

## ✅ Checklist Final Pre-Demo

**30 minutos antes:**
- [ ] Backend corriendo y accesible
- [ ] App instalada y funcional
- [ ] Datos de prueba poblados
- [ ] Proyección funcionando
- [ ] Backup (celular físico) listo
- [ ] Agua y notas a mano

**5 minutos antes:**
- [ ] Cerrar todas las apps
- [ ] Silenciar notificaciones
- [ ] Abrir TicketMobile
- [ ] Verificar conexión backend (probar un login rápido)
- [ ] Limpiar cache si es necesario

**¡Éxito en la defensa!** 🚀

---

**Preparado por:** Equipo TicketMobile  
**Fecha:** Noviembre 2025  
**Versión:** 1.0

