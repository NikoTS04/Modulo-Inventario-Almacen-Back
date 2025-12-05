# Despliegue en Railway - Backend

## Pasos para Desplegar

### 1. Crear Proyecto en Railway
1. Ve a https://railway.app
2. Clic en "New Project"
3. Selecciona "Deploy from GitHub repo"
4. Conecta tu repositorio: `Modulo-Inventario-Almacen-Back`

### 2. Agregar Base de Datos MySQL
1. En Railway, clic en "+ New" → "Database" → "Add MySQL"
2. Railway creará automáticamente una base de datos
3. Copia las credenciales que aparecen en la pestaña "Variables"

### 3. Configurar Variables de Entorno

En Railway, ve a tu servicio backend → pestaña **Variables** y agrega:

```bash
# Database (usar valores de Railway MySQL)
SPRING_DATASOURCE_URL=jdbc:mysql://MYSQL_HOST:MYSQL_PORT/MYSQL_DATABASE?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=MYSQL_USER
SPRING_DATASOURCE_PASSWORD=MYSQL_PASSWORD

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=none
SPRING_JPA_SHOW_SQL=false

# CORS - Agregar URL de Vercel cuando la tengas
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://tu-app-frontend.vercel.app

# Logging
LOGGING_LEVEL_ROOT=INFO
```

**Nota:** Railway tiene variables especiales para MySQL:
- `${{MySQL.MYSQL_URL}}` - URL completa de conexión
- `${{MySQL.MYSQL_HOST}}`, `${{MySQL.MYSQL_PORT}}`, etc.

Puedes usar directamente: `SPRING_DATASOURCE_URL=${{MySQL.MYSQL_URL}}`

### 4. Migrar Esquema de Base de Datos

Railway no ejecuta automáticamente scripts SQL. Opciones:

**Opción A - Railway CLI:**
```bash
railway login
railway link [project-id]
railway run mysql -h $MYSQL_HOST -u $MYSQL_USER -p$MYSQL_PASSWORD < database_schema.sql
```

**Opción B - MySQL Workbench / CLI local:**
1. Copia las credenciales de Railway
2. Conecta desde tu máquina:
   ```bash
   mysql -h HOST -P PORT -u USER -p DATABASE < database_schema.sql
   ```

**Opción C - Cambiar temporalmente `SPRING_JPA_HIBERNATE_DDL_AUTO=update`**
- Solo la primera vez para crear tablas
- Luego cambiar a `none`

### 5. Deploy

Railway hace deploy automático cuando detecta cambios en GitHub.

### 6. Obtener URL del Backend

1. En Railway, clic en tu servicio backend
2. Ve a "Settings" → "Domains"
3. Clic en "Generate Domain"
4. Copia la URL (ejemplo: `https://modulo-inventario-production.up.railway.app`)

### 7. Verificar Funcionamiento

```bash
# Health check
curl https://TU-URL.railway.app/api/v1/materials

# Ver logs en Railway
railway logs --follow
```

### 8. Actualizar CORS con URL de Vercel

Una vez desplegado el frontend en Vercel, actualiza la variable:

```bash
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://tu-app-frontend.vercel.app
```

## Troubleshooting

### Error de conexión a BD
- Verifica que las variables `SPRING_DATASOURCE_*` estén correctas
- Usa la URL completa con `?useSSL=false&serverTimezone=UTC`

### Error 403 CORS
- Verifica que `CORS_ALLOWED_ORIGINS` incluya la URL de Vercel
- Railway redeploys automáticamente al cambiar variables

### Logs
```bash
railway logs
```

## Costos

Railway ofrece:
- **$5 USD de crédito mensual gratis**
- Suficiente para proyectos académicos/demo
- No requiere tarjeta de crédito inicialmente
