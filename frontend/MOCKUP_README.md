# 🎫 Ticket Mobile - Mockup Interactivo

Esta aplicación Android es un mockup completamente funcional de una aplicación de ticketera empresarial, desarrollada con Jetpack Compose y Kotlin.

## 🚀 Características Implementadas

### 🎨 **Interfaz de Usuario**
- ✅ Design System consistente con paleta de colores personalizada
- ✅ Material Design 3 con soporte para modo claro/oscuro
- ✅ Animaciones y transiciones fluidas
- ✅ Responsive design adaptable

### 🔐 **Sistema de Autenticación**
- ✅ **LoginScreen**: Autenticación con email y contraseña
- ✅ **RegisterScreen**: Registro de nuevos usuarios
- ✅ **ForgotPasswordScreen**: Recuperación de contraseña

### 🎫 **Gestión de Tickets**
- ✅ **TicketListScreen**: Lista con filtros (Pendientes, Completados, Cancelados)
- ✅ **CreateTicketScreen**: Creación de nuevos tickets
- ✅ Estados visuales con chips de colores
- ✅ Categorización por RRHH, IT, Administración, Mantenimiento

### 👤 **Gestión de Usuario**
- ✅ **ProfileScreen**: Visualización y edición de perfil
- ✅ **ChangePasswordScreen**: Cambio de contraseña
- ✅ Avatar circular con iniciales
- ✅ Información personal editable

### 🧭 **Navegación**
- ✅ Navegación completa entre todas las pantallas
- ✅ Menú hamburguesa con opciones
- ✅ FAB para crear tickets
- ✅ Navegación hacia atrás consistente

## 🔑 **Credenciales de Prueba**

| Email | Contraseña | Tipo |
|-------|------------|------|
| `usuario@test.com` | `123456` | Usuario normal |
| `admin@test.com` | `admin123` | Administrador |
| `test@test.com` | `test123` | Usuario de prueba |

## 📱 **Pantallas Implementadas**

### 🔐 **Autenticación**
1. **MockInfoScreen** - Información del demo con credenciales
2. **LoginScreen** - Pantalla de inicio de sesión
3. **RegisterScreen** - Registro de usuarios
4. **ForgotPasswordScreen** - Recuperación de contraseña

### 👤 **Usuario Normal**
5. **TicketListScreen** - Lista de tickets con filtros por estado
6. **CreateTicketScreen** - Creación de nuevos tickets
7. **ProfileScreen** - Perfil del usuario
8. **ChangePasswordScreen** - Cambio de contraseña

### 👨‍💼 **Administrador**
9. **AdminHomeScreen** - Dashboard principal con tickets sin asignar
10. **UserManagementScreen** - Gestión completa de usuarios
11. **StatisticsScreen** - Dashboard con estadísticas y gráficos
12. **TicketDetailsScreen** - Vista detallada con comentarios y archivos
13. **EditTicketScreen** - Edición completa de tickets

## 🛠 **Arquitectura Técnica**

### **Patrón MVVM**
- ✅ **ViewModel** con StateFlow para gestión reactiva del estado
- ✅ **Repository Pattern** con MockRepository para datos simulados
- ✅ **Separación de responsabilidades** clara

### **Datos Mock**
- ✅ **MockData**: Datos simulados de usuarios, tickets y categorías
- ✅ **MockTicketRepository**: Simulación completa de operaciones CRUD
- ✅ **8 tickets de ejemplo** con diferentes estados y prioridades
- ✅ **4 categorías** diferentes para clasificación

### **Funcionalidades Simuladas**
- ✅ Autenticación con validación de credenciales
- ✅ CRUD completo de tickets
- ✅ Filtrado por estado (Pendientes/Completados/Cancelados)
- ✅ Gestión de perfil de usuario
- ✅ Cambio de contraseña con validación
- ✅ Registro de nuevos usuarios
- ✅ Recuperación de contraseña

## 🎯 **Datos de Ejemplo**

### **Tickets Incluidos:**
1. "Password reset not working" (Abierto - Alta)
2. "Error en sistema de nóminas" (En Progreso - Urgente)
3. "Solicitud de vacaciones" (Resuelto - Baja)
4. "Problema con impresora" (Abierto - Media)
5. "Acceso al sistema ERP" (Cerrado - Alta)
6. "Actualización de datos" (Resuelto - Baja)
7. "Error en carga de documentos" (Cerrado - Alta)
8. "Mantenimiento aire acondicionado" (En Progreso - Baja)

### **Categorías:**
- 🏢 **RRHH** - Recursos Humanos
- 💻 **IT** - Tecnología
- 📋 **Administración** - Administración General
- 🔧 **Mantenimiento** - Mantenimiento de instalaciones

## 🚀 **Cómo Usar el Mockup**

1. **Ejecutar la aplicación** desde Android Studio
2. **Leer la información inicial** en la pantalla de bienvenida
3. **Usar las credenciales proporcionadas** para hacer login
4. **Explorar todas las funcionalidades**:
   - Navegar entre tickets con los tabs
   - Crear nuevos tickets
   - Gestionar perfil de usuario
   - Probar registro y recuperación de contraseña
   - Usar el menú hamburguesa

## 📋 **Notas Técnicas**

- **Sin Backend**: Todos los datos se manejan en memoria
- **Simulación Realista**: Delays simulados para emular llamadas de red
- **Estados Persistentes**: Los cambios se mantienen durante la sesión
- **Validaciones**: Formularios con validación en tiempo real
- **Compatibilidad**: Material Icons estándar para máxima compatibilidad

## 🎨 **Paleta de Colores**

- **Primary**: Azul #2E86AB
- **Secondary**: Verde #8BC34A
- **Accent**: Naranja #F18F01
- **Success**: Verde #8BC34A
- **Error**: Rojo #F44336
- **Warning**: Amarillo #FFEB3B

---

**Desarrollado con ❤️ usando Android Studio, Kotlin y Jetpack Compose**
