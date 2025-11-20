package com.uade.ticket_mobile.data.api

/**
 * Configuración central de la API
 * 
 * IMPORTANTE: Cambia BASE_URL después de desplegar en Railway
 */
object ApiConfig {
    /**
     * URL base de la API
     * 
     * Para desarrollo local (emulador Android):
     *   "http://10.0.2.2:8000/api/"
     * 
     * Para producción (Railway):
     *   "https://tu-proyecto.railway.app/api/"
     *   
     * REEMPLAZA "tu-proyecto" con el dominio que te dará Railway
     */
    const val BASE_URL = "https://ticket-mobile-backend.onrender.com/api/" // ← CAMBIAR AQUÍ
    
    /**
     * Modo de desarrollo
     * true = muestra logs detallados
     * false = logs mínimos
     */
    const val DEBUG_MODE = true
}

