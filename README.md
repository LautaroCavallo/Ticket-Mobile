# Sistema de Gestión de Tickets

## Descripción
Este proyecto es un **sistema de gestión de tickets** desarrollado en el marco de la materia *Desarrollo de Aplicaciones I* (UADE, 2025).  
Permite a los usuarios reportar incidencias, a los agentes de soporte gestionarlas y a los administradores supervisar el proceso.

El sistema incluye:
- Creación, actualización y cierre de tickets.
- Clasificación por categorías (ej. Hardware, Software, Redes).
- Roles diferenciados de usuario (admin, soporte y usuario).
- Comentarios y seguimiento dentro de cada ticket.

---

##  Roles del Equipo
- **Product Owner / API Rest:** Lautaro Cavallo  
- **UX/UI:** Ivo Rubino  
- **Backend:** Tomás Liñeiro  
- **QA / DevOps:** Facundo Cores  

---

## Tecnologías
- **Frontend:** Android (Kotlin / XML / Material Design)  
- **Backend:** Node.js / Express (a definir)  
- **Base de datos:** PostgreSQL / MongoDB (según decisión final)  
- **API Rest:** OpenAPI 3.0 (Swagger)  
- **CI/CD:** GitHub Actions  

---

## Objetivos por Entregas
### H1 – Entrega Intermedia
- Figma con prototipo navegable  
- Flujo de pantallas definido  
- Repo inicializado con tablero de seguimiento (GitHub Projects)  
- DER inicial  
- Plan de pruebas (QA)  
- APK demo con pantallas mockeadas  
- Diagrama de arquitectura inicial  
- Swagger con endpoints iniciales (tickets, usuarios, categorías, comentarios)

### H2 – Entrega Final
- Feature set completo  
- Métricas de calidad y performance  
- APK Release Candidate  
- Documentación final (técnica y de usuario)  
- Demo lista para defensa  

---

## Organización del Repo
- `/docs` → Documentación del proyecto (arquitectura, DER, plan de pruebas, métricas, justificaciones).
- `/frontend` → Aplicación Android (Kotlin).
- `/backend` → Lógica de negocio, persistencia y tests.
- `/api` → Especificación OpenAPI (`swagger.yaml`) y ejemplos de requests/responses.
- `/config` → Configuración de herramientas (ej: `detekt-config.yml`, linters, formateadores).
- `/db` → Scripts de base de datos (`schema_h1.sql`, `seeds_h1.sql`, `queries_h1.sql`).
- `/scripts` → Scripts auxiliares para CI/CD o automatización (`setup-ci.sh`, etc.).
- `/.github/workflows` → Pipelines de integración y despliegue (ej: `ci.yml`).

---

## Instalación (a completar en H2)
1. Clonar el repositorio:  
   ```bash
   git clone https://github.com/tu-repo/tickets.git
   cd tickets
