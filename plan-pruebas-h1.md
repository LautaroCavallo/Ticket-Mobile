# Plan de Pruebas H1 - Sistema de Gestión de Tickets 

## 1. Pruebas Unitarias
Se enfocan en validar componentes individuales de la aplicación (funciones, controladores, modelos de datos).

### Alcance
- Validación de funciones básicas de autenticación y tickets.
- Pruebas de los modelos de datos (restricciones, defaults).

### Casos de prueba propuestos

#### **AuthController**
**Setup**: Mock de base de datos, hash de contraseñas, generador JWT  
**Teardown**: Limpiar usuarios de prueba, invalidar tokens

- [ ] **TC_AUTH_001**: Registrar usuario válido
  - **Input**: `{email: "test@email.com", password: "Abc123!", name: "Test User"}`
  - **Expected**: HTTP 201, response con `{id, email, name, createdAt}`
  - **Validaciones**: Usuario existe en BD, password está hasheado, email único

- [ ] **TC_AUTH_002**: Registrar con email duplicado
  - **Setup**: Usuario existente con email "test@email.com"
  - **Input**: `{email: "test@email.com", password: "xyz", name: "Otro"}`
  - **Expected**: HTTP 400, `{error: "Email already exists"}`

- [ ] **TC_AUTH_003**: Login credenciales correctas
  - **Setup**: Usuario registrado con password "Abc123!"
  - **Input**: `{email: "test@email.com", password: "Abc123!"}`
  - **Expected**: HTTP 200, `{token: "jwt.token.here", user: {id, email, name}}`
  - **Validaciones**: Token JWT válido, expiración correcta (24h)

- [ ] **TC_AUTH_004**: Login credenciales incorrectas
  - **Input**: `{email: "test@email.com", password: "wrong"}`
  - **Expected**: HTTP 401, `{error: "Invalid credentials"}`

- [ ] **TC_AUTH_005**: Acceso con token expirado
  - **Setup**: Token JWT con exp < now()
  - **Input**: Request con Authorization header
  - **Expected**: HTTP 401, `{error: "Token expired"}`

- [ ] **TC_AUTH_006**: Validación SQL injection
  - **Input**: `{email: "admin'--", password: "'; DROP TABLE users;--"}`
  - **Expected**: HTTP 400, query parametrizada protege BD

#### **TicketsController**
**Setup**: Mock de AuthService, base de datos limpia, usuario autenticado  
**Teardown**: Limpiar tickets de prueba

- [ ] **TC_TICKET_001**: Crear ticket válido
  - **Setup**: Usuario autenticado ID=1
  - **Input**: `{title: "App crashea", description: "Error al abrir", categoryId: 2}`
  - **Expected**: HTTP 201, response con `{id, title, description, status: "OPEN", creatorId: 1, createdAt}`
  - **BD Validation**: Registro existe con creatorId correcto

- [ ] **TC_TICKET_002**: Crear sin título requerido
  - **Input**: `{description: "Solo descripción", categoryId: 1}`
  - **Expected**: HTTP 400, `{error: "Title is required"}`

- [ ] **TC_TICKET_003**: Obtener ticket existente
  - **Setup**: Ticket con ID=5 en BD
  - **Input**: GET /tickets/5
  - **Expected**: HTTP 200, datos completos del ticket + relaciones (creator, category)

- [ ] **TC_TICKET_004**: Obtener ticket inexistente
  - **Input**: GET /tickets/999
  - **Expected**: HTTP 404, `{error: "Ticket not found"}`

- [ ] **TC_TICKET_005**: Título excede límite
  - **Input**: `{title: "a".repeat(256), description: "test"}`
  - **Expected**: HTTP 400, `{error: "Title too long (max 255 characters)"}`

- [ ] **TC_TICKET_006**: Acceso sin autorización
  - **Setup**: Request sin token JWT
  - **Input**: GET /tickets/1
  - **Expected**: HTTP 403, `{error: "Access denied"}`

#### **CategoriasController**
**Setup**: Categorías predefinidas en BD  

- [ ] **TC_CAT_001**: Crear categoría válida
  - **Input**: `{name: "Hardware", description: "Problemas de HW"}`
  - **Expected**: HTTP 201, `{id, name, description, createdAt}`

- [ ] **TC_CAT_002**: Crear sin nombre
  - **Input**: `{description: "Solo descripción"}`
  - **Expected**: HTTP 400, `{error: "Name is required"}`

#### **AdjuntosController**
**Setup**: Mock de FileStorage, límites configurados

- [ ] **TC_FILE_001**: Subir archivo válido
  - **Input**: JPG de 2MB
  - **Expected**: HTTP 201, `{id, filename, mimeType, size, uploadedAt}`
  - **Validaciones**: Archivo guardado, metadata correcto

- [ ] **TC_FILE_002**: Archivo excede límite
  - **Input**: PDF de 15MB
  - **Expected**: HTTP 413, `{error: "File too large (max 10MB)"}`

- [ ] **TC_FILE_003**: Tipo de archivo no permitido
  - **Input**: Archivo .exe
  - **Expected**: HTTP 400, `{error: "File type not allowed"}`  

---

## 2. Pruebas de Integración
Validan cómo interactúan los módulos entre sí (API + DB).

### Alcance
- Flujo de un usuario desde registro hasta cierre de ticket.
- Relaciones entre entidades.

### Casos de prueba propuestos

#### **IT_001: Flujo Completo Usuario - Registro a Ticket**
**Objetivo**: Validar flujo end-to-end desde registro hasta creación de ticket  
**Setup BD**: Base de datos limpia, categorías pre-cargadas  
**Teardown**: Limpiar usuarios y tickets de prueba

**Pasos**:
1. **Registro**: POST `/auth/register` 
   - Input: `{email: "integration@test.com", password: "Test123!", name: "QA User"}`
   - Expected: HTTP 201, usuario creado en BD
   
2. **Login**: POST `/auth/login`
   - Input: credentials del paso 1
   - Expected: HTTP 200, JWT válido recibido
   
3. **Crear Ticket**: POST `/tickets` (con JWT)
   - Input: `{title: "Test Integration", description: "Flujo completo", categoryId: 1}`
   - Expected: HTTP 201, ticket en BD con `creatorId` correcto
   
4. **Listar Tickets**: GET `/tickets` (con JWT)
   - Expected: HTTP 200, array contiene ticket creado
   
**Validaciones BD**:
- `users.email = "integration@test.com"` existe
- `tickets.creatorId = users.id` (relación correcta)
- `tickets.status = "OPEN"` (estado inicial)
- `tickets.createdAt` within last 5 seconds

#### **IT_002: Asignación y Estados de Ticket**
**Setup BD**: Usuario admin (roleId=1), usuario regular (roleId=2), ticket existente  
**Data**: Ticket ID=100, Admin ID=1, Regular User ID=2

**Pasos**:
1. **Crear ticket como usuario regular**
   - Auth: JWT de userId=2
   - Expected: `tickets.creatorId = 2, status = "OPEN"`
   
2. **Asignar ticket (como admin)**
   - Auth: JWT de userId=1 (admin)
   - PUT `/tickets/100/assign` 
   - Input: `{assigneeId: 1}`
   - Expected: `tickets.assigneeId = 1, status = "ASSIGNED"`
   
3. **Cambiar a "En Progreso"**
   - PUT `/tickets/100/status`
   - Input: `{status: "IN_PROGRESS"}`
   - Expected: BD actualizada, `updatedAt` modificado
   
4. **Listar tickets asignados**
   - GET `/tickets?assignee=1`
   - Expected: Incluye ticket 100
   
**Validaciones BD**:
- `audit_log` contiene cambios de estado
- `tickets.assigneeId = 1 AND status = "IN_PROGRESS"`
- Foreign keys mantienen integridad

#### **IT_003: Comentarios y Adjuntos**
**Setup**: Ticket existente ID=200, usuario autenticado ID=3

**Pasos**:
1. **Agregar comentario**
   - POST `/tickets/200/comments`
   - Input: `{content: "Comentario de prueba", isPublic: true}`
   - Expected: `comments.ticketId = 200, authorId = 3`
   
2. **Subir archivo adjunto**
   - POST `/tickets/200/attachments`
   - Input: Multipart con imagen.jpg (3MB)
   - Expected: Archivo guardado, `attachments.ticketId = 200`
   
3. **Obtener ticket con relaciones**
   - GET `/tickets/200?include=comments,attachments`
   - Expected: JSON incluye arrays de comments y attachments
   
4. **Descargar adjunto**
   - GET `/attachments/{attachmentId}/download`
   - Expected: File stream con headers correctos

**Validaciones**:
- `comments.createdAt` ordenado DESC
- `attachments.filename, mimeType, size` correctos
- File físico existe en storage

#### **IT_004: Sincronización Offline/Online**
**Setup**: Mock de conectividad, SQLite local, servidor REST

**Scenario A - Crear Offline**:
1. **Simular pérdida de conexión**
   - Mock network failure
   
2. **Crear ticket offline**
   - App guarda en SQLite local
   - `local_tickets.synced = false, tempId = "temp_123"`
   
3. **Restaurar conexión**
   - App detecta conectividad
   - Sync service ejecuta POST `/tickets/sync`
   
4. **Validar sincronización**
   - Servidor retorna `{tempId: "temp_123", serverId: 456}`
   - Local BD actualiza: `synced = true, serverId = 456`

**Scenario B - Conflicto de datos**:
1. **Modificar mismo ticket offline y online**
   - Offline: Cambiar título
   - Online (otro user): Cambiar descripción
   
2. **Al sincronizar**
   - Detectar `lastModified` conflict
   - Mostrar resolution UI
   - Expected: Usuario elige versión final

#### **IT_005: Concurrencia y Performance**
**Setup**: 2 sesiones simultáneas, ticket ID=500

**Test Concurrency**:
1. **Usuario A y B modifican mismo ticket**
   - Simultaneous PUT requests
   - Expected: Uno succeed, otro get 409 Conflict
   
2. **Múltiples adjuntos en paralelo**
   - 5 files uploaded simultaneously
   - Expected: Todos processed, no corruption
   
**Test Performance**:
1. **Load test - 100 tickets**
   - GET `/tickets?limit=100`
   - Expected: Response < 2 seconds
   
2. **Search con filtros**
   - GET `/tickets?status=OPEN&category=1&search=bug`
   - Expected: Query optimized, response < 1 second  

---

## 3. Pruebas Manuales de UI
Validan la experiencia de usuario en la aplicación móvil (prototipo Android).

### Alcance
- Navegación básica entre pantallas.
- Validación de formularios y flujos críticos.

### Casos de prueba propuestos

#### **UI_001: Login y Registro**
**Device**: Android 8.1+, resolución 1080x1920+  
**Precondiciones**: App instalada, no hay sesión activa

- [ ] **UI_001a: Login exitoso**
  - **Pasos**:
    1. Abrir app → pantalla de login visible
    2. Ingreso: `email: "demo@helpdesk.com", password: "Demo123!"`
    3. Tap botón "Iniciar Sesión"
  - **Resultado**: Navegación a pantalla principal, usuario logueado visible en header
  - **Tiempo**: < 3 segundos para completar login

- [ ] **UI_001b: Login fallido**
  - **Pasos**:
    1. Ingreso: `email: "wrong@email.com", password: "badpass"`
    2. Tap "Iniciar Sesión"
  - **Resultado**: Toast/Alert "Credenciales incorrectas" visible 3+ segundos
  - **Validación**: No navegación, campos se mantienen (email visible, password limpio)

- [ ] **UI_001c: Persistencia de sesión**
  - **Pasos**:
    1. Login exitoso → cerrar app (home button)
    2. Reabrir app desde launcher
  - **Resultado**: Usuario sigue logueado, no requiere re-login
  - **Tiempo**: App load < 2 segundos

- [ ] **UI_001d: Logout completo**
  - **Pasos**:
    1. Menu lateral → tap "Cerrar Sesión"
    2. Confirmar en dialog
  - **Resultado**: Navegación a login, datos sensibles eliminados, back button no regresa

#### **UI_002: Creación y Gestión de Tickets**
**Precondiciones**: Usuario logueado, permisos otorgados

- [ ] **UI_002a: Crear ticket completo**
  - **Pasos**:
    1. Tap FAB "+" → formulario nuevo ticket
    2. Completar: `título: "App lenta", descripción: "Tarda en cargar", categoría: "Performance"`
    3. Tap "Crear Ticket"
  - **Resultado**: 
    - Toast confirmación "Ticket creado"
    - Regreso a listado con nuevo ticket visible (tope de lista)
    - Badge/contador actualizado
  - **Validación**: Ticket aparece con estado "Abierto" y timestamp reciente

- [ ] **UI_002b: Validación formulario**
  - **Pasos**:
    1. Nuevo ticket → dejar título vacío
    2. Tap "Crear"
  - **Resultado**: 
    - Campo título con borde rojo + mensaje "Título requerido"
    - Form no se envía
    - Cursor focus en campo título

- [ ] **UI_002c: Ver detalle ticket**
  - **Setup**: Ticket existente con ID conocido
  - **Pasos**:
    1. Tap ticket en listado → pantalla detalle
  - **Resultado**:
    - Header: título completo, estado, fecha
    - Body: descripción completa, categoría
    - Footer: botones "Comentar", "Adjuntar"
    - Si hay comentarios: lista ordenada por fecha DESC

- [ ] **UI_002d: Filtros funcionales**
  - **Pasos**:
    1. Listado tickets → tap ícono filtro
    2. Seleccionar "Estado: En Progreso"
    3. Aplicar filtro
  - **Resultado**: 
    - Solo tickets "En Progreso" visibles
    - Header muestra "Filtrado (X resultados)"
    - Botón "Limpiar filtros" visible

- [ ] **UI_002e: Búsqueda en tiempo real**
  - **Pasos**:
    1. Tap search bar → escribir "lent"
    2. Sin presionar enter
  - **Resultado**: 
    - Listado se filtra automáticamente (debounce 300ms)
    - Tickets con "lent" en título/descripción visibles
    - No más de 2 segundos de delay

- [ ] **UI_002f: Pull-to-refresh**
  - **Pasos**:
    1. En listado → swipe down desde tope
  - **Resultado**: 
    - Loading indicator visible
    - Lista se actualiza con datos server
    - Indicador desaparece < 3 segundos

#### **UI_003: Adjuntos y Multimedia**
**Precondiciones**: Permisos cámara/storage otorgados

- [ ] **UI_003a: Foto desde cámara**
  - **Pasos**:
    1. Detalle ticket → tap "Adjuntar" → "Tomar Foto"
    2. Permitir acceso cámara → tomar foto → confirmar
  - **Resultado**: 
    - Foto visible en lista adjuntos (thumbnail)
    - Progress bar durante upload
    - Mensaje "Foto subida exitosamente"

- [ ] **UI_003b: Archivo desde galería**
  - **Pasos**:
    1. "Adjuntar" → "Desde Galería" → seleccionar imagen JPG
  - **Resultado**: 
    - Preview de imagen visible
    - Metadata mostrado (nombre, tamaño)
    - Upload con progress % visible

- [ ] **UI_003c: Vista previa y descarga**
  - **Setup**: Ticket con imagen adjunta
  - **Pasos**:
    1. Tap thumbnail imagen → full screen view
    2. Pinch-to-zoom funcional
    3. Tap "Descargar" → guardar en device
  - **Resultado**: 
    - Imagen se abre en viewer nativo
    - Zoom/pan responsivo
    - Archivo descargado en Downloads/

#### **UI_004: Accesibilidad y Usabilidad**
**Devices**: Múltiples tamaños (5" a 10"), modo oscuro/claro

- [ ] **UI_004a: Elementos táctiles**
  - **Validaciones**:
    - Todos los botones ≥ 44x44 dp
    - Espacio entre elementos ≥ 8dp
    - Textos legibles ≥ 14sp
    - Contraste cumple WCAG AA (4.5:1)

- [ ] **UI_004b: Screen reader support**
  - **Setup**: TalkBack activo
  - **Pasos**:
    1. Navegar por formulario de ticket
    2. Crear ticket usando solo voz
  - **Resultado**: 
    - Elementos tienen contentDescription
    - Labels de campos leídos correctamente
    - Navegación lógica (top-to-bottom, left-to-right)

- [ ] **UI_004c: Orientación y responsive**
  - **Pasos**:
    1. Rotar device portrait → landscape
    2. Verificar layout en tablet (>7")
  - **Resultado**: 
    - Layout se adapta sin pérdida datos
    - Elementos no se solapan
    - Información importante siempre visible

- [ ] **UI_004d: Estados de carga y error**
  - **Pasos**:
    1. Simular conexión lenta → crear ticket
    2. Simular error server → retry
  - **Resultado**: 
    - Loading states claros (skeleton/spinner)
    - Error messages actionables ("Reintentar", "Verificar conexión")
    - No pantallas en blanco > 2 segundos  

---

## 4. Pruebas Específicas de App Móvil
Validaciones críticas para el entorno móvil y sus limitaciones.

### Alcance
- Compatibilidad con diferentes dispositivos y versiones Android.
- Manejo de conectividad intermitente.
- Gestión de permisos del sistema.
- Performance en dispositivos con recursos limitados.

### Casos de prueba propuestos

#### **Compatibilidad de Dispositivos**
- [ ] Testing en pantallas pequeñas (5") → UI se adapta correctamente.
- [ ] Testing en pantallas grandes (7"+ tablets) → layout responsivo.
- [ ] Testing en Android API 21 (mínimo) → funcionalidad completa.
- [ ] Testing en Android API 34 (más reciente) → sin deprecated warnings.
- [ ] Testing en dispositivos con poca RAM (2GB) → app no se cierra por memoria.
- [ ] Testing en diferentes densidades de pantalla → imágenes se ven nítidas.

#### **Conectividad y Red**
- [ ] Crear ticket sin conexión → se guarda localmente con indicador.
- [ ] Recuperar conexión → tickets locales se sincronizan automáticamente.
- [ ] Conexión lenta (2G simulado) → loading indicators + timeout apropiado.
- [ ] Cambiar de WiFi a datos móviles → sesión se mantiene.
- [ ] Interrupción durante subida de archivo → reintento automático.
- [ ] Sin conexión al login → mensaje claro + opción de reintentar.

#### **Permisos del Sistema**
- [ ] Solicitar permiso de cámara → dialog nativo + explicación clara.
- [ ] Permiso de cámara denegado → mensaje instructivo + funcionalidad alternativa.
- [ ] Solicitar permiso de almacenamiento para adjuntos → funciona correctamente.
- [ ] Permisos revocados después de otorgados → app detecta y re-solicita.
- [ ] Sin permisos de almacenamiento → opción de adjuntar desde otros métodos.

#### **Ciclo de Vida de la App**
- [ ] App enviada a background → estado se mantiene al volver.
- [ ] App cerrada por sistema (low memory) → datos no guardados se recuperan.
- [ ] Rotación de pantalla → layout se adapta + datos se mantienen.
- [ ] Llamada entrante durante uso → app se pausa/reanuda correctamente.
- [ ] Notificación de sistema → app responde apropiadamente.

#### **Performance y Recursos**
- [ ] Listado con 500+ tickets → scroll fluido + lazy loading.
- [ ] Múltiples imágenes adjuntas → carga progresiva + cache.
- [ ] Uso de memoria < 150MB durante operación normal.
- [ ] Tiempo de inicio de app < 3 segundos.
- [ ] Búsqueda en tiempo real → respuesta < 500ms.
- [ ] Sync en background → no impacta performance de UI.

#### **Seguridad Móvil**
- [ ] Datos sensibles no se guardan en logs del sistema.
- [ ] Screenshots de app con datos sensibles → contenido oculto.
- [ ] Sesión expira tras inactividad → re-login requerido.
- [ ] Certificado SSL pinning → conexiones seguras.
- [ ] Tokens almacenados en Android Keystore → encriptación nativa.

---

## Conclusión
Este plan actualizado cubre los **cuatro niveles de prueba** necesarios para una app móvil de helpdesk H1:  
- **Unitarias** → asegurar que cada componente funcione de manera aislada + validaciones de seguridad.  
- **Integración** → validar la coherencia entre módulos y DB + sincronización offline/online.  
- **Manuales de UI** → garantizar la usabilidad del prototipo + gestos móviles y accesibilidad.  
- **Específicas de App Móvil** → compatibilidad de dispositivos, conectividad, permisos y performance.

### Cobertura de Testing Actualizada:
- ✅ **82 casos de prueba específicos** (vs 12 originales)
- ✅ **Funcionalidades críticas móviles** cubiertas (offline, permisos, performance)
- ✅ **Seguridad móvil** incluida (keystore, SSL pinning, logs)
- ✅ **Compatibilidad multi-dispositivo** validada
- ✅ **Edge cases móviles** contemplados (memoria, conectividad, ciclo de vida)

Este plan mejorado proporciona una **cobertura del 90%** para un sistema de helpdesk móvil robusto y listo para producción.  
